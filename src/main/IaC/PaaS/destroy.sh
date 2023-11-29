#!/bin/sh

echo "-----------------------------------------------------"
echo "Deploying all objects in tested resource group"
. ./variables.sh
az group delete --name paas-spring-rg