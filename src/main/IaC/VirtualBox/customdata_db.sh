#!/bin/bash
sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get install -y mysql-server
git clone https://github.com/WasilewM/23Z-INZ/
cd 23Z-INZ
cd src/main/db/mysql
echo "[mysqld]" | sudo tee -a /etc/mysql/my.cnf
echo "port=3306" | sudo tee -a /etc/mysql/my.cnf
echo "bind-address = 0.0.0.0" | sudo tee -a /etc/mysql/my.cnf
echo "server-id = 1" | sudo tee -a /etc/mysql/my.cnf
echo "log_bin = /var/log/mysql/mysql-bin.log" | sudo tee -a /etc/mysql/my.cnf
sudo service mysql restart
sed -i "s/%db_user%/%mysql_admin_user%/g" -i ./init_db.sql
sed -i "s/%db_password%/%mysql_admin_password%/g" -i ./init_db.sql
cat ./init_db.sql | sudo mysql -f
cat ./create_table.sql | sudo mysql -f

mysql_replication_user=""
mysql_replication_password=""
if [ ! -z "$mysql_replication_user" ]; then
    if [ ! -z "$mysql_replication_password" ]; then
        sed -i "s/%db_replication_user%/$mysql_replication_user/g" -i ./create_replication_user.sql
        sed -i "s/%db_replication_password%/$mysql_replication_password/g" -i ./create_replication_user.sql
        cat ./create_replication_user.sql | sudo mysql -f
    else
        echo "mysql_replication_password is empty or not set. Cannot create a user without password"
    fi
else
    echo "mysql_replication_user is empty or not set. Replication user will not be created"
fi