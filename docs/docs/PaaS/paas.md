# Platform as a Service

## About

## User guide

!!! Note  
    Please note that before going any further we need to have [Azure CLI](https://learn.microsoft.com/en-us/cli/azure/install-azure-cli) installed. Apart from that we need to be logged into the Azure account in our console by running:  
    ```shell
    az login
    ```
    This will redirect us to a browser to confirm the identity.

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
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
export SPRING_APP_REPLICAS_COUNT=
export VM_ADMIN_USERNAME=
export PUBLIC_KEY_PATH=
```
Below is an explanation of each variable:  
- `RESOURCE_GROUP_NAME` - resource group name which will be created and in which all other resources will be created  
- `RESOURCE_GROUP_LOCATION` - Azure region (location) in which resource group should be located. Region names can be checked [here](https://azure.microsoft.com/en-us/explore/global-infrastructure/geographies/#geographies). It is important to choose a region which supports [Azure Spring Apps](https://azure.microsoft.com/en-us/products/spring-apps) because not all of them do. For example `northeurope` or `westeurope` can be chosen, but `polandcentral` is not suitable in this case.  
- `MYSQL_ADMIN_USER` - admin username that will be used to access the database  
- `MYSQL_REPLICATION_PASSWORD` - password for the `MYSQL_ADMIN_USER`  
- `MYSQL_REPLICATION_USER` - username for the replication user. In 1-1 model this variable should remain empty  
- `MYSQL_REPLICATION_PASSWORD` - password for the `MYSQL_REPLICATION_USER`. In 1-1 model this variable should remain empty  
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
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
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
# terraform logs containing you sensitive information, like ssh-rsa key, Azure subscription ID
Apply complete! Resources: 8 added, 0 changed, 0 destroyed.
```

When the environment is no longer needed we can destroy it by following the steps from [this paragraph](#how-to-clean-up-the-environment)

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
export MYSQL_REPLICATION_USER=
export MYSQL_REPLICATION_PASSWORD=
export SPRING_APP_REPLICAS_COUNT=3
export VM_ADMIN_USERNAME=adminuser
export PUBLIC_KEY_PATH=~/.ssh/azure_test-01-rg_key.pub
```
The command used to create the test environment is the same as previously:  
```shell
./deploy.sh
```

### How to clean up the environment?
When the test environment is no longer needed we can run `destroy.sh` script to clean up the environment:
```shell
./destroy.sh
```
Here we will be asked twice whether we want to destroy the resources. In order to fully clean the environment (and stop paying for the resources wwe no longer need), we need to accept both messages:  
```shell
...
Plan: 0 to add, 0 to change, 8 to destroy.

Do you really want to destroy all resources?
  Terraform will destroy all your managed infrastructure, as shown above.
  There is no undo. Only 'yes' will be accepted to confirm.

  Enter a value:

...

Destroy complete! Resources: 8 destroyed.
Are you sure you want to perform this operation? (y/n): 
...
```