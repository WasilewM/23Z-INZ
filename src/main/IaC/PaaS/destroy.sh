#!/bin/bash

echo "Are you sure that you want to destroy the test environment? (y/n)"
read -r answer

if [ "$answer" = "y" ] || [ "$answer" = "Y" ]; then
    echo "-----------------------------------------------------"
    echo "Destroying all objects in tested resource group"
    terraform destroy --auto-approve
    . ./variables.sh
    az group delete --name "$RESOURCE_GROUP_NAME" --yes

    echo "-----------------------------------------------------"
    echo "Restoring original files"
    mv terraform.tfvars.backup terraform.tfvars

    echo "-----------------------------------------------------"
    echo "The test environment has been cleaned"
elif [ "$answer" = "n" ] || [ "$answer" = "N" ]; then
    echo "Aborting. Test environment will not be destroyed."
else
    echo "Invalid input. Please enter 'y' or 'n'."
fi