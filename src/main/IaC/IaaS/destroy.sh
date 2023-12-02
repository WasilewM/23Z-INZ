#!/bin/sh

echo "-----------------------------------------------------"
echo "Destroying all objects in tested resource group"
. ./variables.sh
terraform destroy

echo "-----------------------------------------------------"
echo "Reverting changes in configuration files"
# terraform.tfvars
sed -i "s|$VM_ADMIN_USERNAME|%admin_username%|g" -i ./terraform.tfvars
sed -i "s|$PUBLIC_KEY_PATH|%public_key_path%|g" -i ./terraform.tfvars
sed -i "s|$RESOURCE_GROUP_NAME|%resource_group_name%|g" -i ./terraform.tfvars
sed -i "s|$RESOURCE_GROUP_LOCATION|%resource_group_location%|g" -i ./terraform.tfvars
sed -i "s|$VM_SERVER_PRIVATE_IP|%server_private_ip%|g" -i ./terraform.tfvars
sed -i "s|$VM_MASTER_DB_PRIVATE_IP|%master_db_private_ip%|g" -i ./terraform.tfvars
sed -i "s|$VM_OBSERVABILITY_PRIVATE_IP|%observability_private_ip%|g" -i ./terraform.tfvars
sed -i "s|$VM_NGINX_PRIVATE_IP|%nginx_private_ip%|g" -i ./terraform.tfvars
# customdata_db.tpl
sed -i "s|$MYSQL_ADMIN_USER|%mysql_admin_user%|g" -i ./customdata_db.tpl
sed -i "s|$MYSQL_ADMIN_PASSWORD|%mysql_admin_password%|g" -i ./customdata_db.tpl
# customdata_server_app.tpl
sed -i "s|$VM_MASTER_DB_PRIVATE_IP|%master_db_private_ip%|g" -i ./customdata_server_app.tpl
sed -i "s|$MYSQL_ADMIN_USER|%mysql_admin_user%|g" -i ./customdata_server_app.tpl
sed -i "s|$MYSQL_ADMIN_PASSWORD|%mysql_admin_password%|g" -i ./customdata_server_app.tpl