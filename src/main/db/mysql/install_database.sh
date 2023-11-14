#!/bin/bash
port=$1
db_user=$2
db_password=$3

sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get install -y mysql-server

echo "[mysqld]" | sudo tee -a /etc/mysql/my.cnf
echo "port=$port" | sudo tee -a /etc/mysql/my.cnf
echo "bind-address = 0.0.0.0" | sudo tee -a /etc/mysql/my.cnf
echo "server-id = 1" | sudo tee -a /etc/mysql/my.cnf
echo "log_bin = /var/log/mysql/mysql-bin.log" | sudo tee -a /etc/mysql/my.cnf

sudo service mysql restart

sed -i "s/%db_user%/$db_user/g" -i ./init_db.sql
sed -i "s/%db_password%/$db_password/g" -i ./init_db.sql

cat ./init_db.sql | sudo mysql -f
cat ./populate_db.sql | sudo mysql -f