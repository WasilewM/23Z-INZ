terraform {
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "3.0.0"
    }
  }
}

provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "paas-rg" {
  name     = "paas-rg"
  location = "polandcentral"

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_virtual_network" "paas-vnet" {
  name                = "paas-network"
  resource_group_name = azurerm_resource_group.paas-rg.name
  location            = azurerm_resource_group.paas-rg.location
  address_space       = ["10.0.1.0/24"]

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_subnet" "paas-subnet-01" {
  name                 = "paas-subnet-01"
  resource_group_name  = azurerm_resource_group.paas-rg.name
  virtual_network_name = azurerm_virtual_network.paas-vnet.name
  address_prefixes     = ["10.0.1.0/26"]
}

resource "azurerm_network_security_group" "paas-sg-01" {
  name                = "paas-sg-01"
  location            = azurerm_resource_group.paas-rg.location
  resource_group_name = azurerm_resource_group.paas-rg.name

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_network_security_rule" "paas-rule-dev-01" {
  name                        = "paas-rule-dev-01"
  priority                    = 100
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "*"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  resource_group_name         = azurerm_resource_group.paas-rg.name
  network_security_group_name = azurerm_network_security_group.paas-sg-01.name
}

resource "azurerm_subnet_network_security_group_association" "paas-rule-dev-01-sga" {
  subnet_id                 = azurerm_subnet.paas-subnet-01.id
  network_security_group_id = azurerm_network_security_group.paas-sg-01.id
}

# VM for observability
resource "azurerm_public_ip" "paas-public-ip-observability" {
  name                = "paas-public-ip-observability"
  resource_group_name = azurerm_resource_group.paas-rg.name
  location            = azurerm_resource_group.paas-rg.location
  allocation_method   = "Dynamic"

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_network_interface" "paas-nic-observability" {
  name                = "paas-nic-observability"
  location            = azurerm_resource_group.paas-rg.location
  resource_group_name = azurerm_resource_group.paas-rg.name

  ip_configuration {
    name                          = "paas-internal-observability"
    subnet_id                     = azurerm_subnet.paas-subnet-01.id
    private_ip_address_allocation = "Static"
    private_ip_address            = var.observability_private_ip
    public_ip_address_id          = azurerm_public_ip.paas-public-ip-observability.id
  }

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_linux_virtual_machine" "paas-vm-observability" {
  name                  = "paas-vm-observability"
  resource_group_name   = azurerm_resource_group.paas-rg.name
  location              = azurerm_resource_group.paas-rg.location
  size                  = "Standard_B1s"
  admin_username        = var.admin_username
  network_interface_ids = [azurerm_network_interface.paas-nic-observability.id]

  custom_data = filebase64("customdata_observability.tpl")

  admin_ssh_key {
    username   = var.admin_username
    public_key = file(var.public_key_path)
  }

  os_disk {
    storage_account_type = "Standard_LRS"
    caching              = "ReadWrite"
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "0001-com-ubuntu-server-jammy"
    sku       = "22_04-lts"
    version   = "latest"
  }

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_service_plan" "paas-service-plan" {
  name                = "paas-service-plan"
  resource_group_name = azurerm_resource_group.paas-rg.name
  location            = azurerm_resource_group.paas-rg.location
  os_type             = "Linux"
  sku_name            = "B1"

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_linux_web_app" "paas-server-app" {
  name                = "paas-server-app"
  resource_group_name = azurerm_resource_group.paas-rg.name
  location            = azurerm_service_plan.paas-service-plan.location
  service_plan_id     = azurerm_service_plan.paas-service-plan.id

  site_config {
    application_stack {
      java_server         = "JAVA"
      java_server_version = 17
      java_version        = 17
    }
  }

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_subnet" "paas-subnet-02" {
  name                 = "paas-subnet-02"
  resource_group_name  = azurerm_resource_group.paas-rg.name
  virtual_network_name = azurerm_virtual_network.paas-vnet.name
  address_prefixes     = ["10.0.1.64/26"]
  service_endpoints    = ["Microsoft.Storage"]

  delegation {
    name = "fs"

    service_delegation {
      name = "Microsoft.DBforMySQL/flexibleServers"
      actions = [
        "Microsoft.Network/virtualNetworks/subnets/join/action",
      ]
    }
  }
}

resource "azurerm_private_dns_zone" "paas-priv-dns-zone" {
  name                = "paas-priv-dns-zone.mysql.database.azure.com"
  resource_group_name = azurerm_resource_group.paas-rg.name

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_private_dns_zone_virtual_network_link" "paas-priv-dns-zone-vnet-link" {
  name                  = "mysqlfsVnetZone-paas-priv-dns-zone-vnet-link.com"
  private_dns_zone_name = azurerm_private_dns_zone.paas-priv-dns-zone.name
  resource_group_name   = azurerm_resource_group.paas-rg.name
  virtual_network_id    = azurerm_virtual_network.paas-vnet.id

  depends_on = [azurerm_subnet.paas-subnet-02]

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_mysql_flexible_server" "paas-mysql-flex-serv" {
  location                     = azurerm_resource_group.paas-rg.location
  name                         = "paas-mysql-flex-serv"
  resource_group_name          = azurerm_resource_group.paas-rg.name
  administrator_login          = var.mysql_administrator_login
  administrator_password       = var.mysql_administrator_password
  backup_retention_days        = 7
  delegated_subnet_id          = azurerm_subnet.paas-subnet-02.id
  geo_redundant_backup_enabled = false
  private_dns_zone_id          = azurerm_private_dns_zone.paas-priv-dns-zone.id
  sku_name                     = "B_Standard_B1ms"
  version                      = "8.0.21"

  maintenance_window {
    day_of_week  = 0
    start_hour   = 8
    start_minute = 0
  }
  storage {
    iops    = 360
    size_gb = 20
  }

  depends_on = [azurerm_private_dns_zone_virtual_network_link.paas-priv-dns-zone-vnet-link]
}

resource "azurerm_mysql_flexible_database" "paas-mysql-flex-db" {
  charset             = "utf8mb4"
  collation           = "utf8mb4_unicode_ci"
  name                = "paas-mysql-flex-db"
  resource_group_name = azurerm_resource_group.paas-rg.name
  server_name         = azurerm_mysql_flexible_server.paas-mysql-flex-serv.name
}