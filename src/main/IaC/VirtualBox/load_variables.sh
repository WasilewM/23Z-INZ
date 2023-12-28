#!/bin/bash

echo "-----------------------------------------------------"
echo "Creating copies of files that need to be changed"
cp customdata_db.sh customdata_db.sh.backup
cp customdata_db_replica.sh customdata_db_replica.sh.backup
cp customdata_nginx.sh customdata_nginx.sh.backup
cp customdata_observability.sh customdata_observability.sh.backup
cp customdata_server_app.sh customdata_server_app.sh.backup

echo "-----------------------------------------------------"
echo "Reading variables.sh"
. ./variables.sh

# customdata_db.sh
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_db.sh
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_db.sh
sed -i "s|mysql_replication_user=\"\"|mysql_replication_user=$MYSQL_REPLICATION_USER|g" -i ./customdata_db.sh
sed -i "s|mysql_replication_password=\"\"|mysql_replication_password=$MYSQL_REPLICATION_PASSWORD|g" -i ./customdata_db.sh

# customdata_db_replication.sh
if [ ! -z "$VM_REPLICA_DB_IP" ]; then
  sed -i "s|%master_db_ip%|$VM_MASTER_DB_IP|g" -i ./customdata_db_replica.sh
  sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_db_replica.sh
  sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_db_replica.sh
  sed -i "s|%mysql_replication_user%|$MYSQL_REPLICATION_USER|g" -i ./customdata_db_replica.sh
  sed -i "s|%mysql_replication_password%|$MYSQL_REPLICATION_PASSWORD|g" -i ./customdata_db_replica.sh
else
    echo "VM_REPLICA_DB_IP is empty or not set. customdata_db_replica.sh will not be parametrized"
fi

# customdata_nginx.sh
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

for ip in "${VM_SERVER_IP[@]}"; do
    servers_string+="server $ip:8080;\n"
done
sed -i "s|%server_ip%|$servers_string|g" -i ./customdata_nginx.sh

# customdata_observability.sh
servers_string=""
for ip in "${VM_SERVER_IP[@]}"; do
    if [ ! -z "$servers_string" ]; then
        servers_string+=", "
    fi
    servers_string+="'$ip:8080'"
done
sed -i "s|%server_ip%|$servers_string|g" -i ./customdata_observability.sh

# customdata_server_app.sh
sed -i "s|%master_db_ip%|$VM_MASTER_DB_IP|g" -i ./customdata_server_app.sh
sed -i "s|%mysql_admin_user%|$MYSQL_ADMIN_USER|g" -i ./customdata_server_app.sh
sed -i "s|%mysql_admin_password%|$MYSQL_ADMIN_PASSWORD|g" -i ./customdata_server_app.sh


if [ $? == 1 ]; then
    echo "Aborting. Something went wrong"
    exit 1
fi

echo "-----------------------------------------------------"
echo "The files have been parametrized"