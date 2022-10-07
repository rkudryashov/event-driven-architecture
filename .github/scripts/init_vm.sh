#!/bin/bash

echo "Start initialization"

echo "Install Docker"
# https://docs.docker.com/engine/install/ubuntu
sudo apt-get update
sudo apt-get install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "Configure VM to use Docker as a non-root user"
# https://docs.docker.com/engine/install/linux-postinstall
sudo groupadd docker
sudo usermod -aG docker $USER
newgrp docker

echo "Create daemon.json to configure log rotation"
sudo apt-get install -y jq
echo '{"log-driver": "json-file", "log-opts": {"max-size": "1m", "max-file": "5"}}' | jq . | sudo tee /etc/docker/daemon.json
sudo systemctl daemon-reload
sudo systemctl restart docker

echo "Create a directory on the host that will later be mapped to the kafka container volume and change its owner"
mkdir ~/event-driven-architecture/misc/kafka_data
sudo chown -R 1001:1001 ~/event-driven-architecture/misc/kafka_data

echo "End initialization"
