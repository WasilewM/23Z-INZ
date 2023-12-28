#!/bin/bash
sudo apt-get update -y &&
sudo apt install maven \
openjdk-17-jdk-headless -y &&
git clone https://github.com/WasilewM/23Z-INZ/ &&
cd 23Z-INZ &&
sudo sed -i "s|%datasource_url%|jdbc:mysql://%master_db_ip%:3306/cache|g" -i ./src/main/resources/application.properties &&
sudo sed -i "s|%datasource_username%|%mysql_admin_user%|g" -i ./src/main/resources/application.properties &&
sudo sed -i "s|%datasource_password%|%mysql_admin_password%|g" -i ./src/main/resources/application.properties &&
sudo mvn spring-boot:run