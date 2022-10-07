#!/bin/bash

echo "Start redeployment"

cd ~/event-driven-architecture
sudo docker compose pull
sudo docker compose down --volumes
rm -rf ./misc/kafka_data/*
sudo docker compose up -d
sudo docker system prune -f

echo "End redeployment"
