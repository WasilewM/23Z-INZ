#!/bin/bash

echo "Creating cloud resources..."

echo "-----------------------------------------------------"
echo "Creating resource group"
az group create --name "$RESOURCE_GROUP_NAME" --location "$RESOURCE_GROUP_LOCATION"

echo "-----------------------------------------------------"
echo "Creating Spring Apps Service instance"
az spring create --name paas-spring-apps-svc --resource-group "$RESOURCE_GROUP_NAME"

echo "-----------------------------------------------------"
echo "Creating Spring App instance"
az spring app create \
	--name paas-spring-server-app \
	--service paas-spring-apps-svc \
	--resource-group "$RESOURCE_GROUP_NAME" \
	--assign-endpoint true \
	--runtime-version Java_17 \
  --instance-count "$SPRING_APP_REPLICAS_COUNT"

echo "-----------------------------------------------------"
echo "Creating MySQL Flexible Server"
echo "and adding firewall rule to allow current client IP to connect"
az mysql flexible-server create \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --name paas-spring-mysql-db \
  --database-name cache \
  --admin-user "$MYSQL_ADMIN_USER" \
  --admin-password "$MYSQL_ADMIN_PASSWORD" \
	--location "$RESOURCE_GROUP_LOCATION" \
	--tier GeneralPurpose \
	--sku-name Standard_D2ads_v5 \
	--version 8.0.21 \
  --yes # Do not prompt for confirmation:
  # Detected current client IP : A.B.C.D
  # Do you want to enable access to client A.B.C.D (y/n):

echo "-----------------------------------------------------"
echo "Creating DB schema in MySQL DB"
mysql -h paas-spring-mysql-db.mysql.database.azure.com \
  --user "$MYSQL_ADMIN_USER" \
  --enable-cleartext-plugin \
  --password="$MYSQL_ADMIN_PASSWORD" < ../../db/mysql/create_table.sql

echo "-----------------------------------------------------"
echo "Creating connection between Spring App and MySQL DB"
az spring connection create mysql-flexible \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --service paas-spring-apps-svc \
  --app paas-spring-server-app \
  --target-resource-group "$RESOURCE_GROUP_NAME" \
  --server paas-spring-mysql-db \
  --database cache \
  --secret name="$MYSQL_ADMIN_USER" secret="$MYSQL_ADMIN_PASSWORD" \
	--client-type springBoot

echo "-----------------------------------------------------"
echo "Creating MySQL Flexible Server - Replica DB"
echo "and adding firewall rule to allow current client IP to connect"
if [ "$CREATE_REPLICA_DB" == "true" ]; then
      az mysql flexible-server replica create \
        --replica-name paas-spring-mysql-db-replica \
        --source-server paas-spring-mysql-db \
        --resource-group "$RESOURCE_GROUP_NAME"

      echo "-----------------------------------------------------"
      echo "Creating connection between Spring App and MySQL Replica DB"
      az spring connection create mysql-flexible \
        --resource-group "$RESOURCE_GROUP_NAME" \
        --service paas-spring-apps-svc \
        --app paas-spring-server-app \
        --target-resource-group "$RESOURCE_GROUP_NAME" \
        --server paas-spring-mysql-db-replica \
        --database cache \
        --secret name="$MYSQL_ADMIN_USER" secret="$MYSQL_ADMIN_PASSWORD" \
      	--client-type springBoot
else
    echo "CREATE_REPLICA_DB is empty or not set. Replication DB will not be created"
fi

echo "-----------------------------------------------------"
echo "Deploying Spring App"
az spring app deploy \
  --name paas-spring-server-app \
  --service paas-spring-apps-svc \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --artifact-path ../../../../target/server_app-0.1.0.jar \
	--runtime-version Java_17

echo "-----------------------------------------------------"
echo "Cloud resources creations has finished"
az spring app list \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --service paas-spring-apps-svc \
  --output table

echo "-----------------------------------------------------"