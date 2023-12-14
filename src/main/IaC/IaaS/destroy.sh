#!/bin/bash

echo "Are you sure that you want to destroy the test environment? (y/n)"
read -r answer

if [ "$answer" = "y" ] || [ "$answer" = "Y" ]; then
    echo "-----------------------------------------------------"
    echo "Destroying all objects in tested resource group"
    . ./variables.sh
    terraform destroy --auto-approve

    if [ $? == 1 ]; then
        echo "Error occurred while destroying the resources. Aborting..."
        exit 1
    fi

    echo "-----------------------------------------------------"
    echo "Restoring original files"
    mv terraform.tfvars.backup terraform.tfvars
    mv customdata_db.tpl.backup customdata_db.tpl
    mv customdata_db_replica.tpl.backup customdata_db_replica.tpl
    mv customdata_nginx.tpl.backup customdata_nginx.tpl
    mv customdata_observability.tpl.backup customdata_observability.tpl
    mv customdata_proxysql.tpl.backup customdata_proxysql.tpl
    mv customdata_server_app.tpl.backup customdata_server_app.tpl

    echo "-----------------------------------------------------"
    echo "The test environment has been cleaned"
elif [ "$answer" = "n" ] || [ "$answer" = "N" ]; then
    echo "Aborting. Test environment will not be destroyed."
else
    echo "Invalid input. Please enter 'y' or 'n'."
fi