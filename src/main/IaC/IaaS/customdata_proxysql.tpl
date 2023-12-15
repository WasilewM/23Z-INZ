#!/bin/bash
curl -OL https://github.com/sysown/proxysql/releases/download/v2.4.2/proxysql_2.4.2-ubuntu20_amd64.deb
sudo dpkg -i proxysql_*
sudo apt-get update -y
sudo apt-get install proxysql mysql-client -y
sudo systemctl start proxysql

sleep 10 # wait for the proxysql and mysql to start up
echo "UPDATE global_variables SET variable_value='monitor' WHERE variable_name='monitor';
LOAD MYSQL VARIABLES TO RUNTIME;
SAVE MYSQL VARIABLES TO DISK;
INSERT INTO mysql_group_replication_hostgroups (writer_hostgroup, backup_writer_hostgroup, reader_hostgroup, offline_hostgroup, active, max_writers, writer_is_also_reader, max_transactions_behind) VALUES (2, 4, 3, 1, 1, 3, 1, 100);
INSERT INTO mysql_servers(hostgroup_id, hostname, port) VALUES (2, '%master_db_private_ip%', 3306);
INSERT INTO mysql_servers(hostgroup_id, hostname, port) VALUES (3, '%replica_db_private_ip%', 3306);
LOAD MYSQL SERVERS TO RUNTIME;
SAVE MYSQL SERVERS TO DISK;
INSERT INTO mysql_users(username, password, default_hostgroup) VALUES ('cacheuser', 'cachepassword123', 2);
LOAD MYSQL USERS TO RUNTIME;
SAVE MYSQL USERS TO DISK;
" > /home/adminuser/init_proxysql.sql
cat  /home/adminuser/init_proxysql.sql | mysql -u admin -padmin -h 127.0.0.1 -P6032 -f
echo $? > /home/adminuser/init_result.txt
# 1 for the offline host group
# 2 for the writer host group
# 3 for the reader host group
# 4 for the backup writer host group
# INSERT INTO mysql_servers(hostgroup_id, hostname, port) VALUES (2, '%master_db_private_ip%', 3306);
# INSERT INTO mysql_servers(hostgroup_id, hostname, port) VALUES (3, '%replica_db_private_ip%', 3306);