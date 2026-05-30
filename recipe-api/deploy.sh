#!/bin/bash
# Build and deploy recipe-api
set -e
cd "$(dirname "$0")"

echo "Building recipe-api..."
mvn package -DskipTests -q

echo "Deploying systemd service..."
sudo cp recipe-api.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl restart recipe-api
sudo systemctl enable recipe-api

echo "Waiting for service to start..."
sleep 3
sudo systemctl status recipe-api --no-pager | head -10

echo "Done. API running on port 8081."
