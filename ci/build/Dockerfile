FROM gradle:8-jdk17-alpine


# Copy the project
WORKDIR /chatapi
COPY chatapi /chatapi

# Build the project
RUN gradle build -x test

# Run the project
#CMD ["gradle", "bootRun"]
CMD ["java", "-jar", "build/libs/chatapi-0.0.1-SNAPSHOT.jar"]