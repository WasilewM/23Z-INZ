#!/bin/sh

echo "-----------------------------------------------------"
echo "Destroying all objects in tested resource group"
terraform destroy
. ./variables.sh
az group delete --name paas-spring-rg

echo "-----------------------------------------------------"
echo "Restoring original files"
mv terraform.tfvars.backup terraform.tfvars