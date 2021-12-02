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

resource "azurerm_postgresql_server" "pg" {
  name                = "marisehat-pg-db"
  location            = "Australia East" // reach quota limit for south east asia
  resource_group_name = azurerm_resource_group.marisehat.name

  administrator_login          = var.pg_admin_login
  administrator_login_password = var.pg_admin_password

  sku_name   = "B_Gen5_1"
  version    = "11"
  storage_mb = 5120

  backup_retention_days        = 7
  geo_redundant_backup_enabled = false
  auto_grow_enabled            = false

  ssl_enforcement_enabled = false
}

resource "azurerm_postgresql_database" "marisehat" {
  name                = var.pg_db_name
  resource_group_name = azurerm_resource_group.marisehat.name
  server_name         = azurerm_postgresql_server.pg.name
  charset             = "UTF8"
  collation           = "English_United States.1252"
}

resource "azurerm_app_service_plan" "marisehat" {
  name                = "marisehat-appserviceplan"
  location            = azurerm_resource_group.marisehat.location
  resource_group_name = azurerm_resource_group.marisehat.name
  kind                = "Linux"
  reserved            = true

  sku {
    tier = "Standard"
    size = "S1"
  }
}

resource "azurerm_app_service" "marisehat" {
  name                = "marisehat"
  location            = azurerm_resource_group.marisehat.location
  resource_group_name = azurerm_resource_group.marisehat.name
  app_service_plan_id = azurerm_app_service_plan.marisehat.id

  site_config {
    always_on        = true
    linux_fx_version = "DOCKER|muktiarafi/marisehat-api:latest"

    health_check_path = "/actuator/health"
  }

  identity {
    type = "SystemAssigned"
  }
}
