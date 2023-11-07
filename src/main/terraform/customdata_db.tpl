#!/bin/bash
cd /home/adminuser &&
git clone https://github.com/WasilewM/23Z-INZ/ &&
cd 23Z-INZ &&
git checkout dev &&
cd src/main/db/mysql &&
sudo chmod +x install_database.sh &&
./install_database.sh 3306 worker wo^Ker_123