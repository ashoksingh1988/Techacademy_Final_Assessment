# Dockerfile for Python Selenium Automation Framework
# Author: Asim Kumar Singh
# Purpose: Containerized Python web automation testing

FROM python:3.9-slim

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV DISPLAY=:99
ENV DEBIAN_FRONTEND=noninteractive

# Install system dependencies
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    gnupg \
    software-properties-common \
    xvfb \
    && rm -rf /var/lib/apt/lists/*

# Install Chrome
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Install Firefox
RUN apt-get update && apt-get install -y firefox-esr \
    && rm -rf /var/lib/apt/lists/*

# Create application directory
WORKDIR /app

# Copy requirements first for better caching
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir --upgrade pip \
    && pip install --no-cache-dir -r requirements.txt \
    && pip install --no-cache-dir pytest-html pytest-xdist pytest-cov allure-pytest

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
ENV PATH="/app/drivers:${PATH}"

# Copy project files
COPY . .

# Create directories for reports and logs
RUN mkdir -p /app/reports /app/logs /app/screenshots

# Set permissions
RUN chmod -R 755 /app

# Create a non-root user
RUN useradd -m -u 1000 testuser && chown -R testuser:testuser /app
USER testuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD python -c "import selenium; print('Selenium is working')" || exit 1

# Default command
CMD ["pytest", "tests/smoke/", "-v", "--html=reports/test-report.html", "--self-contained-html"]
