#!/bin/sh

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