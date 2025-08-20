#!/bin/bash

echo "Testing end-to-end message flow..."

# Port forward services for testing
echo "Setting up port forwarding..."
kubectl port-forward service/sender-service 8080:80 &
SENDER_PID=$!
kubectl port-forward service/receiver-service 8081:80 &
RECEIVER_PID=$!

# Wait for port forwarding to be established
sleep 5

echo "Testing sender service health..."
curl -s http://localhost:8080/api/sender/health | jq '.'

echo -e "\nTesting receiver service health..."
curl -s http://localhost:8081/api/receiver/health | jq '.'

echo -e "\nSending test message via REST API..."
curl -X POST http://localhost:8080/api/sender/send \
  -H "Content-Type: application/json" \
  -d '"Hello from Kafka test!"' | jq '.'

echo -e "\nWaiting for message to be processed (15 seconds)..."
sleep 15

echo -e "\nChecking received messages..."
curl -s http://localhost:8081/api/receiver/messages | jq '.'

echo -e "\nChecking message count..."
curl -s http://localhost:8081/api/receiver/messages/count | jq '.'

echo -e "\nChecking receiver stats..."
curl -s http://localhost:8081/api/receiver/stats | jq '.'

# Clean up port forwarding
echo -e "\nCleaning up port forwarding..."
kill $SENDER_PID $RECEIVER_PID

echo -e "\nTest completed!"
echo -e "\nTo send more messages through web interface:"
echo "kubectl port-forward service/sender-service 8080:80"
echo "Then visit: http://localhost:8080"