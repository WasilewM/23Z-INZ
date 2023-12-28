#!/bin/bash
sudo apt-get update -y &&
sudo apt-get install -y nginx

cat << EOF > /etc/nginx/sites-enabled/lb
upstream backend {
    %server_ip%
}

server {
    listen 8080;

    location / {
        proxy_pass http://backend;
        include proxy_params;
    }
}
EOF

sudo nginx -s reload