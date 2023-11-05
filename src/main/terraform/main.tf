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

resource "azurerm_resource_group" "test-01-rg" {
  name     = "test-01-rg"
  location = "West Europe"
  tags = {
    environment = "test"
  }
}

resource "azurerm_virtual_network" "test-01-vnet" {
  name                = "test-01-network"
  resource_group_name = azurerm_resource_group.test-01-rg.name
  location            = azurerm_resource_group.test-01-rg.location
  address_space       = ["10.0.1.0/24"]
  tags = {
    environment = "test"
  }
}

resource "azurerm_subnet" "test-01-subnet-01" {
  name                 = "test-01-subnet-01"
  resource_group_name  = azurerm_resource_group.test-01-rg.name
  virtual_network_name = azurerm_virtual_network.test-01-vnet.name
  address_prefixes     = ["10.0.1.0/26"]
}

resource "azurerm_network_security_group" "test-01-sg-01" {
  name                = "test-01-sg-01"
  location            = azurerm_resource_group.test-01-rg.location
  resource_group_name = azurerm_resource_group.test-01-rg.name
  tags = {
    environment = "test"
  }
}

resource "azurerm_network_security_rule" "test-01-rule-dev-01" {
  name                        = "test-01-rule-dev-01"
  priority                    = 100
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "*"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  resource_group_name         = azurerm_resource_group.test-01-rg.name
  network_security_group_name = azurerm_network_security_group.test-01-sg-01.name
}

resource "azurerm_subnet_network_security_group_association" "test-01-rule-dev-01-sga" {
  subnet_id                 = azurerm_subnet.test-01-subnet-01.id
  network_security_group_id = azurerm_network_security_group.test-01-sg-01.id
}

resource "azurerm_public_ip" "test-01-public-ip-01" {
  name                = "test-01-public-ip-01"
  resource_group_name = azurerm_resource_group.test-01-rg.name
  location            = azurerm_resource_group.test-01-rg.location
  allocation_method   = "Dynamic"
  tags = {
    environment = "test"
  }
}

resource "azurerm_network_interface" "test-01-nic-01" {
  name                = "test-01-nic-01"
  location            = azurerm_resource_group.test-01-rg.location
  resource_group_name = azurerm_resource_group.test-01-rg.name

  ip_configuration {
    name                          = "test-01-internal-01"
    subnet_id                     = azurerm_subnet.test-01-subnet-01.id
    private_ip_address_allocation = "Dynamic"
    public_ip_address_id          = azurerm_public_ip.test-01-public-ip-01.id
  }

  tags = {
    environment = "test"
  }
}

resource "azurerm_linux_virtual_machine" "test-01-vm" {
  name                  = "test-01-vm"
  resource_group_name   = azurerm_resource_group.test-01-rg.name
  location              = azurerm_resource_group.test-01-rg.location
  size                  = "Standard_B1s"
  admin_username        = "adminuser"
  network_interface_ids = [azurerm_network_interface.test-01-nic-01.id]

  custom_data = filebase64("customdata_observability.tpl")

  admin_ssh_key {
    username   = "adminuser"
    public_key = file("~/.ssh/azure_test-01-rg_key.pub")
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
}