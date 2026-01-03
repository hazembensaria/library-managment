#!/usr/bin/env bash
set -euo pipefail

# Simple deploy script to pull latest Docker image and restart the stack
# Usage: ./deploy.sh <image:tag>
# Requires: docker, docker-compose installed and working

IMAGE_TAG=${1:-latest}
SERVICE_NAME=library-backend
COMPOSE_FILE=docker-compose.yml

echo "Deploying image: ${IMAGE_TAG}"

echo "Stopping existing containers (if any)..."
docker-compose -f ${COMPOSE_FILE} down || true

echo "Pulling image: ${IMAGE_TAG}"
docker pull ${IMAGE_TAG} || true

echo "Starting containers..."
docker-compose -f ${COMPOSE_FILE} up -d --build

echo "Deployment finished. Current containers:"
docker ps --filter "name=${SERVICE_NAME}" --format "table {{.Names}}	{{.Image}}	{{.Status}}"

echo "To rollback, run: ./deploy.sh <previous-image:tag> or use 'docker-compose down' and start desired tag."
