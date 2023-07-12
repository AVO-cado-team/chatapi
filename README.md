# ChatAPI
ChatAPI is a REST and WebSocket API for a chat application.

## Table of contents
* [How to run](#how-to-run)
* [General info](#general-info)
* [Features](#features)
* [Technologies](#technologies)

## How to run
### Prerequisites
- Docker
- Docker Compose
- Make

### Run
1. Clone the repository
2. Copy the `.env.default` file to `.env` and fill in the variables
3. Run `make up` to build the project

## General info
Our project is a full-fledged backend for chat rooms. 
CI for code formatting is configured in the project. 
The project is built with a Makefile that calls commands in the right order. 
The final result is a Docker image. For convenience, we have created a Docker Compose that runs the database and Simple File Storage.  

## Features
### Security
- Our app supports a verification system for Email.
- JWT is used for authentication.
- Access and refresh tokens are used.
- The app supports the following roles: USER_VERIFIED, USER_UNVERIFIED

### API
- REST API is documented using OpenAPI 3.0.
- We use the STOMP protocol for WebSocket communication.

### External services
- We use Simple File Storage for storing files.

### CI
- We use GitHub Workflows for CI.

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
