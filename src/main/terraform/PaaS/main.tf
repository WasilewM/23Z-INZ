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
    environment = "test-paas"
  }
}

resource "azurerm_service_plan" "test-01-paas-service-plan" {
  name                = "test-01-paas-service-plan"
  resource_group_name = azurerm_resource_group.test-01-paas-rg.name
  location            = azurerm_resource_group.test-01-paas-rg.location
  os_type             = "Linux"
  sku_name            = "B1"
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
}
