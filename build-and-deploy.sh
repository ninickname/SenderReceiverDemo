#!/bin/bash

echo "Building and deploying microservices architecture..."

# Build sender service
echo "Building sender-service..."
cd sender-service
mvn clean package -DskipTests
docker build -t sender-service:latest .
cd ..

# Build receiver service  
echo "Building receiver-service..."
cd receiver-service
mvn clean package -DskipTests
docker build -t receiver-service:latest .
cd ..

# Deploy infrastructure (PostgreSQL, Kafka)
echo "Deploying infrastructure..."
kubectl apply -f k8s/postgresql.yaml
kubectl apply -f k8s/kafka.yaml

# Wait for infrastructure to be ready
echo "Waiting for infrastructure to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres --timeout=120s
kubectl wait --for=condition=ready pod -l app=kafka --timeout=120s

# Deploy services
echo "Deploying services..."
kubectl apply -f sender-service/k8s/
kubectl apply -f receiver-service/k8s/

# Wait for services to be ready
echo "Waiting for services to be ready..."
kubectl wait --for=condition=ready pod -l app=sender-service --timeout=120s
kubectl wait --for=condition=ready pod -l app=receiver-service --timeout=120s

echo "Deployment completed!"
echo ""
echo "Access the services:"
echo "Sender Service: http://sender-service.local (add to /etc/hosts)"
echo "Receiver Service: http://receiver-service.local (add to /etc/hosts)"
echo ""
echo "Or use port forwarding:"
echo "kubectl port-forward service/sender-service 8080:80"
echo "kubectl port-forward service/receiver-service 8081:80"
echo ""
echo "Check logs:"
echo "kubectl logs -l app=sender-service"
echo "kubectl logs -l app=receiver-service"
echo "kubectl logs -l app=kafka"