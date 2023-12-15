#!/bin/bash

echo "
# VM for proxysql
resource \"azurerm_network_interface\" \"iaas-nic-proxysql\" {
  name                = \"iaas-nic-proxysql\"
  location            = azurerm_resource_group.iaas-rg.location
  resource_group_name = azurerm_resource_group.iaas-rg.name

  ip_configuration {
    name                          = \"iaas-internal-proxysql\"
    subnet_id                     = azurerm_subnet.iaas-subnet-01.id
    private_ip_address_allocation = \"Static\"
    private_ip_address            = var.proxysql_private_ip
  }

  tags = {
    environment = \"IaaS\"
  }
}

resource \"azurerm_linux_virtual_machine\" \"iaas-vm-proxysql\" {
  name                  = \"iaas-vm-proxysql\"
  resource_group_name   = azurerm_resource_group.iaas-rg.name
  location              = azurerm_resource_group.iaas-rg.location
  size                  = \"Standard_B1s\"
  admin_username        = var.admin_username
  network_interface_ids = [azurerm_network_interface.iaas-nic-proxysql.id]

  custom_data = filebase64(\"customdata_proxysql.tpl\")

  admin_ssh_key {
    username   = var.admin_username
    public_key = file(var.public_key_path)
  }

  os_disk {
    storage_account_type = \"Standard_LRS\"
    caching              = \"ReadWrite\"
  }

  source_image_reference {
    publisher = \"Canonical\"
    offer     = \"0001-com-ubuntu-server-jammy\"
    sku       = \"22_04-lts\"
    version   = \"latest\"
  }

  depends_on = [azurerm_linux_virtual_machine.iaas-vm-master-db]

  tags = {
    environment = \"IaaS\"
  }
}" >> ./main.tf