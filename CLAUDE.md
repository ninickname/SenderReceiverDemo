# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a multi-service Spring Boot project designed for Kubernetes deployment, featuring a modern microservices architecture with web interface and container orchestration.

## Project Structure

- **Root Directory**: Parent folder for multiple Spring Boot services
- **sender-service/**: Complete Spring Boot application with web interface
  - Maven project with Spring Boot 3.4.0 and Java 24
  - Web UI with Thymeleaf templates showing Java version
  - REST API endpoints for health checks and message sending
  - Spring Actuator with Prometheus metrics
  - Docker containerization with Java 24 runtime
  - Complete Kubernetes manifests (deployment, service, ingress)

## Current Technology Stack

- **Java**: 24 (Eclipse Temurin)
- **Spring Boot**: 3.4.0
- **Build Tool**: Maven
- **Web Framework**: Spring MVC with Thymeleaf
- **Development**: Spring Boot DevTools for hot reload
- **Containerization**: Docker with openjdk:24-jdk-slim
- **Orchestration**: Kubernetes with nginx ingress controller
- **Monitoring**: Spring Actuator + Prometheus metrics

## Development Workflows

### Local Development with Hot Reload (Recommended)
```bash
cd sender-service
# Ensure Java 24 is active
export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-24.0.2.12-hotspot"

# Start with DevTools (auto-restart on code changes)
mvn spring-boot:run
# Access: http://localhost:8080
# Changes to Java/HTML/Properties auto-restart the app
```

### Full Build and Package
```bash
cd sender-service
mvn clean package
docker build -t sender-service:latest .
```

### Kubernetes Operations
```bash
# Deploy all resources
kubectl apply -f sender-service/k8s/

# Update deployment with new image
kubectl rollout restart deployment sender-service

# Check deployment status
kubectl get pods -l app=sender-service
kubectl logs -l app=sender-service

# Port forwarding for testing
kubectl port-forward service/sender-service 8080:80
```

## Access Methods

### Development (Port Forward)
```bash
kubectl port-forward service/sender-service 8080:80
# Access: http://localhost:8080
```

### Production (Ingress)
- **Hostname**: sender-service.local
- **Setup**: Add `127.0.0.1 sender-service.local` to hosts file
- **Access**: http://sender-service.local

## Application Features

### Web Interface
- **Welcome Page**: Displays Java version and input form
- **Message Logging**: Form submissions are logged to application logs
- **Health Checks**: Built-in endpoints for Kubernetes probes

### API Endpoints
- `GET /` - Web interface
- `POST /submit` - Form submission
- `GET /api/sender/health` - Health check
- `POST /api/sender/send` - REST API for messages
- `GET /actuator/health` - Kubernetes health probe
- `GET /actuator/prometheus` - Metrics endpoint

## Architecture Benefits

- **Production Ready**: ClusterIP service with ingress for external access
- **Scalable**: Ready for multiple replicas with load balancing
- **Observable**: Comprehensive logging and metrics
- **Modern**: Latest Java 24 with Spring Boot 3.4.0
- **Cloud Native**: Kubernetes-native deployment with health checks

## Development Tools

### Spring Boot DevTools
- **Auto-restart**: Application restarts automatically when code changes
- **Live reload**: Browser refreshes automatically after changes
- **Fast development**: ~2-3 second restart time vs full rebuild
- **Triggers**: Changes to `.java`, `.html`, `.properties` files
- **IDE Setup**: Enable "Build project automatically" in IntelliJ IDEA

### Typical Development Flow
1. Run `mvn spring-boot:run` locally with DevTools
2. Edit Java code, HTML templates, or properties
3. Save files (Ctrl+S)
4. App auto-restarts in seconds
5. Browser auto-refreshes
6. See changes immediately

### When to Use Each Approach
- **Local Development**: Use `mvn spring-boot:run` with DevTools for rapid iteration
- **Integration Testing**: Deploy to Kubernetes to test with full stack
- **Production**: DevTools automatically disabled in packaged applications

## Development Notes

- **Java Version**: Requires Java 24 for building and running
- **Spring Boot Version**: 3.4.0 supports Java 24 (earlier versions don't)
- **Docker Base**: Uses openjdk:24-jdk-slim for container runtime
- **Service Type**: ClusterIP for production readiness with ingress
- **Replicas**: Currently set to 1, easily scalable to multiple instances
- **DevTools**: Included for development, automatically disabled in production