#!/bin/bash
sudo apt-get update -y &&
sudo apt-get install -y \
apt-transport-https \
ca-certificates \
curl \
gnupg-agent \
software-properties-common \
mysql-client &&
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add - &&
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" &&
sudo apt-get update -y &&
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin -y &&
sudo mkdir -p /home/customdata &&
cd /home/customdata &&
git clone https://github.com/WasilewM/23Z-INZ/ &&
cd 23Z-INZ &&
cd src/main/observability &&
new_ip=$(ip a | grep 'inet 10\.0\.1\..' | awk '{print $2}' | cut -f1 -d'/')
sudo sed -i "s|%prometheus_url%|http://$new_ip:9090|g" -i ./grafana/datasources/datasources.yaml
sudo sed -i "s|%app_url%|10.0.1.4:8080|g" -i ./prometheus/prometheus.yaml
sudo docker compose up -d
cd ../db/mysql
cat ./create_table.sql | mysql -h paas-mysql-flex-serv.mysql.database.azure.com -u worker --password=wo^Ker_123 -f
cat ./populate_db.sql | mysql -h paas-mysql-flex-serv.mysql.database.azure.com -u worker --password=wo^Ker_123 -f