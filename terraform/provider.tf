provider "azuread" {
  tenant_id = var.azure_ad_tenant_id
}

provider "azurerm" {
  features {

  }
}
