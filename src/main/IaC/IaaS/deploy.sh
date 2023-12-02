#!/bin/sh

echo "-----------------------------------------------------"
echo "Reading variables.sh"
. ./variables.sh
# terraform.tfvars
sed -i "s|%admin_username%|$VM_ADMIN_USERNAME|g" -i ./terraform.tfvars
sed -i "s|%public_key_path%|$PUBLIC_KEY_PATH|g" -i ./terraform.tfvars
sed -i "s|%resource_group_name%|$RESOURCE_GROUP_NAME|g" -i ./terraform.tfvars
sed -i "s|%resource_group_location%|$RESOURCE_GROUP_LOCATION|g" -i ./terraform.tfvars
sed -i "s|%server_private_ip%|$VM_SERVER_PRIVATE_IP|g" -i ./terraform.tfvars
sed -i "s|%master_db_private_ip%|$VM_MASTER_DB_PRIVATE_IP|g" -i ./terraform.tfvars
sed -i "s|%observability_private_ip%|$VM_OBSERVABILITY_PRIVATE_IP|g" -i ./terraform.tfvars
sed -i "s|%nginx_private_ip%|$VM_NGINX_PRIVATE_IP|g" -i ./terraform.tfvars
# customdata_db.tpl
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_db.tpl
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_db.tpl
# customdata_server_app.tpl
sed -i "s|%master_db_private_ip%|$VM_MASTER_DB_PRIVATE_IP|g" -i ./customdata_server_app.tpl
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_server_app.tpl
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_server_app.tpl

echo "-----------------------------------------------------"
echo "Deploying infrastructure"
terraform apply -auto-approve