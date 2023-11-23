CREATE USER IF NOT EXISTS '%db_user%'@'%' IDENTIFIED BY '%db_password%';

CREATE DATABASE IF NOT EXISTS cache;

ALTER DATABASE cache
	DEFAULT CHARACTER SET utf8
	DEFAULT COLLATE utf8_general_ci;

GRANT ALL PRIVILEGES ON cache.* TO '%db_user%'@'%' WITH GRANT OPTION;