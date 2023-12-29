# Platform as a Service

## About
This section describes steps required to configure the `PaaS` (Platform as a Service) test environment. This environment is set up in Microsoft Azure and uses `Azure Spring Apps`. 
More information about `PaaS` in Microsoft Azure cloud can be found [here](https://azure.microsoft.com/en-us/resources/cloud-computing-dictionary/what-is-paas). 
More information about `Azure Spring Apps` can be found [here](https://azure.microsoft.com/en-us/products/spring-apps). 

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

### How to create a 1-1 model?
Let's assume that we start in the root directory of the project. In order to create a model go to the `src/main/IaC/PaaS` directory:  
```shell
cd src/main/IaC/PaaS
```
Now we need to configure the environment variables. To do this we need to edit the `variables.sh` file which look like this:  
```bash
#!/bin/sh

export RESOURCE_GROUP_NAME=
export RESOURCE_GROUP_LOCATION=
export MYSQL_ADMIN_USER=
export MYSQL_ADMIN_PASSWORD=
export READ_REPLICA_DB_COUNT=
export SPRING_APP_REPLICAS_COUNT=
export VM_ADMIN_USERNAME=
export PUBLIC_KEY_PATH=
```
Below is an explanation of each variable:  
- `RESOURCE_GROUP_NAME` - resource group name which will be created and in which all other resources will be created  
- `RESOURCE_GROUP_LOCATION` - Azure region (location) in which resource group should be located. Available names can be checked [here](https://azure.microsoft.com/en-us/explore/global-infrastructure/geographies/#geographies). It is important to choose a region which supports [Azure Spring Apps](https://azure.microsoft.com/en-us/products/spring-apps) because not all of them do. For example `northeurope` or `westeurope` can be chosen, but `polandcentral` is not suitable in this case  
- `MYSQL_ADMIN_USER` - admin username that will be used to access the database  
- `MYSQL_ADMIN_PASSWORD` - password for the `MYSQL_ADMIN_USER`  
- `READ_REPLICA_DB_COUNT` - number of read replica databases that we want to create. In 1-1 model this variable should be set to `0`  
- `SPRING_APP_REPLICAS_COUNT` - number of the server app replicas that should be created. In 1-1 model this variable should be set to `1`  
- `VM_ADMIN_USERNAME` - admin username that will be used to access the observability VM  
- `PUBLIC_KEY_PATH` - path to the public key created for the `VM_ADMIN_USERNAME` to access the observability VM. It is suggested to use following command to generate the ssh key pair: `ssh-keygen -t rsa`. For more information about the ssh keys you can read [this article](https://cloud.ibm.com/docs/power-iaas?topic=power-iaas-creating-ssh-key).  
  
To sum up all the above our `variables.sh` file for 1-1 model should look like this:  
```bash
#!/bin/sh

export RESOURCE_GROUP_NAME=paas-spring-rg
export RESOURCE_GROUP_LOCATION=northeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export READ_REPLICA_DB_COUNT=0
export SPRING_APP_REPLICAS_COUNT=1
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
```
Now we can run `deploy.sh` script to create the test environment:  
```shell
./deploy.sh
```
When the script finishes its execution we should see confirmation that our resources have been created successfully:
```shell
Cloud resources creations has finished
Name                    Location     ResourceGroup    Public Url                                                                 Production Deployment    Provisioning State    CPU    Memory    Running Instance    Registered Instance
    Persistent Storage    Bind Service Registry    Bind Application Configuration Service
----------------------  -----------  ---------------  -------------------------------------------------------------------------  -----------------------  --------------------  -----  --------  ------------------  -------------------
--  --------------------  -----------------------  ----------------------------------------
paas-spring-server-app  northeurope  paas-spring-rg   https://paas-spring-apps-svc-paas-spring-server-app.azuremicroservices.io  default                  Succeeded             1      1Gi       1/1                 0/1
    -                     -                        -
-----------------------------------------------------
-----------------------------------------------------
Deploying observability infrastructure

Terraform used the selected providers to generate the following execution plan. Resource actions are indicated with the following symbols:
  + create

Terraform will perform the following actions:
...

Apply complete! Resources: 8 added, 0 changed, 0 destroyed.
```
Terraform logs have been skipped in the example in order not to reveal any sensitive data, like Azure subscription ID, email address, etc.

When the environment is no longer needed we can destroy it by following the steps from [this paragraph](#how-to-clean-up-the-environment).

### How to create a n-1 model?
The steps to create a n-1 model are almost the same as the ones executed previously for 1-1 model.  
The only difference is the value used for `SPRING_APP_REPLICAS_COUNT`. Here we should specify the number of replicas that we want to use, for example `3`.  

!!! Warning  
    The exact number of replicas that you can create may depend on your [subscription type](https://learn.microsoft.com/en-us/microsoft-365/enterprise/subscriptions-licenses-accounts-and-tenants-for-microsoft-cloud-offerings?view=o365-worldwide) and/or your [resource quotas](https://learn.microsoft.com/en-us/azure/quotas/quotas-overview).

An exemplary `variables.sh` file for this scenario is presented below:  
```bash
#!/bin/sh

export RESOURCE_GROUP_NAME=paas-spring-rg
export RESOURCE_GROUP_LOCATION=northeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export READ_REPLICA_DB_COUNT=0
export SPRING_APP_REPLICAS_COUNT=3
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
```
The command used to create the test environment is the same as previously:  
```shell
./deploy.sh
```
When the script finishes its execution we should see confirmation that our resources have been created successfully:
```shell
-----------------------------------------------------
Cloud resources creations has finished
Name                    Location     ResourceGroup    Public Url                                                                 Production Deployment    Provisioning State    CPU    Memory    Running Instance    Registered Instance
    Persistent Storage    Bind Service Registry    Bind Application Configuration Service
----------------------  -----------  ---------------  -------------------------------------------------------------------------  -----------------------  --------------------  -----  --------  ------------------  -------------------
--  --------------------  -----------------------  ----------------------------------------
paas-spring-server-app  northeurope  paas-spring-rg   https://paas-spring-apps-svc-paas-spring-server-app.azuremicroservices.io  default                  Succeeded             1      1Gi       3/3                 0/3
    -                     -                        -
-----------------------------------------------------
-----------------------------------------------------
Deploying observability infrastructure
...

Apply complete! Resources: 8 added, 0 changed, 0 destroyed.
```

Terraform logs have been skipped in the example in order not to reveal any sensitive data, like Azure subscription ID, email address, etc.

When the environment is no longer needed we can destroy it by following the steps from [this paragraph](#how-to-clean-up-the-environment).

### How to create a master-slave database configuration?
As mentioned in the variables description earlier, we will need to modify value for 1 variable (in comparison to previous scenarios):  
- `READ_REPLICA_DB_COUNT` - needs to be set to the number of read replica databases we want to create  
So our `variables.sh` file should look like this:  
```bash
#!/bin/sh

export RESOURCE_GROUP_NAME=paas-spring-rg
export RESOURCE_GROUP_LOCATION=northeurope
export MYSQL_ADMIN_USER=worker
export MYSQL_ADMIN_PASSWORD=wo^Ker_123
export READ_REPLICA_DB_COUNT=2
export SPRING_APP_REPLICAS_COUNT=3
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
```
And now we cen deploy the resources:  
```shell
./deploy.sh
```
When the script finishes its execution we should see confirmation that our resources have been created successfully:
```shell
-----------------------------------------------------
Cloud resources creations has finished
Name                    Location     ResourceGroup    Public Url                                                                 Production Deployment    Provisioning State    CPU    Memory    Running Instance    Registered Instance
    Persistent Storage    Bind Service Registry    Bind Application Configuration Service
----------------------  -----------  ---------------  -------------------------------------------------------------------------  -----------------------  --------------------  -----  --------  ------------------  -------------------
--  --------------------  -----------------------  ----------------------------------------
paas-spring-server-app  northeurope  paas-spring-rg   https://paas-spring-apps-svc-paas-spring-server-app.azuremicroservices.io  default                  Succeeded             1      1Gi       3/3                 0/3
    -                     -                        -
-----------------------------------------------------
-----------------------------------------------------
Deploying observability infrastructure
...

Apply complete! Resources: 8 added, 0 changed, 0 destroyed.
```

Terraform logs have been skipped in the example in order not to reveal any sensitive data, like Azure subscription ID, email address, etc.

!!! Note
    If you need more information about connecting the slave database to master database, please refer to these 2 articles:  
    - [How to configure Azure Database for MySQL - Flexible Server data-in replication](https://learn.microsoft.com/en-us/azure/mysql/flexible-server/how-to-data-in-replication?tabs=bash%2Ccommand-line#configure-the-source-mysql-server)  
    - [MySQL Configuring Replication](https://dev.mysql.com/doc/refman/5.7/en/replication-configuration.html)  
    - [Read replicas in Azure Database for MySQL - Flexible Server](https://learn.microsoft.com/en-us/azure/mysql/flexible-server/concepts-read-replicas)  

When the environment is no longer needed we can destroy it by following the steps from [this paragraph](#how-to-clean-up-the-environment).

### How to clean up the environment?
When the test environment is no longer needed we can run `destroy.sh` script to clean up the environment:
```shell
./destroy.sh
```
Here we will be asked whether we want to destroy the test environment (and stop paying for the resources that we no longer need):  
```shell
...
Are you sure that you want to destroy the test environment? (y/n)
```

When the script finishes its work we should see a message similar to this one:  
```shell
...
Destroy complete! Resources: 8 destroyed.
-----------------------------------------------------
Restoring original files
-----------------------------------------------------
The test environment has been cleaned
```