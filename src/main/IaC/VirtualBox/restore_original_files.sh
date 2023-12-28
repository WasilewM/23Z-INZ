#!/bin/bash

echo "Are you sure that you want to restore the original files? (y/n)"
read -r answer

if [ "$answer" = "y" ] || [ "$answer" = "Y" ]; then
    echo "-----------------------------------------------------"
    echo "Restoring original files"
    mv customdata_db.sh.backup customdata_db.sh
    mv customdata_db_replica.sh.backup customdata_db_replica.sh
    mv customdata_nginx.sh.backup customdata_nginx.sh
    mv customdata_observability.sh.backup customdata_observability.sh
    mv customdata_server_app.sh.backup customdata_server_app.sh

    echo "-----------------------------------------------------"
    echo "The files have been restored"
elif [ "$answer" = "n" ] || [ "$answer" = "N" ]; then
    echo "Aborting. Something went wrong"
else
    echo "Invalid input. Please enter 'y' or 'n'"
fi