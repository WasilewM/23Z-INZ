#!/bin/bash
db_user=worker
db_password=wo^Ker_123

sed -i "s/%db_user%/$db_user/g" -i ./init_db.sql
sed -i "s/%db_password%/$db_password/g" -i ./init_db.sql

cat ./init_db.sql | sudo mysql -f
cat ./populate_db.sql | sudo mysql -f