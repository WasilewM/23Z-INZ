#!/bin/bash
sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get install -y mysql-server
sudo mkdir -p /home/customdata
cd /home/customdata
git clone https://github.com/WasilewM/23Z-INZ/
cd 23Z-INZ
cd src/main/db/mysql
sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get install -y mysql-server
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
cat ./populate_db.sql | sudo mysql -f