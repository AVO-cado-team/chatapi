# COLORED OUTPUT XD
ccinfo 	= $(shell tput setaf 6)
ccwarn 	= $(shell tput setaf 3)
ccerror = $(shell tput setaf 1)
ccok 	= $(shell tput setaf 2)
ccreset = $(shell tput sgr0)
INFO 	= $(ccinfo)[INFO] |$(ccreset)
WARN 	= $(ccwarn)[WARN] |$(ccreset)
ERROR 	= $(ccerror)[ERROR]|$(ccreset)
OK 		= $(ccok)[OK]   |$(ccreset)


DOCKER_COMPOSE_ARGS = -f ./ci/build/docker-compose.yml
APP_DOCKER_FILE = ./ci/build/Dockerfile
APP_DEV_DOCKER_FILE = ./ci/build/dev.Dockerfile
APP_DOTENV_FILE = ./ci/build/.env
APP_DEFAULT_DOTENV_FILE = ./ci/build/.default.env
APP_VERSION = 0.0.1
APP_DOCKER_IMAGE = chatapi-image
APP_DOCKER_CONTAINER = chatapi
APP_BUILD_INFO_PROPERTIES = ./chatapi/src/main/resources/build-info.properties
DB_DOCER_CONTAINER = postgres
DB_EXTERNAL_PORT = 5433

# Variables for create-build-info-properties
old_commit_sha = $(shell grep -oP '(?<=git.commit.sha=).*' ${APP_BUILD_INFO_PROPERTIES} 2> /dev/null || echo "not_found")
new_commit_sha = $(shell git rev-parse HEAD)
new_eq_old_commit_sha = $(shell [ "$(old_commit_sha)" = "$(new_commit_sha)" ] && echo "true" || echo "false")

.download-wait-for-it:
	@echo "${INFO} Downloading wait-for-it.sh"
	@if [ ! -f wait-for-it.sh ]; then \
		curl -o wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh; \
		chmod +x wait-for-it.sh; \
	fi

.wait-for-db: .download-wait-for-it
	@echo "${INFO} Waiting for database"
	@./wait-for-it.sh -t 0 0.0.0.0:$(DB_EXTERNAL_PORT)

.create-build-info-properties:
	@echo "${INFO} Creating build-info.properties"

	@if [ "$(new_eq_old_commit_sha)" == "false" ]; \
	then \
		echo "${INFO} New commit sha found"; \
		echo "# DO NOT EDIT THIS FILE MANUALLY" > $(APP_BUILD_INFO_PROPERTIES); \
		echo "# This file is generated by Makefile" >> $(APP_BUILD_INFO_PROPERTIES); \
		echo "version=$(APP_VERSION)" >> $(APP_BUILD_INFO_PROPERTIES); \
		echo "build.time=$(shell date +%Y-%m-%dT%H:%M:%S%z)" >> $(APP_BUILD_INFO_PROPERTIES); \
		echo "git.commit.sha=$(shell git rev-parse HEAD)" >> $(APP_BUILD_INFO_PROPERTIES); \
	else \
		echo "${INFO} Build info properties already created"; \
	fi

.exists-check:
	@if [ ! -f $(APP_DOTENV_FILE) ]; then \
		echo "${WARN} .env file not found!"; \
		echo "${WARN} Copy .default.env to .env"; \
		exit 1; \
	fi

.up-db:
	@echo "${INFO} Starting database"
	@docker-compose $(DOCKER_COMPOSE_ARGS) up -d $(DB_DOCER_CONTAINER)

.stop-db:
	@echo "${INFO} Stopping database"
	@docker-compose $(DOCKER_COMPOSE_ARGS) stop $(DB_DOCER_CONTAINER)

build-app: .create-build-info-properties
	@echo "${INFO} Building app"
	@docker build -t $(APP_DOCKER_IMAGE) -f $(APP_DOCKER_FILE) .

dev-build-app: .create-build-info-properties
	@echo "${INFO} Building app for development"
	@chmod +x ./chatapi/gradlew
	@./chatapi/gradlew clean build -p ./chatapi
	@docker build -t $(APP_DOCKER_IMAGE) -f $(APP_DEV_DOCKER_FILE) .

up-app: .wait-for-db
	@echo "${INFO} Starting app"
	@docker-compose $(DOCKER_COMPOSE_ARGS) up -d $(APP_DOCKER_CONTAINER)

stop-app:
	@echo "${INFO} Stopping app"
	@docker-compose $(DOCKER_COMPOSE_ARGS) stop $(APP_DOCKER_CONTAINER)

logs-app:
	@echo "${INFO} Showing app logs"
	@docker-compose $(DOCKER_COMPOSE_ARGS) logs -f $(APP_DOCKER_CONTAINER)

up: .exists-check .up-db build-app up-app logs-app
stop: stop-app .stop-db
dev-up: .exists-check .up-db dev-build-app up-app logs-app