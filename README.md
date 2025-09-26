🐳 Docker Example with Java Spring Boot

Docker commands 

docker images                # List all images on your system
docker build -t name:tag .   # Build an image from Dockerfile in current dir
docker rmi image_id          # Remove an image
docker image prune           # Remove unused images

Docker Workflow
1.Write application code.
build project jar 
->mvn clean install

2.Create a Dockerfile (instructions for building an image).
Create a file named Dockerfile in project root:
# Use Java base image
FROM amazoncorretto:17

# Set working directory
WORKDIR /app

# Copy JAR file into container
COPY target/docker-demo-image-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

3.Build the Docker image.
docker build -t docker-demo-image:tagname .

4.Run the image as a container.
docker run -p 8080:8080 docker-demo-image:tagname:

5.Push the image to Docker Hub.
step 1:->docker login
step 2:->docker tag docker-image-demo:tagname your-dockerhub-username/docker-demo-image:tagname
step 3:->docker push your-dockerhub-username/docker-demo-image:tagname


6.Pull the image on another machine/environment.
docker pull your-dockerhub-username/docker-demo-image:tagname
docker run -p 8080:8080 your-dockerhub-username/docker-demo-image:tagname

7.Run the container anywhere.
using this process of any application can  containerized, published to Docker Hub, and runnable anywhere.

Commom docker commands
docker images                        # List images
docker build -t name:tag .           # Build image from Dockerfile
docker run -p 8080:8080 name:tag     # Run container (map ports)
docker tag name:tag user/repo:tag    # Tag image for Docker Hub
docker push user/repo:tag            # Push image to Docker Hub
docker pull user/repo:tag            # Pull image from Docker Hub
docker ps                            # List running containers
