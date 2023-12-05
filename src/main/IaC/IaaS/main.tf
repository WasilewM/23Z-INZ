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

resource "azurerm_resource_group" "iaas-rg" {
  name     = var.resource_group_name
  location = var.resource_group_location

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_virtual_network" "iaas-vnet" {
  name                = "iaas-network"
  resource_group_name = azurerm_resource_group.iaas-rg.name
  location            = azurerm_resource_group.iaas-rg.location
  address_space       = ["10.0.1.0/24"]

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_subnet" "iaas-subnet-01" {
  name                 = "iaas-subnet-01"
  resource_group_name  = azurerm_resource_group.iaas-rg.name
  virtual_network_name = azurerm_virtual_network.iaas-vnet.name
  address_prefixes     = ["10.0.1.0/26"]
}

resource "azurerm_network_security_group" "iaas-sg-01" {
  name                = "iaas-sg-01"
  location            = azurerm_resource_group.iaas-rg.location
  resource_group_name = azurerm_resource_group.iaas-rg.name

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_network_security_rule" "iaas-rule-dev-01" {
  name                        = "iaas-rule-dev-01"
  priority                    = 100
  direction                   = "Inbound"
  access                      = "Allow"
  protocol                    = "Tcp"
  source_port_range           = "*"
  destination_port_range      = "*"
  source_address_prefix       = "*"
  destination_address_prefix  = "*"
  resource_group_name         = azurerm_resource_group.iaas-rg.name
  network_security_group_name = azurerm_network_security_group.iaas-sg-01.name
}

resource "azurerm_subnet_network_security_group_association" "iaas-rule-dev-01-sga" {
  subnet_id                 = azurerm_subnet.iaas-subnet-01.id
  network_security_group_id = azurerm_network_security_group.iaas-sg-01.id
}

# VM for observability
resource "azurerm_public_ip" "iaas-public-ip-observability" {
  name                = "iaas-public-ip-observability"
  resource_group_name = azurerm_resource_group.iaas-rg.name
  location            = azurerm_resource_group.iaas-rg.location
  allocation_method   = "Dynamic"

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_network_interface" "iaas-nic-observability" {
  name                = "iaas-nic-observability"
  location            = azurerm_resource_group.iaas-rg.location
  resource_group_name = azurerm_resource_group.iaas-rg.name

  ip_configuration {
    name                          = "iaas-internal-observability"
    subnet_id                     = azurerm_subnet.iaas-subnet-01.id
    private_ip_address_allocation = "Static"
    private_ip_address            = var.observability_private_ip
    public_ip_address_id          = azurerm_public_ip.iaas-public-ip-observability.id
  }

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_linux_virtual_machine" "iaas-vm-observability" {
  name                  = "iaas-vm-observability"
  resource_group_name   = azurerm_resource_group.iaas-rg.name
  location              = azurerm_resource_group.iaas-rg.location
  size                  = "Standard_B1s"
  admin_username        = var.admin_username
  network_interface_ids = [azurerm_network_interface.iaas-nic-observability.id]

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

  depends_on = [azurerm_linux_virtual_machine.iaas-vm-server-app]

  tags = {
    environment = "IaaS"
  }
}

# VM for MySQL master DB
resource "azurerm_network_interface" "iaas-nic-master-db" {
  name                = "iaas-nic-master-db"
  location            = azurerm_resource_group.iaas-rg.location
  resource_group_name = azurerm_resource_group.iaas-rg.name

  ip_configuration {
    name                          = "iaas-internal-master-db"
    subnet_id                     = azurerm_subnet.iaas-subnet-01.id
    private_ip_address_allocation = "Static"
    private_ip_address            = var.master_db_private_ip
  }

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_linux_virtual_machine" "iaas-vm-master-db" {
  name                  = "iaas-vm-master-db"
  resource_group_name   = azurerm_resource_group.iaas-rg.name
  location              = azurerm_resource_group.iaas-rg.location
  size                  = "Standard_B1s"
  admin_username        = var.admin_username
  network_interface_ids = [azurerm_network_interface.iaas-nic-master-db.id]

  custom_data = filebase64("customdata_db.tpl")

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
    environment = "IaaS"
  }
}

# VM for MySQL replica DB
resource "azurerm_network_interface" "iaas-nic-replica-db" {
  name                = "iaas-nic-replica-db"
  location            = azurerm_resource_group.iaas-rg.location
  resource_group_name = azurerm_resource_group.iaas-rg.name

  ip_configuration {
    name                          = "iaas-internal-replica-db"
    subnet_id                     = azurerm_subnet.iaas-subnet-01.id
    private_ip_address_allocation = "Static"
    private_ip_address            = var.replica_db_private_ip
  }

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_linux_virtual_machine" "iaas-vm-replica-db" {
  name                  = "iaas-vm-replica-db"
  resource_group_name   = azurerm_resource_group.iaas-rg.name
  location              = azurerm_resource_group.iaas-rg.location
  size                  = "Standard_B1s"
  admin_username        = var.admin_username
  network_interface_ids = [azurerm_network_interface.iaas-nic-replica-db.id]

  custom_data = filebase64("customdata_db_replica.tpl")

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

  depends_on = [azurerm_linux_virtual_machine.iaas-vm-master-db]

  tags = {
    environment = "IaaS"
  }
}

# VM for server-app
resource "azurerm_network_interface" "iaas-nic-server-app" {
  for_each            = var.server_app_vms
  name                = "iaas-nic-server-app-${each.key}"
  location            = azurerm_resource_group.iaas-rg.location
  resource_group_name = azurerm_resource_group.iaas-rg.name

  ip_configuration {
    name                          = "iaas-internal-server-app"
    subnet_id                     = azurerm_subnet.iaas-subnet-01.id
    private_ip_address_allocation = "Static"
    private_ip_address            = each.value.private_ip
  }

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_linux_virtual_machine" "iaas-vm-server-app" {
  for_each              = var.server_app_vms
  name                  = "iaas-vm-server-app-${each.key}"
  resource_group_name   = azurerm_resource_group.iaas-rg.name
  location              = azurerm_resource_group.iaas-rg.location
  size                  = "Standard_A1_v2"
  admin_username        = var.admin_username
  network_interface_ids = [azurerm_network_interface.iaas-nic-server-app[each.key].id]

  custom_data = filebase64("customdata_server_app.tpl")

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

  depends_on = [azurerm_linux_virtual_machine.iaas-vm-master-db]

  tags = {
    environment = "IaaS"
  }
}

# VM for load-balancer (nginx)
resource "azurerm_public_ip" "iaas-public-ip-nginx" {
  name                = "iaas-public-ip-nginx"
  resource_group_name = azurerm_resource_group.iaas-rg.name
  location            = azurerm_resource_group.iaas-rg.location
  allocation_method   = "Dynamic"

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_network_interface" "iaas-nic-nginx" {
  name                = "iaas-nic-nginx"
  location            = azurerm_resource_group.iaas-rg.location
  resource_group_name = azurerm_resource_group.iaas-rg.name

  ip_configuration {
    name                          = "iaas-internal-nginx"
    subnet_id                     = azurerm_subnet.iaas-subnet-01.id
    private_ip_address_allocation = "Static"
    private_ip_address            = var.nginx_private_ip
    public_ip_address_id          = azurerm_public_ip.iaas-public-ip-nginx.id
  }

  tags = {
    environment = "IaaS"
  }
}

resource "azurerm_linux_virtual_machine" "iaas-vm-nginx" {
  name                  = "iaas-vm-nginx"
  resource_group_name   = azurerm_resource_group.iaas-rg.name
  location              = azurerm_resource_group.iaas-rg.location
  size                  = "Standard_B1s"
  admin_username        = var.admin_username
  network_interface_ids = [azurerm_network_interface.iaas-nic-nginx.id]

  custom_data = filebase64("customdata_nginx.tpl")

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

  depends_on = [azurerm_linux_virtual_machine.iaas-vm-server-app]

  tags = {
    environment = "IaaS"
  }
}