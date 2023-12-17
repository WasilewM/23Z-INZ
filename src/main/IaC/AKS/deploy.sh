#!/bin/bash

RESOURCE_GROUP_NAME=aks-rg
RESOURCE_GROUP_LOCATION=westeurope
MYSQL_ADMIN_USER=worker
MYSQL_ADMIN_PASSWORD=wo^Ker_123
REGISTRY_NAME=akstestenvexamplaryregistry

echo "Creating cloud resources..."

echo "-----------------------------------------------------"
echo "Creating resource group"
az group create --name "$RESOURCE_GROUP_NAME" --location "$RESOURCE_GROUP_LOCATION"

echo "-----------------------------------------------------"
echo "Creating vnet and subnet for MySQL Flexible Server"
az network vnet create \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --name aks-vnet-mysql \
  --address-prefixes 10.25.0.0/16 \
  --subnet-name aks-subnet-mysql \
  --subnet-prefix 10.25.1.0/24

echo "-----------------------------------------------------"
echo "Creating MySQL Flexible Server"
echo "and a new private DNS zone"
az mysql flexible-server create \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --name aks-mysql-fs-db \
  --database-name cache \
  --admin-user "$MYSQL_ADMIN_USER" \
  --admin-password "$MYSQL_ADMIN_PASSWORD" \
  --location "$RESOURCE_GROUP_LOCATION" \
  --tier GeneralPurpose \
  --sku-name Standard_D2ads_v5 \
  --version 8.0.21 \
  --vnet aks-vnet-mysql \
  --subnet aks-subnet-mysql \
  --yes # Do not prompt for confirmation:
  # Do you want to create a new private DNS zone aks-mysql-fs-db.private.mysql.database.azure.com in resource group aks-rg (y/n):

echo "-----------------------------------------------------"
echo "Creating Azure Container Registry"
az acr create \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --name "$REGISTRY_NAME" \
  --sku Basic

az config set defaults.acr="$REGISTRY_NAME"
az acr login && mvn compile jib:build