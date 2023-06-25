FROM gradle:8-jdk17-alpine

# Install required packages
RUN apk add --no-cache \
    bash \
    curl \
    git \
    openssh-client \
    openssl \
    tar \
    unzip \
    wget \
    zip \
    make \
    && rm -rf /var/cache/apk/*

# Copy the project
WORKDIR /chatapi
COPY chatapi /chatapi

# Build the project
RUN gradle build

# Run the project
CMD ["gradle", "bootRun"]