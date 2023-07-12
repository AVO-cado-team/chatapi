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
TEST_APP_DEV_DOCKER_FILE = ./ci/build/test.Dockerfile
APP_DOTENV_FILE = ./ci/build/.env
APP_DEFAULT_DOTENV_FILE = ./ci/build/.default.env
APP_VERSION = 0.0.1
APP_DOCKER_IMAGE = chatapi-image
TEST_APP_DOCKER_IMAGE = chatapi-test-image
APP_DOCKER_CONTAINER = chatapi
TEST_APP_DOCKER_CONTAINER = chatapi-test
APP_BUILD_INFO_PROPERTIES = ./chatapi/src/main/resources/build-info.properties
DB_DOCKER_CONTAINER = postgres
DB_EXTERNAL_PORT = 5433
SIMPLE_FILE_STORAGE_DOCKER_CONTAINER = simple-file-storage

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
	@echo "# DO NOT EDIT THIS FILE MANUALLY" > $(APP_BUILD_INFO_PROPERTIES);
	@echo "# This file is generated by Makefile" >> $(APP_BUILD_INFO_PROPERTIES);
	@echo "version=$(APP_VERSION)" >> $(APP_BUILD_INFO_PROPERTIES);
	@echo "build.time=$(shell date +%Y-%m-%dT%H:%M:%S%z)" >> $(APP_BUILD_INFO_PROPERTIES);
	@echo "git.commit.sha=$(shell git rev-parse HEAD)" >> $(APP_BUILD_INFO_PROPERTIES);
	@echo "git.branch=$(shell git rev-parse --abbrev-ref HEAD)" >> $(APP_BUILD_INFO_PROPERTIES);

.exists-check:
	@if [ ! -f $(APP_DOTENV_FILE) ]; then \
		echo "${WARN} .env file not found!"; \
		echo "${WARN} Copy .default.env to .env"; \
		exit 1; \
	fi

.up-db:
	@echo "${INFO} Starting database"
	@docker-compose $(DOCKER_COMPOSE_ARGS) up -d $(DB_DOCKER_CONTAINER)

.up-simple-file-storage:
	@echo "${INFO} Starting simple file storage"
	@docker-compose $(DOCKER_COMPOSE_ARGS) up -d $(SIMPLE_FILE_STORAGE_DOCKER_CONTAINER)

.stop-db:
	@echo "${INFO} Stopping database"
	@docker-compose $(DOCKER_COMPOSE_ARGS) stop $(DB_DOCKER_CONTAINER)

build-app: .create-build-info-properties
	@echo "${INFO} Building app"
	@docker build -t $(APP_DOCKER_IMAGE) -f $(APP_DOCKER_FILE) .

build-test-app: .create-build-info-properties
	@echo "${INFO} Building test app"
	@docker build -t $(TEST_APP_DOCKER_IMAGE) -f $(TEST_APP_DEV_DOCKER_FILE) .

dev-build-app: .create-build-info-properties
	@echo "${INFO} Building app for development"
	@chmod +x ./chatapi/gradlew
	@./chatapi/gradlew clean build -p ./chatapi
	@docker build -t $(APP_DOCKER_IMAGE) -f $(APP_DEV_DOCKER_FILE) .

up-app: .wait-for-db
	@echo "${INFO} Starting app"
	@docker-compose $(DOCKER_COMPOSE_ARGS) up -d $(APP_DOCKER_CONTAINER)

up-test-app: .wait-for-db
	@echo "${INFO} Starting test app"
	@docker-compose $(DOCKER_COMPOSE_ARGS) up -d $(TEST_APP_DOCKER_CONTAINER)

stop-app:
	@echo "${INFO} Stopping app"
	@docker-compose $(DOCKER_COMPOSE_ARGS) stop $(APP_DOCKER_CONTAINER)
	@docker-compose $(DOCKER_COMPOSE_ARGS) stop $(TEST_APP_DOCKER_CONTAINER)

logs-app:
	@echo "${INFO} Showing app logs"
	@docker-compose $(DOCKER_COMPOSE_ARGS) logs -f $(APP_DOCKER_CONTAINER)

up: .exists-check .up-db .up-simple-file-storage build-app up-app logs-app
up-test: .exists-check .up-db .up-simple-file-storage build-test-app up-test-app logs-app
stop: stop-app .stop-db
dev-up: .exists-check .up-db .up-simple-file-storage dev-build-app up-app logs-app