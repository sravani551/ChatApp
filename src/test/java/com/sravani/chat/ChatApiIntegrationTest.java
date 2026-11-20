package com.sravani.chat;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChatApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    private long signup(String username) throws Exception {
        String body = mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"s3cret-pass\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(not(containsString("password"))))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("id").asLong();
    }

    private long startChat(long a, long b) throws Exception {
        String body = mvc.perform(post("/api/chats").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + a + ",\"otherUserId\":" + b + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("id").asLong();
    }

    private void send(long chatId, long senderId, String text) throws Exception {
        mvc.perform(post("/api/chats/{id}/messages", chatId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderId\":" + senderId + ",\"content\":\"" + text + "\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void signupAndLogin() throws Exception {
        signup("alice");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"s3cret-pass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void wrongPasswordAndUnknownUserGiveSame401() throws Exception {
        signup("alice");
        for (String creds : new String[] {
                "{\"username\":\"alice\",\"password\":\"wrong-pass\"}",
                "{\"username\":\"nobody\",\"password\":\"whatever1\"}"}) {
            mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(creds))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.detail").value("Invalid username or password"));
        }
    }

    @Test
    void duplicateUsernameIsConflict() throws Exception {
        signup("alice");
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"alice\",\"password\":\"another-pass\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidSignupIsBadRequestWithFieldErrors() throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"a\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("password")))
                .andExpect(jsonPath("$.detail").value(containsString("username")));
    }

    @Test
    void chatIsTheSameWhoeverStartsIt() throws Exception {
        long alice = signup("alice");
        long bob = signup("bob");
        startChat(alice, bob);
        mvc.perform(post("/api/chats").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":" + bob + ",\"otherUserId\":" + alice + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    void readingMarksOnlyReceivedMessagesAsRead() throws Exception {
        long alice = signup("alice");
        long bob = signup("bob");
        long chat = startChat(alice, bob);
        send(chat, alice, "hi bob");
        send(chat, alice, "are you there?");
        send(chat, bob, "hi alice");

        mvc.perform(get("/api/users/{id}/unread-count", bob)).andExpect(jsonPath("$.unread").value(2));
        mvc.perform(get("/api/users/{id}/unread-count", alice)).andExpect(jsonPath("$.unread").value(1));

        // Bob opens the chat: Alice's messages become read, Bob's own message does not.
        mvc.perform(get("/api/chats/{id}/messages", chat).param("readerId", String.valueOf(bob)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].content").value("hi bob"));

        mvc.perform(get("/api/users/{id}/unread-count", bob)).andExpect(jsonPath("$.unread").value(0));
        mvc.perform(get("/api/users/{id}/unread-count", alice)).andExpect(jsonPath("$.unread").value(1));
    }

    @Test
    void outsiderCannotSendOrRead() throws Exception {
        long alice = signup("alice");
        long bob = signup("bob");
        long eve = signup("eve");
        long chat = startChat(alice, bob);

        mvc.perform(post("/api/chats/{id}/messages", chat).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderId\":" + eve + ",\"content\":\"hello\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/chats/{id}/messages", chat).param("readerId", String.valueOf(eve)))
                .andExpect(status().isForbidden());
    }

    @Test
    void missingResourcesAre404AndDeleteWorks() throws Exception {
        long alice = signup("alice");
        long bob = signup("bob");
        long chat = startChat(alice, bob);
        send(chat, alice, "bye");

        mvc.perform(delete("/api/chats/{id}", chat)).andExpect(status().isNoContent());
        mvc.perform(get("/api/chats/{id}/messages", chat).param("readerId", String.valueOf(alice)))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/users/{id}", 999)).andExpect(status().isNotFound());

        JsonNode chats = json.readTree(mvc.perform(get("/api/users/{id}/chats", alice))
                .andReturn().getResponse().getContentAsString());
        org.assertj.core.api.Assertions.assertThat(chats).isEmpty();
    }
}
