# ChatAPI
ChatAPI is a REST and WebSocket API for a chat application.

## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Project structure](#project-structure)

## General info
Our project is a full-fledged backend for chat rooms. 
CI for code formatting is configured in the project. 
The project is built with a Makefile that calls commands in the right order. 
The final result is a Docker image. For convenience, we have created a Docker Compose that runs the database and Simple File Storage.  

## Technologies
The project uses technologies such as:
- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- STOMP
- Lombok
- Docker
- GitHub Workflows
- Makefile
- Java JWT
- PostgreSQL
- Gradle

The application architecture is written in DDD-like style.
Data access pattern: Repository.

## Project structure
```
chatapi/src/main/java/sk/avo/chatapi
├── App.java
├── application
│  ├── ApplicationService.java
│  ├── dto
│  │  └── TokenPair.java
│  ├── exceptions
│  │  ├── BaseApplicationException.java
│  │  └── SubscriptionPathNotFoundException.java
│  └── impl
│     ├── ChatServiceImpl.java
│     ├── JwtTokenServiceImpl.java
│     ├── RoomServiceImpl.java
│     └── UserServiceImpl.java
├── config
│  ├── Application.java
│  ├── LoggingConfig.java
│  ├── OpenApi30Config.java
│  ├── SecurityConfig.java
│  └── WebSocketConfig.java
├── domain
│  ├── model
│  │  ├── chat
│  │  │  ├── BaseChatException.java
│  │  │  ├── ChatEntity.java
│  │  │  ├── ChatId.java
│  │  │  ├── ChatNotFoundException.java
│  │  │  ├── MessageEntity.java
│  │  │  ├── MessageEntityId.java
│  │  │  ├── MessageId.java
│  │  │  ├── MessageNotFoundException.java
│  │  │  ├── MessageType.java
│  │  │  ├── UserIsAlreadyInTheChatException.java
│  │  │  └── UserIsNotInTheChatException.java
│  │  ├── filestorage
│  │  │  └── FileNotFoundException.java
│  │  ├── security
│  │  │  ├── BaseSecurityException.java
│  │  │  ├── InvalidTokenException.java
│  │  │  └── TokenType.java
│  │  └── user
│  │     ├── BaseUserException.java
│  │     ├── UserAlreadyExistsException.java
│  │     ├── UserEmailIsAlreadyVerifiedException.java
│  │     ├── UserEmailVerifyException.java
│  │     ├── UserEntity.java
│  │     ├── UserId.java
│  │     ├── UserIsNotVerifiedException.java
│  │     └── UserNotFoundException.java
│  ├── repository
│  │  ├── ChatRepo.java
│  │  ├── MessageRepo.java
│  │  ├── UserRepo.java
│  │  └── VerifyEmailRepo.java
│  ├── service
│  │  ├── ChatService.java
│  │  ├── FileStorageService.java
│  │  ├── JwtTokenService.java
│  │  ├── RoomService.java
│  │  └── UserService.java
│  └── shared
│     ├── BaseId.java
│     └── Tuple.java
├── infrastructure
│  ├── cache
│  │  └── verifyemail
│  │     ├── CacheVerifyEmailRepo.java
│  │     ├── models
│  │     │  └── Email.java
│  │     └── utils
│  │        └── RandomCode.java
│  └── rest
│     └── filestorage
│        ├── FileStorageServiceImpl.java
│        └── models
│           └── File.java
├── presentation
│  ├── controller
│  │  ├── Auth.java
│  │  ├── BuildInfo.java
│  │  └── Chat.java
│  ├── dto
│  │  ├── auth
│  │  │  ├── LoginRequest.java
│  │  │  ├── LoginResponse.java
│  │  │  ├── RefreshRequest.java
│  │  │  ├── RefreshResponse.java
│  │  │  ├── ResendEmailRequest.java
│  │  │  ├── ResendEmailResponse.java
│  │  │  ├── SignupRequest.java
│  │  │  ├── SignupResponse.java
│  │  │  └── VerifyEmailRequest.java
│  │  ├── chat
│  │  │  ├── AddUserToChatRequest.java
│  │  │  ├── ChatAddMessage.java
│  │  │  ├── ChatDetails.java
│  │  │  ├── ChatUser.java
│  │  │  ├── CreateChatRequest.java
│  │  │  └── NewMessageRequest.java
│  │  ├── dev
│  │  │  └── BuildInfoResponse.java
│  │  └── exceptionsresolver
│  │     └── ErrorResponse.java
│  └── resolver
│     └── ErrorResolver.java
└── security
   ├── JwtRequestFilter.java
   └── shared
      └── UserRoles.java
```

### Application
The application layer contains the ApplicationService.
ApplicationService is responsible for the highest level logic, combining domain service calls.

### Config
Contains the configuration of the application.

### Domain
The domain layer contains the domain model, repositories, and services.

### Infrastructure
The infrastructure layer contains the implementation of the repositories and services.

### Presentation
The presentation layer contains the controllers and DTOs.

### Security
The security contains the security configuration and the JWT filter.
