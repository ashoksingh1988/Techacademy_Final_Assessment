# Dockerfile for Java Appium Automation Framework
# Author: Asim Kumar Singh
# Purpose: Containerized execution for CI/CD pipelines

FROM openjdk:11-jdk-slim

# Set environment variables
ENV MAVEN_VERSION=3.8.6
ENV MAVEN_HOME=/opt/maven
ENV PATH=$MAVEN_HOME/bin:$PATH
ENV APPIUM_VERSION=2.0.0
ENV NODE_VERSION=18

# Install system dependencies
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    git \
    android-tools-adb \
    && rm -rf /var/lib/apt/lists/*

# Install Node.js
RUN curl -fsSL https://deb.nodesource.com/setup_${NODE_VERSION}.x | bash - \
    && apt-get install -y nodejs

# Install Maven
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt \
    && mv /opt/apache-maven-${MAVEN_VERSION} /opt/maven \
    && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Install Appium
RUN npm install -g appium@${APPIUM_VERSION} \
    && npm install -g appium-doctor \
    && appium driver install uiautomator2

# Create application directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Download dependencies
RUN mvn dependency:resolve

# Create directories for reports and logs
RUN mkdir -p /app/reports /app/logs /app/screenshots

# Set permissions
RUN chmod +x /app

# Expose Appium port
EXPOSE 4723

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:4723/status || exit 1

# Default command
CMD ["mvn", "test", "-Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke-tests.xml"]
