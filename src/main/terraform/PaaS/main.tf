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

resource "azurerm_resource_group" "test-01-paas-rg" {
  name     = "test-01-paas-rg"
  location = "polandcentral"

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_virtual_network" "test-01-paas-vnet" {
  name                = "test-01-paas-network"
  resource_group_name = azurerm_resource_group.test-01-paas-rg.name
  location            = azurerm_resource_group.test-01-paas-rg.location
  address_space       = ["10.0.1.0/24"]

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_subnet" "test-01-paas-subnet-01" {
  name                 = "test-01-paas-subnet-01"
  resource_group_name  = azurerm_resource_group.test-01-paas-rg.name
  virtual_network_name = azurerm_virtual_network.test-01-paas-vnet.name
  address_prefixes     = ["10.0.1.0/26"]
}

resource "azurerm_network_security_group" "test-01-paas-sg-01" {
  name                = "test-01-paas-sg-01"
  location            = azurerm_resource_group.test-01-paas-rg.location
  resource_group_name = azurerm_resource_group.test-01-paas-rg.name

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_network_security_rule" "test-01-paas-rule-dev-01" {
  name                        = "test-01-paas-rule-dev-01"
  priority                    = 100
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "*"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  resource_group_name         = azurerm_resource_group.test-01-paas-rg.name
  network_security_group_name = azurerm_network_security_group.test-01-paas-sg-01.name
}

resource "azurerm_subnet_network_security_group_association" "test-01-paas-rule-dev-01-sga" {
  subnet_id                 = azurerm_subnet.test-01-paas-subnet-01.id
  network_security_group_id = azurerm_network_security_group.test-01-paas-sg-01.id
}

# VM for observability
resource "azurerm_public_ip" "test-01-paas-public-ip-observability" {
  name                = "test-01-paas-public-ip-observability"
  resource_group_name = azurerm_resource_group.test-01-paas-rg.name
  location            = azurerm_resource_group.test-01-paas-rg.location
  allocation_method   = "Dynamic"
  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_network_interface" "test-01-paas-nic-observability" {
  name                = "test-01-paas-nic-observability"
  location            = azurerm_resource_group.test-01-paas-rg.location
  resource_group_name = azurerm_resource_group.test-01-paas-rg.name

  ip_configuration {
    name                          = "test-01-paas-internal-observability"
    subnet_id                     = azurerm_subnet.test-01-paas-subnet-01.id
    private_ip_address_allocation = "Static"
    private_ip_address            = var.observability_private_ip
    public_ip_address_id          = azurerm_public_ip.test-01-paas-public-ip-observability.id
  }

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_linux_virtual_machine" "test-01-paas-vm-observability" {
  name                  = "test-01-paas-vm-observability"
  resource_group_name   = azurerm_resource_group.test-01-paas-rg.name
  location              = azurerm_resource_group.test-01-paas-rg.location
  size                  = "Standard_B1s"
  admin_username        = var.admin_username
  network_interface_ids = [azurerm_network_interface.test-01-paas-nic-observability.id]

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

resource "azurerm_service_plan" "test-01-paas-service-plan" {
  name                = "test-01-paas-service-plan"
  resource_group_name = azurerm_resource_group.test-01-paas-rg.name
  location            = azurerm_resource_group.test-01-paas-rg.location
  os_type             = "Linux"
  sku_name            = "B1"

  tags = {
    environment = "PaaS"
  }
}

resource "azurerm_linux_web_app" "test-01-paas-server-app" {
  name                = "test-01-paas-server-app"
  resource_group_name = azurerm_resource_group.test-01-paas-rg.name
  location            = azurerm_service_plan.test-01-paas-service-plan.location
  service_plan_id     = azurerm_service_plan.test-01-paas-service-plan.id

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
