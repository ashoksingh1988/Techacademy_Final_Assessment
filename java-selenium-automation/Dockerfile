# Dockerfile for Java Selenium Automation Framework
# Author: Asim Kumar Singh
# Purpose: Containerized web automation testing

FROM selenium/standalone-chrome:latest

# Switch to root user for installations
USER root

# Install Java 11 and Maven
RUN apt-get update && apt-get install -y \
    openjdk-11-jdk \
    maven \
    wget \
    curl \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Set Java environment
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Set Maven environment
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

# Create application directory
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Download dependencies
RUN mvn dependency:resolve

# Create directories for reports and logs
RUN mkdir -p /app/reports /app/logs /app/screenshots

# Download WebDriver binaries
RUN mkdir -p /app/drivers && \
    # ChromeDriver
    wget -O /app/drivers/chromedriver.zip https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip && \
    unzip /app/drivers/chromedriver.zip -d /app/drivers/ && \
    chmod +x /app/drivers/chromedriver && \
    # GeckoDriver
    wget -O /app/drivers/geckodriver.tar.gz https://github.com/mozilla/geckodriver/releases/download/v0.33.0/geckodriver-v0.33.0-linux64.tar.gz && \
    tar -xzf /app/drivers/geckodriver.tar.gz -C /app/drivers/ && \
    chmod +x /app/drivers/geckodriver && \
    # Cleanup
    rm /app/drivers/*.zip /app/drivers/*.tar.gz

# Set WebDriver paths
ENV WEBDRIVER_CHROME_DRIVER=/app/drivers/chromedriver
ENV WEBDRIVER_GECKO_DRIVER=/app/drivers/geckodriver

# Set display for headless mode
ENV DISPLAY=:99

# Expose ports
EXPOSE 4444 5900

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:4444/status || exit 1

# Switch back to seluser
USER seluser

# Default command
CMD ["mvn", "test", "-Dsurefire.suiteXmlFiles=src/test/resources/suites/smoke-tests.xml", "-Dbrowser=chrome", "-Dheadless=true"]
