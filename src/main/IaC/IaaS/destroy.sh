#!/bin/bash

echo "-----------------------------------------------------"
echo "Destroying all objects in tested resource group"
. ./variables.sh
terraform destroy

if [ $? == 1 ]; then
    exit 1
fi

echo "-----------------------------------------------------"
echo "Restoring original files"
mv terraform.tfvars.backup terraform.tfvars
mv customdata_db.tpl.backup customdata_db.tpl
mv customdata_nginx.tpl.backup customdata_nginx.tpl
mv customdata_observability.tpl.backup customdata_observability.tpl
mv customdata_server_app.tpl.backup customdata_server_app.tpl