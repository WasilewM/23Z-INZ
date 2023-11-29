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

resource "azurerm_virtual_network" "paas-vnet" {
  name                = "paas-network"
  resource_group_name = var.resource_group_name
  location            = var.resource_group_location
  address_space       = ["10.0.1.0/24"]

  tags = {
    environment = "Observability"
  }
}

resource "azurerm_subnet" "paas-subnet-01" {
  name                 = "paas-subnet-01"
  resource_group_name  = var.resource_group_name
  virtual_network_name = azurerm_virtual_network.paas-vnet.name
  address_prefixes     = ["10.0.1.0/26"]
}

resource "azurerm_network_security_group" "paas-sg-01" {
  name                = "paas-sg-01"
  location            = var.resource_group_location
  resource_group_name = var.resource_group_name

  tags = {
    environment = "Observability"
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
  resource_group_name         = var.resource_group_name
  network_security_group_name = azurerm_network_security_group.paas-sg-01.name
}

resource "azurerm_subnet_network_security_group_association" "paas-rule-dev-01-sga" {
  subnet_id                 = azurerm_subnet.paas-subnet-01.id
  network_security_group_id = azurerm_network_security_group.paas-sg-01.id
}

# VM for observability
resource "azurerm_public_ip" "paas-public-ip-observability" {
  name                = "paas-public-ip-observability"
  resource_group_name = var.resource_group_name
  location            = var.resource_group_location
  allocation_method   = "Dynamic"

  tags = {
    environment = "Observability"
  }
}

resource "azurerm_network_interface" "paas-nic-observability" {
  name                = "paas-nic-observability"
  location            = var.resource_group_location
  resource_group_name = var.resource_group_name

  ip_configuration {
    name                          = "paas-internal-observability"
    subnet_id                     = azurerm_subnet.paas-subnet-01.id
    private_ip_address_allocation = "Static"
    private_ip_address            = var.observability_private_ip
    public_ip_address_id          = azurerm_public_ip.paas-public-ip-observability.id
  }

  tags = {
    environment = "Observability"
  }
}

resource "azurerm_linux_virtual_machine" "paas-vm-observability" {
  name                  = "paas-vm-observability"
  resource_group_name   = var.resource_group_name
  location              = var.resource_group_location
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
    environment = "Observability"
  }
}