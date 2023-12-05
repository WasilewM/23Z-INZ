#!/bin/bash

echo "-----------------------------------------------------"
echo "Creating copies of files that need to be changed"
cp terraform.tfvars terraform.tfvars.backup
cp customdata_db.tpl customdata_db.tpl.backup
cp customdata_db_replica.tpl customdata_db_replica.tpl.backup
cp customdata_nginx.tpl customdata_nginx.tpl.backup
cp customdata_observability.tpl customdata_observability.tpl.backup
cp customdata_server_app.tpl customdata_server_app.tpl.backup

echo "-----------------------------------------------------"
echo "Reading variables.sh"
. ./variables.sh
# terraform.tfvars
sed -i "s|%admin_username%|$VM_ADMIN_USERNAME|g" -i ./terraform.tfvars
sed -i "s|%public_key_path%|$PUBLIC_KEY_PATH|g" -i ./terraform.tfvars
sed -i "s|%resource_group_name%|$RESOURCE_GROUP_NAME|g" -i ./terraform.tfvars
sed -i "s|%resource_group_location%|$RESOURCE_GROUP_LOCATION|g" -i ./terraform.tfvars
sed -i "s|%master_db_private_ip%|$VM_MASTER_DB_PRIVATE_IP|g" -i ./terraform.tfvars
sed -i "s|%replica_db_private_ip%|$VM_REPLICA_DB_PRIVATE_IP|g" -i ./terraform.tfvars
sed -i "s|%observability_private_ip%|$VM_OBSERVABILITY_PRIVATE_IP|g" -i ./terraform.tfvars
sed -i "s|%nginx_private_ip%|$VM_NGINX_PRIVATE_IP|g" -i ./terraform.tfvars

# create a configuration block of all server vms
block_string=""
for ((i = 0; i < ${#VM_SERVER_PRIVATE_IP[@]}; i++)); do
    ip="${VM_SERVER_PRIVATE_IP[i]}"

    block_string+="\"$i\" = {\n"
    block_string+="    private_ip = \"$ip\"\n"
    block_string+="  }\n"
done
sed -i "s|%server_private_ip%|$block_string|g" -i ./terraform.tfvars

# customdata_db.tpl
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_db.tpl
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_db.tpl
sed -i "s|%mysql_replication_user%|$MYSQL_REPLICATION_USER|g" -i ./customdata_db.tpl
sed -i "s|%mysql_replication_password%|$MYSQL_REPLICATION_PASSWORD|g" -i ./customdata_db.tpl

# customdata_db_replication.tpl
sed -i "s|%master_db_private_ip%|$VM_MASTER_DB_PRIVATE_IP|g" -i ./customdata_db_replica.tpl
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_db_replica.tpl
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_db_replica.tpl
sed -i "s|%mysql_replication_user%|$MYSQL_REPLICATION_USER|g" -i ./customdata_db_replica.tpl
sed -i "s|%mysql_replication_password%|$MYSQL_REPLICATION_PASSWORD|g" -i ./customdata_db_replica.tpl

# customdata_nginx.tpl
servers_string=""
for ip in "${VM_SERVER_PRIVATE_IP[@]}"; do
    servers_string+="server $ip:8080;\n"
done
sed -i "s|%server_private_ip%|$servers_string|g" -i ./customdata_nginx.tpl

# customdata_observability.tpl
servers_string=""
for ip in "${VM_SERVER_PRIVATE_IP[@]}"; do
    if [ ! -z "$servers_string" ]; then
        servers_string+=", "
    fi
    servers_string+="'$ip:8080'"
done
sed -i "s|%server_private_ip%|$servers_string|g" -i ./customdata_observability.tpl

# customdata_server_app.tpl
sed -i "s|%master_db_private_ip%|$VM_MASTER_DB_PRIVATE_IP|g" -i ./customdata_server_app.tpl
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_server_app.tpl
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_server_app.tpl


if [ $? == 1 ]; then
    exit 1
fi

echo "-----------------------------------------------------"
echo "Deploying infrastructure"
terraform fmt
terraform apply -auto-approve