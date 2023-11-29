#!/bin/bash
sudo apt-get update -y &&
sudo apt install maven \
openjdk-17-jdk-headless -y &&
sudo mkdir -p /home/customdata &&
cd /home/customdata &&
git clone https://github.com/WasilewM/23Z-INZ/ &&
cd 23Z-INZ &&
sudo sed -i "s|%datasource_url%|jdbc:mysql://10.0.1.5:3306/cache|g" -i ./src/main/resources/application.properties &&
sudo sed -i "s|%datasource_username%|worker|g" -i ./src/main/resources/application.properties &&
sudo sed -i "s|%datasource_password%|wo^Ker_123|g" -i ./src/main/resources/application.properties &&
sudo mvn spring-boot:run