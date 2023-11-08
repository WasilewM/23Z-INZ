#!/bin/bash
sudo apt-get update -y &&
sudo apt-get install -y \
apt-transport-https \
ca-certificates \
curl \
gnupg-agent \
software-properties-common &&
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add - &&
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" &&
sudo apt-get update -y &&
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin -y &&
sudo mkdir -p /home/customdata &&
cd /home/customdata &&
git clone https://github.com/WasilewM/23Z-INZ/ &&
cd 23Z-INZ &&
git checkout dev-observability &&
cd src/main/observability &&
sudo docker compose up -d