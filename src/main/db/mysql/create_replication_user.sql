CREATE USER IF NOT EXISTS '%db_replication_user%'@'%' IDENTIFIED WITH mysql_native_password BY '%db_replication_password%';
GRANT REPLICATION SLAVE ON *.* TO '%db_replication_user%'@'%';