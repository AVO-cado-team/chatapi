FROM gradle:8-jdk17-alpine

WORKDIR /chatapi
COPY chatapi /chatapi

RUN ["gradle", "clean", "test", "--info", "--warning-mode", "all" ]

CMD ["java", "-jar", "build/libs/chatapi-0.0.1-SNAPSHOT.jar"]
