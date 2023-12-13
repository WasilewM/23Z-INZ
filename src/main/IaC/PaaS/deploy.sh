#!/bin/bash

echo "-----------------------------------------------------"
echo "Creating copies of files that need to be changed"
cp terraform.tfvars terraform.tfvars.backup

echo "-----------------------------------------------------"
echo "Packaging maven project"
cd ../../../../
echo "PWD:$(pwd)"
mvn package -Dmaven.test.skip=true

echo "-----------------------------------------------------"
echo "Deploying infrastructure"
cd -
echo "PWD:$(pwd)"
. ./variables.sh
./deploy_azure_resources.sh

echo "-----------------------------------------------------"
echo "Deploying observability infrastructure"
sed -i "s|%admin_username%|$VM_ADMIN_USERNAME|g" -i ./terraform.tfvars
sed -i "s|%public_key_path%|$PUBLIC_KEY_PATH|g" -i ./terraform.tfvars
sed -i "s|%resource_group_name%|$RESOURCE_GROUP_NAME|g" -i ./terraform.tfvars
sed -i "s|%resource_group_location%|$RESOURCE_GROUP_LOCATION|g" -i ./terraform.tfvars
terraform apply -auto-approve