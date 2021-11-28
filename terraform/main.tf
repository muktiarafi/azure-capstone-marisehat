resource "azurerm_resource_group" "marisehat" {
  name     = "marisehat-resources"
  location = "Southeast Asia"
}

resource "azurerm_dns_zone" "marisehat-public" {
  name                = var.domain_name
  resource_group_name = azurerm_resource_group.marisehat.name
}

resource "azurerm_dns_txt_record" "ad_custom_domain" {
  name                = "@"
  resource_group_name = azurerm_resource_group.marisehat.name
  zone_name           = azurerm_dns_zone.marisehat-public.name
  ttl                 = 3600

  record {
    value = var.ad_custom_domain_txt_record_value
  }
}
