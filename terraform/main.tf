resource "azurerm_resource_group" "marisehat" {
  name     = "marisehat-resources"
  location = "Southeast Asia"
}

resource "azurerm_dns_zone" "marisehat-public" {
  name                = var.domain_name
  resource_group_name = azurerm_resource_group.marisehat.name
}
