# Infrastructure as a Service

## About

## User guide

!!! Note  
    Please note that before going any further we need to have [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli) installed. Apart from that we need to be logged into the Azure account in our console by running:  
    ```shell
    az login
    ```
    This will redirect us to a browser to confirm the identity.

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

To sum up all the above our `variables.sh` file for 1-1 model should look like this:
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
The steps to create a n-1 model are almost the same as the ones executed previously for 1-1 model.  
The only difference is the number of IP addresses used for `VM_SERVER_PRIVATE_IP` variable. Here we should specify the internal IPs for the VMs we want to create, for example `("10.0.1.4" "10.0.1.8")` or `("10.0.1.4" "10.0.1.8" "10.0.1.9")`.  

!!! Tip
    Before created numerous VMs we should check our resource quotas to make sure that we are allowed to creat the desired environment. We need to pay attention to the following quotas:  
    - `Total Regional vCPUs` - represents total number of `vCPUs` that we can request  
    - `Standard BS Family vCPUs` - represents the number of `vCPUs` from `Standard BS Family` that we can request. VMs for the database and the nginx load balancer use this `vCPUs`  
    - `Standard Av2 Family vCPUs` - represents the number of `vCPUs` from `Standard Av2 Family` that we can request. VMs for the server application use this `vCPUs`  
    More on quotas topic can be found [here](https://learn.microsoft.com/en-us/azure/quotas/quotas-overview).  
    More on VM families can be found [here](https://azure.microsoft.com/en-us/pricing/details/virtual-machines/series/).  

To sum up all the above our `variables.sh` file for 1-n model should look like this:
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

### How to clean up the environment?
When the test environment is no longer needed we can run `destroy.sh` script to clean up the environment:
```shell
./destroy.sh
```
Here we will be asked whether we want to destroy the resources. In order to clean the environment (and stop paying for the resources wwe no longer need), we need to type `yes` to confirm the resource destruction:
```shell
...
Plan: 0 to add, 0 to change, 16 to destroy.

Do you really want to destroy all resources?
  Terraform will destroy all your managed infrastructure, as shown above.
  There is no undo. Only 'yes' will be accepted to confirm.

  Enter a value:

```
And after some time we should see the confirmation that the resources have been successfully deleted:
```shell
...

Destroy complete! Resources: 16 destroyed.
-----------------------------------------------------
Restoring original files
```