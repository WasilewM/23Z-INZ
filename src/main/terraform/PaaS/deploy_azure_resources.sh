#!/bin/sh

echo "Creating cloud resources..."

echo "-----------------------------------------------------"
echo "Creating resource group"
az group create --name paas-spring-rg --location "$AZ_LOCATION"

echo "-----------------------------------------------------"
echo "Creating Spring Apps Service instance"
az spring create --name paas-spring-apps-svc --resource-group paas-spring-rg

echo "-----------------------------------------------------"
echo "Creating MySQL Flexible Server"
echo "and adding firewall rule to allow current client IP to connect"
az mysql flexible-server create \
  --resource-group paas-spring-rg \
  --name paas-spring-mysql-db \
  --database-name cache \
  --admin-user "$AZ_MYSQL_ADMIN_USER" \
  --admin-password "$AZ_MYSQL_ADMIN_PASSWORD" \
	--location "$AZ_LOCATION" \
	--sku-name Standard_B1ms \
	--version 8.0.21 \
  --yes # Do not prompt for confirmation:
  # Detected current client IP : A.B.C.D
  # Do you want to enable access to client A.B.C.D (y/n):

echo "-----------------------------------------------------"
echo "Creating DB schema in MySQL DB"
mysql -h paas-spring-mysql-db.mysql.database.azure.com --user "$AZ_MYSQL_ADMIN_USER" --enable-cleartext-plugin --password="$AZ_MYSQL_ADMIN_PASSWORD" < ../../db/mysql/create_table.sql

echo "-----------------------------------------------------"
echo "Populating DB schema in MySQL DB with data"
mysql -h paas-spring-mysql-db.mysql.database.azure.com --user "$AZ_MYSQL_ADMIN_USER" --enable-cleartext-plugin --password="$AZ_MYSQL_ADMIN_PASSWORD" < ../../db/mysql/populate_db.sql

echo "-----------------------------------------------------"
echo "Creating Spring App instance"
az spring app create \
	--name paas-spring-server-app \
	--service paas-spring-apps-svc \
	--resource-group paas-spring-rg \
	--assign-endpoint true \
	--runtime-version Java_17

echo "-----------------------------------------------------"
echo "Creating connection between Spring App and MySQL DB"
az spring connection create mysql-flexible \
  --resource-group paas-spring-rg \
  --service paas-spring-apps-svc \
  --app paas-spring-server-app \
  --target-resource-group paas-spring-rg \
  --server paas-spring-mysql-db \
  --database cache \
  --secret name="$AZ_MYSQL_ADMIN_USER" secret="$AZ_MYSQL_ADMIN_PASSWORD" \
	--client-type springBoot

echo "-----------------------------------------------------"
echo "Deploying Spring App"
az spring app deploy \
  --name paas-spring-server-app \
  --service paas-spring-apps-svc \
  --resource-group paas-spring-rg \
  --artifact-path ../../../../target/server_app-0.1.0.jar \
	--runtime-version Java_17

echo "-----------------------------------------------------"
echo "Cloud resources creations has finished"
az spring app list \
  --resource-group paas-spring-rg \
  --service paas-spring-apps-svc \
  --output table

echo "-----------------------------------------------------"