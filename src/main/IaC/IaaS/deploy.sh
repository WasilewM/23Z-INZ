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
sed -i "s|%observability_private_ip%|$VM_OBSERVABILITY_PRIVATE_IP|g" -i ./terraform.tfvars
sed -i "s|%nginx_private_ip%|$VM_NGINX_PRIVATE_IP|g" -i ./terraform.tfvars

# create a configuration block of all server VMs
block_string=""
for ((i = 0; i < ${#VM_SERVER_PRIVATE_IP[@]}; i++)); do
    ip="${VM_SERVER_PRIVATE_IP[i]}"

    block_string+="\"$i\" = {\n"
    block_string+="    private_ip = \"$ip\"\n"
    block_string+="  }\n"
done
sed -i "s|%server_private_ip%|$block_string|g" -i ./terraform.tfvars

# create a configuration block of all DB VMs
db_block_string=""
## add master DB
db_block_string="\"master\" = {\n"
db_block_string+="    private_ip = \"$VM_MASTER_DB_PRIVATE_IP\"\n"
db_block_string+="    customdata_file = \"customdata_db.tpl\"\n"
db_block_string+="  }\n"
## add replica DB
if [ ! -z "$VM_REPLICA_DB_PRIVATE_IP" ]; then
    if [ ! -z "$MYSQL_REPLICATION_USER" ]; then
        if [ ! -z "$MYSQL_REPLICATION_PASSWORD" ]; then
            db_block_string+="\"replica\" = {\n"
            db_block_string+="    private_ip = \"$VM_REPLICA_DB_PRIVATE_IP\"\n"
            db_block_string+="    customdata_file = \"customdata_db_replica.tpl\"\n"
            db_block_string+="  }\n"
        else
            echo "MYSQL_REPLICATION_PASSWORD is empty or not set. Cannot create a user without password"
        fi
    else
        echo "MYSQL_REPLICATION_USER is empty or not set. Replication user will not be created"
    fi
else
    echo "VM_REPLICA_DB_PRIVATE_IP is empty or not set. Replication DB will not be created"
fi
## terraform.tfvars
sed -i "s|%db_ip%|$db_block_string|g" -i ./terraform.tfvars

# customdata_db.tpl
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_db.tpl
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_db.tpl
sed -i "s|mysql_replication_user=|mysql_replication_user=$MYSQL_REPLICATION_USER|g" -i ./customdata_db.tpl
sed -i "s|mysql_replication_password=|mysql_replication_password=$MYSQL_REPLICATION_PASSWORD|g" -i ./customdata_db.tpl

# customdata_db_replication.tpl
sed -i "s|%master_db_private_ip%|$VM_MASTER_DB_PRIVATE_IP|g" -i ./customdata_db_replica.tpl
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_db_replica.tpl
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_db_replica.tpl
sed -i "s|%mysql_replication_user%|$MYSQL_REPLICATION_USER|g" -i ./customdata_db_replica.tpl
sed -i "s|%mysql_replication_password%|$MYSQL_REPLICATION_PASSWORD|g" -i ./customdata_db_replica.tpl

# customdata_nginx.tpl
servers_string=""

if [ ! -z "$NGINX_LOAD_BALANCING_STRATEGY" ]; then
    if [ "$NGINX_LOAD_BALANCING_STRATEGY" == "least_conn" ]; then
        servers_string="least_conn;\n"
        echo "Strategy \"$NGINX_LOAD_BALANCING_STRATEGY\" selected for nginx load balancer"
    elif [ "$NGINX_LOAD_BALANCING_STRATEGY" == "ip_hash" ]; then
        servers_string="ip_hash;\n"
        echo "Strategy \"$NGINX_LOAD_BALANCING_STRATEGY\" selected for nginx load balancer"
    else
        echo "Value \"$NGINX_LOAD_BALANCING_STRATEGY\" is undetermined for NGINX_LOAD_BALANCING_STRATEGY. Proceeding with default strategy \"round robin\""
    fi
else
    echo "NGINX_LOAD_BALANCING_STRATEGY is empty or not set. Proceeding with default strategy \"round robin\""
fi

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