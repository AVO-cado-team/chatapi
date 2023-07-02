FROM gradle:8-jdk17-alpine


# Copy the project
WORKDIR /chatapi
COPY chatapi /chatapi

# Run the project
CMD ["java", "-jar", "build/libs/chatapi-0.0.1-SNAPSHOT.jar"]