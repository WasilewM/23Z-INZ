az group create --name paas-spring-rg --location northeurope

az spring create --name paas-spring-apps-svc --resource-group paas-spring-rg

az mysql flexible-server create \
  --resource-group paas-spring-rg \
  --name paas-spring-mysql-db \
  --database-name cache \
  --admin-user worker \
  --admin-password wo^Ker_123 \
	--location northeurope \
	--sku-name Standard_B1ms \
	--version 8.0.21 \
  --yes # Do not prompt for confirmation:
  # Detected current client IP : A.B.C.D
  # Do you want to enable access to client A.B.C.D (y/n):

az spring app create \
	--name paas-spring-server-app \
	--service paas-spring-apps-svc \
	--resource-group paas-spring-rg \
	--assign-endpoint true \
	--runtime-version Java_17

az spring connection create mysql-flexible \
  --resource-group paas-spring-rg \
  --service paas-spring-apps-svc \
  --app paas-spring-server-app \
  --target-resource-group paas-spring-rg \
  --server paas-spring-mysql-db \
  --database cache \
  --secret name=worker secret=wo^Ker_123 \
	--client-type springBoot

az spring app deploy \
  --name paas-spring-server-app \
  --service paas-spring-apps-svc \
  --resource-group paas-spring-rg \
  --artifact-path ../../../../target/server_app-0.1.0.jar \
	--runtime-version Java_17

az spring app list  --resource-group paas-spring-rg --service paas-spring-apps-svc --output table