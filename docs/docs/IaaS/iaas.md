# Infrastructure as a Service

## About
This section describes steps required to configure the `IaaS` (Infrastructure as a Service) test environment. This environment is set up in Microsoft Azure. 
More information about `IaaS` in Microsoft Azure cloud can be found [here](https://azure.microsoft.com/en-us/solutions/azure-iaas/?ef_id=_k_Cj0KCQiA1rSsBhDHARIsANB4EJauXG98uWXDTN4LX1qbU2wN7bukTlcml_efIeGJ99zwDeAcKKoYtNIaAkcREALw_wcB_k_&OCID=AIDcmm4rphvbww_SEM__k_Cj0KCQiA1rSsBhDHARIsANB4EJauXG98uWXDTN4LX1qbU2wN7bukTlcml_efIeGJ99zwDeAcKKoYtNIaAkcREALw_wcB_k_&gad_source=1&gclid=Cj0KCQiA1rSsBhDHARIsANB4EJauXG98uWXDTN4LX1qbU2wN7bukTlcml_efIeGJ99zwDeAcKKoYtNIaAkcREALw_wcB#overview).

## User guide

!!! Note  
    Please note that before going any further we need to have [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli) installed. Apart from that we need to be logged into the Azure account in our console by running:  
    ```shell
    az login
    ```
    This will redirect us to a browser to confirm the identity.

!!! Warning
    Before running any script we need to make sure that the line separators of files with extensions `.tpl` and `.sh` match our system requirements. Example is provided below:   
    ```
    $ file deploy.sh
    deploy.sh: Bourne-Again shell script, ASCII text executable, with CRLF line terminators
    $ sed -i 's/\r$//' deploy.sh
    $ file deploy.sh
    deploy.sh: Bourne-Again shell script, ASCII text executable
    ```
    Alternatively, line separators can be configured in our IDE like in [IntelliJ IDEA](https://www.jetbrains.com/help/idea/configuring-line-endings-and-line-separators.html).  


!!! Tip  
    Both `IaaS` deployment and [`VirtualBox`](../VirtualBox/virtualbox.md) deployment use virtual machines (VMs); therefore, some tips / instructions may apply to both environments.

### How to create a 1-1 model?
Let's assume that we start in the root directory of the project. In order to create a model go to the `src/main/IaC/IaaS` directory:
```shell
cd src/main/IaC/IaaS
```
Now we need to configure the environment variables. To do this we need to edit the `variables.sh` file which look like this:
```bash
#!/bin/bash

export RESOURCE_GROUP_NAME=
export RESOURCE_GROUP_LOCATION=
export MYSQL_ADMIN_USER=
export MYSQL_ADMIN_PASSWORD=
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
export VM_ADMIN_USERNAME=
export PUBLIC_KEY_PATH=
export VM_SERVER_PRIVATE_IP=()
export VM_MASTER_DB_PRIVATE_IP=
export VM_REPLICA_DB_PRIVATE_IP=
export VM_OBSERVABILITY_PRIVATE_IP=
export VM_NGINX_PRIVATE_IP=
export NGINX_LOAD_BALANCING_STRATEGY=
```
Below is an explanation of each variable:  
- `RESOURCE_GROUP_NAME` - resource group name which will be created and in which all other resources will be created  
- `RESOURCE_GROUP_LOCATION` - Azure region (location) in which resource group should be located. Region names can be checked [here](https://azure.microsoft.com/en-us/explore/global-infrastructure/geographies/#geographies). It is important to choose a region which supports [Azure Spring Apps](https://azure.microsoft.com/en-us/products/spring-apps) because not all of them do. For example `northeurope` or `westeurope` can be chosen, but `polandcentral` is not suitable in this case.  
- `MYSQL_ADMIN_USER` - admin username that will be used to access the database  
- `MYSQL_REPLICATION_PASSWORD` - password for the `MYSQL_ADMIN_USER`  
- `MYSQL_REPLICATION_USER` - username for the replication user. In 1-1 model this variable should remain empty  
- `MYSQL_REPLICATION_PASSWORD` - password for the `MYSQL_REPLICATION_USER`. In 1-1 model this variable should remain empty  
- `VM_ADMIN_USERNAME` - admin username that will be used to access the observability VM  
- `PUBLIC_KEY_PATH` - path to the public key created for the `VM_ADMIN_USERNAME` to access the observability VM. It is suggested to use following command to generate the ssh key pair: `ssh-keygen -t rsa`. For more information about the ssh keys you can read [this article](https://cloud.ibm.com/docs/power-iaas?topic=power-iaas-creating-ssh-key)  
- `VM_SERVER_PRIVATE_IP` - an array of private IPs for the server application virtual machines. IPs should be passed as string, for example `"10.0.1.4"`, and have to belong to the IP range of the `"10.0.1.0/26"` subnet. In 1-1 model we only want to specify a single address here in order to create only one VM for the server app  
- `VM_MASTER_DB_PRIVATE_IP` - a private IP for the virtual machine for the MySQL database. The IP has to belong to the IP range of the `"10.0.1.0/26"` subnet
- `VM_REPLICA_DB_PRIVATE_IP` - a private IP for the virtual machine for the MySQL replica database (slave). In 1-1 model this variable should remain empty
- `VM_OBSERVABILITY_PRIVATE_IP` - a private IP for the virtual machine for the observability. The IP has to belong to the IP range of the `"10.0.1.0/26"` subnet
- `VM_NGINX_PRIVATE_IP` - a private IP for the virtual machine for load balancer. The IP has to belong to the IP range of the `"10.0.1.0/26"` subnet  
- `NGINX_LOAD_BALANCING_STRATEGY` - a strategy for load balancing. In 1-1 model it is can be left empty. More on the load balancing strategies can be found [here](#how-to-choose-load-balancing-strategy)  

To sum up all the above our `variables.sh` file for the 1-1 model should look like this:
```bash
#!/bin/bash

export RESOURCE_GROUP_NAME=iaas-rg
export RESOURCE_GROUP_LOCATION=westeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
export VM_SERVER_PRIVATE_IP=("10.0.1.4")
export VM_MASTER_DB_PRIVATE_IP=10.0.1.5
export VM_REPLICA_DB_PRIVATE_IP=
export VM_OBSERVABILITY_PRIVATE_IP=10.0.1.6
export VM_NGINX_PRIVATE_IP=10.0.1.7
export NGINX_LOAD_BALANCING_STRATEGY=
```
Now we can run `deploy.sh` script to create the test environment:
```shell
./deploy.sh
```
And we expect the output similar to this one:
```shell
-----------------------------------------------------
Creating copies of files that need to be changed     
-----------------------------------------------------
Reading variables.sh
VM_REPLICA_DB_PRIVATE_IP is empty or not set. Replication DB will not be created
NGINX_LOAD_BALANCING_STRATEGY is empty or not set. Proceeding with default strategy "round robin"
-----------------------------------------------------
Deploying infrastructure
terraform.tfvars

Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
...

Apply complete! Resources: 16 added, 0 changed, 0 destroyed.
```
Terraform logs have been skipped in the example in order not to reveal any sensitive data, like Azure subscription ID, email address, etc.  

!!! Warning  
    We may need to wait a bit for the VMs to be ready due to the fact that they are being configured in the background.  

When the environment is no longer needed we can destroy it by following the steps from [this paragraph](#how-to-clean-up-the-environment).

### How to create a n-1 model?
The steps to create a n-1 model are almost the same as the ones executed previously for the 1-1 model.  
The only difference is the number of IP addresses used for `VM_SERVER_PRIVATE_IP` variable. Here we should specify the internal IPs for the VMs we want to create, for example `("10.0.1.4" "10.0.1.8")` or `("10.0.1.4" "10.0.1.8" "10.0.1.9")`.  

!!! Tip
    Before created numerous VMs we should check our resource quotas to make sure that we are allowed to creat the desired environment. We need to pay attention to the following quotas:  
    - `Total Regional vCPUs` - represents total number of `vCPUs` that we can request  
    - `Standard BS Family vCPUs` - represents the number of `vCPUs` from `Standard BS Family` that we can request. VMs for the database and the nginx load balancer use this `vCPUs`  
    - `Standard Av2 Family vCPUs` - represents the number of `vCPUs` from `Standard Av2 Family` that we can request. VMs for the server application use this `vCPUs`  
    More on quotas topic can be found [here](https://learn.microsoft.com/en-us/azure/quotas/quotas-overview).  
    More on VM families can be found [here](https://azure.microsoft.com/en-us/pricing/details/virtual-machines/series/).  

To sum up all the above our `variables.sh` file for the n-1 model should look like this:
```bash
#!/bin/bash

export RESOURCE_GROUP_NAME=iaas-rg
export RESOURCE_GROUP_LOCATION=westeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
export VM_SERVER_PRIVATE_IP=("10.0.1.4" "10.0.1.8")
export VM_MASTER_DB_PRIVATE_IP=10.0.1.5
export VM_REPLICA_DB_PRIVATE_IP=
export VM_OBSERVABILITY_PRIVATE_IP=10.0.1.6
export VM_NGINX_PRIVATE_IP=10.0.1.7
export NGINX_LOAD_BALANCING_STRATEGY=
```
Now we can run `deploy.sh` script to create the test environment:
```shell
./deploy.sh
```
And we expect the output similar to this one:
```shell
-----------------------------------------------------
Creating copies of files that need to be changed     
-----------------------------------------------------
Reading variables.sh
VM_REPLICA_DB_PRIVATE_IP is empty or not set. Replication DB will not be created
NGINX_LOAD_BALANCING_STRATEGY is empty or not set. Proceeding with default strategy "round robin"
-----------------------------------------------------
Deploying infrastructure
terraform.tfvars

Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
...

Apply complete! Resources: 18 added, 0 changed, 0 destroyed.
```
Terraform logs have been skipped in the example in order not to reveal any sensitive data, like Azure subscription ID, email address, etc.

!!! Warning  
    We may need to wait a bit for the VMs to be ready due to the fact that they are being configured in the background.

When the environment is no longer needed we can destroy it by following the steps from [this paragraph](#how-to-clean-up-the-environment).

### How to create a master-slave database configuration?
As mentioned in the variables description earlier, we will need to specify values for 3 variables that we've left empty until now:  
- `MYSQL_REPLICATION_USER` - username for the replication user  
- `MYSQL_REPLICATION_PASSWORD` - password for the `MYSQL_REPLICATION_USER`  
- `VM_REPLICA_DB_PRIVATE_IP` - a private IP for the virtual machine for the MySQL replica database (slave). The IP has to belong to the IP range of the `"10.0.1.0/26"` subnet  
So our `variables.sh` file should look like this:
```bash
#!/bin/bash

export RESOURCE_GROUP_NAME=iaas-rg
export RESOURCE_GROUP_LOCATION=westeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export MYSQL_REPLICATION_USER=repl
export MYSQL_REPLICATION_PASSWORD=repl#789
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
export VM_SERVER_PRIVATE_IP=("10.0.1.4" "10.0.1.8")
export VM_MASTER_DB_PRIVATE_IP=10.0.1.5
export VM_REPLICA_DB_PRIVATE_IP=10.0.1.9
export VM_OBSERVABILITY_PRIVATE_IP=10.0.1.6
export VM_NGINX_PRIVATE_IP=10.0.1.7
export NGINX_LOAD_BALANCING_STRATEGY=
```
And now we cen deploy the resources:
```shell
./deploy.sh
```
And we expect the output similar to this one:
```shell
-----------------------------------------------------
Creating copies of files that need to be changed     
-----------------------------------------------------
Reading variables.sh
NGINX_LOAD_BALANCING_STRATEGY is empty or not set. Proceeding with default strategy "round robin"
-----------------------------------------------------
Deploying infrastructure
terraform.tfvars

Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
  + create

Terraform will perform the following actions:
...

Apply complete! Resources: 20 added, 0 changed, 0 destroyed.
```
Terraform logs have been skipped in the example in order not to reveal any sensitive data, like Azure subscription ID, email address, etc.

!!! Warning  
    We may need to wait a bit for the VMs to be ready due to the fact that they are being configured in the background.

When the environment is no longer needed we can destroy it by following the steps from [this paragraph](#how-to-clean-up-the-environment).

### How to choose load balancing strategy?
#### What load balancing strategies are available?
In nginx there are several options regarding load balancing which are describe [here](https://docs.nginx.com/nginx/admin-guide/load-balancer/http-load-balancer/).  
In this environment we can specify which load balancing strategy do we want to use. Below are quotes describing available strategies:  

!!! quote
    `Round Robin` – Requests are distributed evenly across the servers (...). This method is used by default (there is no directive for enabling it).

!!! quote
    `Least Connections` – A request is sent to the server with the least number of active connections (...).

!!! quote
    `IP Hash` – The server to which a request is sent is determined from the client IP address. In this case, either the first three octets of the IPv4 address or the whole IPv6 address are used to calculate the hash value. The method guarantees that requests from the same address get to the same server unless it is not available.  

#### How to set up the `Round Robin` strategy?
In order to choose the strategy type, we need to specify a `NGINX_LOAD_BALANCING_STRATEGY` variable value in `variables.sh`:  
- when left empty, then `Round Robin` strategy is used  
- `least_conn` should be used to enable `Least Connections` strategy (more on this strategy can be found [here](#how-to-set-up-the-least-connections-strategy))  
- `ip_hash` should be used to enable `IP Hash` strategy (more on this strategy can be found [here](#how-to-set-up-the-ip-hash-strategy))  

Other variables in `variables.sh` file can be set up the same as in the [1-n model paragraph](#how-to-create-a-n-1-model). We can also change the strategy type for the 1-1 model, but testing different load balancing strategies for only a single target is probably not worth the time.  
So our `variables.sh` file should look like this:
```shell
#!/bin/bash

export RESOURCE_GROUP_NAME=iaas-rg
export RESOURCE_GROUP_LOCATION=westeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
export VM_SERVER_PRIVATE_IP=("10.0.1.4" "10.0.1.8")
export VM_MASTER_DB_PRIVATE_IP=10.0.1.5
export VM_REPLICA_DB_PRIVATE_IP=
export VM_OBSERVABILITY_PRIVATE_IP=10.0.1.6
export VM_NGINX_PRIVATE_IP=10.0.1.7
export NGINX_LOAD_BALANCING_STRATEGY=
```

When the deployment script is already in progress, we can check whether our desired strategy has been selected. The default strategy (`Round Robin`) will have the following message at the beginning of the logs:  
```shell
NGINX_LOAD_BALANCING_STRATEGY is empty or not set. Proceeding with default strategy "round robin"
```
And it can be found here:  
```shell
-----------------------------------------------------
Creating copies of files that need to be changed     
-----------------------------------------------------
Reading variables.sh
VM_REPLICA_DB_PRIVATE_IP is empty or not set. Replication DB will not be created
NGINX_LOAD_BALANCING_STRATEGY is empty or not set. Proceeding with default strategy "round robin"
-----------------------------------------------------
Deploying infrastructure
terraform.tfvars
...
```

#### How to set up the `Least Connections` strategy?
In order to choose the `Least Connections` strategy type, we need to specify the `least_conn` value for the variable `NGINX_LOAD_BALANCING_STRATEGY` in `variables.sh`:
```shell
#!/bin/bash

export RESOURCE_GROUP_NAME=iaas-rg
export RESOURCE_GROUP_LOCATION=westeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
export VM_SERVER_PRIVATE_IP=("10.0.1.4" "10.0.1.8")
export VM_MASTER_DB_PRIVATE_IP=10.0.1.5
export VM_REPLICA_DB_PRIVATE_IP=
export VM_OBSERVABILITY_PRIVATE_IP=10.0.1.6
export VM_NGINX_PRIVATE_IP=10.0.1.7
export NGINX_LOAD_BALANCING_STRATEGY=least_conn
```

When the deployment script is already in progress, we can check whether our desired strategy has been selected. The `Least Connections` strategy will have the following message:  
```shell
VM_REPLICA_DB_PRIVATE_IP is empty or not set. Replication DB will not be created
```
And it can be found here:
```shell
-----------------------------------------------------
Creating copies of files that need to be changed     
-----------------------------------------------------
Reading variables.sh
VM_REPLICA_DB_PRIVATE_IP is empty or not set. Replication DB will not be created
Strategy "least_conn" selected for nginx load balancer
-----------------------------------------------------
Deploying infrastructure
terraform.tfvars
...
```

#### How to set up the `IP Hash` strategy?
In order to choose the `IP Hash` strategy type, we need to specify the `ip_hash` value for the variable `NGINX_LOAD_BALANCING_STRATEGY` in `variables.sh`:
```shell
#!/bin/bash

export RESOURCE_GROUP_NAME=iaas-rg
export RESOURCE_GROUP_LOCATION=westeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
export VM_SERVER_PRIVATE_IP=("10.0.1.4" "10.0.1.8")
export VM_MASTER_DB_PRIVATE_IP=10.0.1.5
export VM_REPLICA_DB_PRIVATE_IP=
export VM_OBSERVABILITY_PRIVATE_IP=10.0.1.6
export VM_NGINX_PRIVATE_IP=10.0.1.7
export NGINX_LOAD_BALANCING_STRATEGY=ip_hash
```

When the deployment script is already in progress, we can check whether our desired strategy has been selected. The `IP Hash` strategy will have the following message:  
```shell
Strategy "ip_hash" selected for nginx load balancer
```
And it can be found here:
```shell
-----------------------------------------------------
Creating copies of files that need to be changed     
-----------------------------------------------------
Reading variables.sh
VM_REPLICA_DB_PRIVATE_IP is empty or not set. Replication DB will not be created
Strategy "ip_hash" selected for nginx load balancer
-----------------------------------------------------
Deploying infrastructure
terraform.tfvars
...
```

#### How to change the strategy on a deployed VM?
Once we are logged in to the nginx virtual machine we can check the content of the `/etc/nginx/sites-enabled/lb` file.  
For the `Round Robin` strategy the file should look like this:  
```bash
upstream backend {
    server 10.0.1.4:8080;
server 10.0.1.8:8080;

}

server {
    listen 8080;

    location / {
        proxy_pass http://backend;
        include proxy_params;
    }
}
```
For the `Least Connections` strategy the file should look like this:  
```bash
upstream backend {
    least_conn;
server 10.0.1.4:8080;
server 10.0.1.8:8080;

}

server {
    listen 8080;

    location / {
        proxy_pass http://backend;
        include proxy_params;
    }
}
```
For the `IP Hash` strategy the file should look like this:
```bash
upstream backend {
    ip_hash;
server 10.0.1.4:8080;
server 10.0.1.8:8080;

}

server {
    listen 8080;

    location / {
        proxy_pass http://backend;
        include proxy_params;
    }
}
```
In order to change the strategy we can simply edit this file and then run the following command to reload the nginx configuration:  
```shell
sudo nginx -s reload
```

??? example
    The entire strategy change process is presented below:
    ```
    adminuser@iaas-vm-nginx:~$ cat /etc/nginx/sites-enabled/lb
    upstream backend {
    server 10.0.1.4:8080;
    server 10.0.1.8:8080;
    
    }
    
    server {
    listen 8080;
    
        location / {
            proxy_pass http://backend;
            include proxy_params;
        }
    }
    adminuser@iaas-vm-nginx:~$ sudo vi  /etc/nginx/sites-enabled/lb
    adminuser@iaas-vm-nginx:~$ sudo nginx -s reload
    adminuser@iaas-vm-nginx:~$ cat /etc/nginx/sites-enabled/lb
    upstream backend {
    least_conn;
    server 10.0.1.4:8080;
    server 10.0.1.8:8080;
    
    }
    
    server {
    listen 8080;
    
        location / {
            proxy_pass http://backend;
            include proxy_params;
        }
    }
    adminuser@iaas-vm-nginx:~$
    ```

### How to clean up the environment?
When the test environment is no longer needed we can run `destroy.sh` script to clean up the environment:
```shell
./destroy.sh
```
Here we will be asked whether we want to destroy the test environment (and stop paying for the resources that we no longer need):  
```shell
Are you sure that you want to destroy the test environment? (y/n)
```
And after some time we should see the confirmation that the resources have been successfully deleted:
```shell
...

Destroy complete! Resources: 16 destroyed.
-----------------------------------------------------
Restoring original files
-----------------------------------------------------
The test environment has been cleaned
```

## Troubleshooting
### Server app not responding
Sometimes we may encounter a scenario in which, despite the message of a successful deployment, the server app is not responding.  
The first thing that we should do is to log into one of the server VMs and check with `htop` that the configuration has already been finished. Due to the fact that server VMs do not have their own public IP we need to SSH to the nginx VM or the observability VM and then SSH over the private IP to the desired server VM to inspect it.  

??? example
    Below is an example of checking the server VM status:
    ```shell
    user@laptop:~$ ssh -i .ssh/azure_test-01-rg_key adminuser@51.136.19.24
    The authenticity of host '51.136.19.24 (51.136.19.24)' can't be established.
    ED25519 key fingerprint is SHA256:b9ygkcB5YPMaiDjklwIIM+fCTIp1BOfAnt/pwPMgVK8.
    This key is not known by any other names
    Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
    Warning: Permanently added '51.136.19.24' (ED25519) to the list of known hosts.
    Welcome to Ubuntu 22.04.3 LTS (GNU/Linux 6.2.0-1018-azure x86_64)
    
    * Documentation:  https://help.ubuntu.com
    * Management:     https://landscape.canonical.com
    * Support:        https://ubuntu.com/advantage
    
    System information disabled due to load higher than 1.0
    
    Expanded Security Maintenance for Applications is not enabled.
    
    10 updates can be applied immediately.
    7 of these updates are standard security updates.
    To see these additional updates run: apt list --upgradable
    
    Enable ESM Apps to receive additional future security updates.
    See https://ubuntu.com/esm or run: sudo pro status
    
    
    
    The programs included with the Ubuntu system are free software;
    the exact distribution terms for each program are described in the
    individual files in /usr/share/doc/*/copyright.
    
    Ubuntu comes with ABSOLUTELY NO WARRANTY, to the extent permitted by
    applicable law.
    
    To run a command as administrator (user "root"), use "sudo <command>".
    See "man sudo_root" for details.
    
    adminuser@iaas-vm-nginx:~$ vi .ssh/id_rsa
    adminuser@iaas-vm-nginx:~$ echo ".ssh/id_rsa is my private key which is complementary to the PUBLIC_KEY_PATH I've specified during environment setup"
    .ssh/id_rsa is my private key which is complementary to the PUBLIC_KEY_PATH I've specified during environment setup
    adminuser@iaas-vm-nginx:~$ chmod 600 .ssh/id_rsa
    adminuser@iaas-vm-nginx:~$ ssh -i .ssh/id_rsa adminuser@10.0.1.4
    The authenticity of host '10.0.1.4 (10.0.1.4)' can't be established.
    ED25519 key fingerprint is SHA256:ZNNrM/K4jeoQR1FTJyXtveYJ7j3m5XyCxtnseuZmJwo.
    This key is not known by any other names
    Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
    Warning: Permanently added '10.0.1.4' (ED25519) to the list of known hosts.
    Welcome to Ubuntu 22.04.3 LTS (GNU/Linux 6.2.0-1018-azure x86_64)
    
    * Documentation:  https://help.ubuntu.com
    * Management:     https://landscape.canonical.com
    * Support:        https://ubuntu.com/advantage
    
    System information disabled due to load higher than 1.0
    
    Expanded Security Maintenance for Applications is not enabled.
    
    10 updates can be applied immediately.
    7 of these updates are standard security updates.
    To see these additional updates run: apt list --upgradable
    
    2 additional security updates can be applied with ESM Apps.
    Learn more about enabling ESM Apps service at https://ubuntu.com/esm
    
    
    
    The programs included with the Ubuntu system are free software;
    the exact distribution terms for each program are described in the
    individual files in /usr/share/doc/*/copyright.
    
    Ubuntu comes with ABSOLUTELY NO WARRANTY, to the extent permitted by
    applicable law.
    
    To run a command as administrator (user "root"), use "sudo <command>".
    See "man sudo_root" for details.
    
    adminuser@iaas-vm-server-app-0:~$ htop
    ```

If it turns out that there is now more configuration activity on the VM and the server up is still not responding, then we can check whether the properties in `application.properties` file have been configured properly.  
Before any configuration the `application.properties` file look like this:  
```
spring.datasource.url=%datasource_url%
spring.datasource.username=%datasource_username%
spring.datasource.password=%datasource_password%
management.endpoints.web.exposure.include=health,metrics,prometheus,loggers
management.endpoint.metrics.enabled=true
management.endpoint.info.enabled=true
management.endpoint.prometheus.enabled=true
management.prometheus.metrics.export.enabled=true
```
The following placeholders are replaced during configuration process:  
- `%datasource_url%` - is replaced with the correct database URL  
- `%datasource_username%` - is replaced with the value we have specified for the `MYSQL_ADMIN_USER` variable in the `variables.sh` file  
- `%datasource_password%` - is replaced with the value we have specified for the `MYSQL_ADMIN_PASSWORD` variable in the `variables.sh` file  

The `application.properties` file can be found under following filepath: `/home/adminuser/customdata/23Z-INZ/src/main/resources/application.properties`.  

??? example
    ```
    adminuser@iaas-vm-nginx:~$ ssh -i .ssh/id_rsa adminuser@10.0.1.4
    Welcome to Ubuntu 22.04.3 LTS (GNU/Linux 6.2.0-1018-azure x86_64)
    
    * Documentation:  https://help.ubuntu.com
    * Management:     https://landscape.canonical.com
    * Support:        https://ubuntu.com/advantage
    
    System information as of Sat Dec  9 13:26:10 UTC 2023
    
    System load:  0.0               Processes:             115
    Usage of /:   7.6% of 28.89GB   Users logged in:       0
    Memory usage: 41%               IPv4 address for eth0: 10.0.1.4
    Swap usage:   0%
    
    
    Expanded Security Maintenance for Applications is not enabled.
    
    10 updates can be applied immediately.
    7 of these updates are standard security updates.
    To see these additional updates run: apt list --upgradable
    
    2 additional security updates can be applied with ESM Apps.
    Learn more about enabling ESM Apps service at https://ubuntu.com/esm
    
    
    Last login: Sat Dec  9 13:18:56 2023 from 10.0.1.7
    To run a command as administrator (user "root"), use "sudo <command>".
    See "man sudo_root" for details.
    
    adminuser@iaas-vm-server-app-0:~$ cat ../customdata/23Z-INZ/src/main/resources/application.properties
    spring.datasource.url=jdbc:mysql://10.0.1.5:3306/cache
    spring.datasource.username=worker
    spring.datasource.password=wo^Ker_123
    management.endpoints.web.exposure.include=health,metrics,prometheus,loggers
    management.endpoint.metrics.enabled=true
    management.endpoint.info.enabled=true
    management.endpoint.prometheus.enabled=true
    management.prometheus.metrics.export.enabled=trueadminuser@iaas-vm-server-app-0:~$
    ```

If anything differs from what we expect, then we can edit this file and try to run the application. To do this we need to be in the `/home/customdata/23Z-INZ` directory and execute here following command:  
```shell
sudo mvn spring-boot:run
```

If it does not work, then the best and the quickest option is to destroy the environment and create it once again.