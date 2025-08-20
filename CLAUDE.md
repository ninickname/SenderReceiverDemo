# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a complete microservices architecture with Kafka message broker, featuring:
- **Sender Service**: Spring Boot web application that publishes messages to Kafka
- **Receiver Service**: Spring Boot consumer that reads from Kafka and stores in PostgreSQL with scheduled processing
- **Kafka**: Modern KRaft-mode message broker (no Zookeeper required)
- **PostgreSQL**: Persistent database with local storage
- **Full Kubernetes Deployment**: Production-ready manifests for all services

## Architecture Flow

1. **Sender Service** (Port 8080): Web form + REST API → Publishes to Kafka topic "sender-messages"
2. **Kafka**: Message broker with topic "sender-messages" 
3. **Receiver Service** (Port 8081): Consumes from Kafka → Saves to PostgreSQL + Scheduled processing every 10 seconds
4. **PostgreSQL**: Persistent storage with volume mount at /tmp/postgres-data

## Project Structure

- **Root Directory**: Parent folder for multiple Spring Boot services
- **sender-service/**: Spring Boot producer service
  - Maven project with Spring Boot 3.4.0 and Java 24
  - Kafka producer configuration and templates
  - Web UI with Thymeleaf templates
  - REST API endpoints for health checks and message sending
  - Docker containerization and Kubernetes manifests
- **receiver-service/**: Spring Boot consumer service
  - Maven project with Spring Boot 3.4.0 and Java 24
  - Kafka consumer with @KafkaListener
  - JPA entities and PostgreSQL integration
  - Scheduled tasks every 10 seconds for message processing
  - REST API for viewing stored messages and stats
- **k8s/**: Infrastructure manifests
  - PostgreSQL with persistent volume
  - Kafka broker in KRaft mode
  - Deployment orchestration scripts

## Current Technology Stack

- **Java**: 24 (Eclipse Temurin)
- **Spring Boot**: 3.4.0
- **Build Tool**: Maven
- **Message Broker**: Apache Kafka 3.8.1 in KRaft mode (Zookeeper-free)
- **Database**: PostgreSQL 15 with persistent volume storage
- **Web Framework**: Spring MVC with Thymeleaf (sender only)
- **Data Access**: Spring Data JPA with Hibernate
- **Development**: Spring Boot DevTools for hot reload
- **Containerization**: Docker with openjdk:24-jdk-slim
- **Orchestration**: Kubernetes with nginx ingress controller
- **Monitoring**: Spring Actuator + Prometheus metrics
- **Scheduling**: Spring @Scheduled tasks (10-second intervals)

## Development Workflows

### Local Development with Hot Reload (Recommended)
```bash
# Ensure Java 24 is active
export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-24.0.2.12-hotspot"

# Start sender service with DevTools
cd sender-service
mvn spring-boot:run
# Access: http://localhost:8080

# Start receiver service with DevTools (separate terminal)
cd receiver-service  
mvn spring-boot:run
# Access: http://localhost:8081/api/receiver/messages
```

### Full Build and Package
```bash
# Build both services
./build-and-deploy.sh

# Or manually:
cd sender-service && mvn clean package && docker build -t sender-service:latest .
cd receiver-service && mvn clean package && docker build -t receiver-service:latest .
```

### Kubernetes Deployment
```bash
# Deploy entire microservices architecture
./build-and-deploy.sh

# Or step by step:
kubectl apply -f k8s/postgresql.yaml
kubectl apply -f k8s/kafka.yaml
kubectl apply -f sender-service/k8s/
kubectl apply -f receiver-service/k8s/

# Test the message flow
./test-flow.sh
```

### Kubernetes Operations
```bash
# Check all pods
kubectl get pods

# Check logs for each service
kubectl logs -l app=sender-service
kubectl logs -l app=receiver-service
kubectl logs -l app=kafka
kubectl logs -l app=postgres

# Port forwarding for testing
kubectl port-forward service/sender-service 8080:80
kubectl port-forward service/receiver-service 8081:80
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
- we use java 24