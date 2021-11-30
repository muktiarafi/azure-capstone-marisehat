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

resource "azurerm_storage_account" "marisehat" {
  name                      = "marisehatstorageapi"
  resource_group_name       = azurerm_resource_group.marisehat.name
  location                  = azurerm_resource_group.marisehat.location
  account_tier              = "Standard"
  account_replication_type  = "LRS"
  allow_blob_public_access  = true
  enable_https_traffic_only = false
}

resource "azurerm_storage_container" "lab_result" {
  name                  = "lab-result"
  storage_account_name  = azurerm_storage_account.marisehat.name
  container_access_type = "blob"
}

resource "azurerm_storage_management_policy" "lab_result" {
  storage_account_id = azurerm_storage_account.marisehat.id

  rule {
    name    = "tier"
    enabled = true

    filters {
      blob_types = ["blockBlob"]
    }

    actions {
      base_blob {
        tier_to_cool_after_days_since_modification_greater_than    = 10
        tier_to_archive_after_days_since_modification_greater_than = 50
      }
    }
  }
}
