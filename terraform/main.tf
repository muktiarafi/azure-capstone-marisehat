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
  https_only          = true

  site_config {
    always_on        = true
    linux_fx_version = "DOCKER|muktiarafi/marisehat-api:latest"

    health_check_path = "/actuator/health"
  }

  identity {
    type = "SystemAssigned"
  }
}

resource "azurerm_dns_a_record" "app_service_custom_domain" {
  name                = "@"
  resource_group_name = azurerm_resource_group.marisehat.name
  zone_name           = azurerm_dns_zone.marisehat-public.name
  ttl                 = 3600
  records             = [var.app_service_ip]
}

resource "azurerm_dns_txt_record" "app_service_custom_domain" {
  name                = "asuid"
  resource_group_name = azurerm_resource_group.marisehat.name
  zone_name           = azurerm_dns_zone.marisehat-public.name
  ttl                 = 3600

  record {
    value = azurerm_app_service.marisehat.custom_domain_verification_id
  }
}

resource "azurerm_app_service_custom_hostname_binding" "name" {
  hostname            = var.domain_name
  app_service_name    = azurerm_app_service.marisehat.name
  resource_group_name = azurerm_resource_group.marisehat.name
}

resource "azurerm_app_service_managed_certificate" "custom_domain" {
  custom_hostname_binding_id = azurerm_app_service_custom_hostname_binding.name.id
}

resource "azurerm_app_service_certificate_binding" "custom_domain_ssl" {
  hostname_binding_id = azurerm_app_service_custom_hostname_binding.name.id
  certificate_id      = azurerm_app_service_managed_certificate.custom_domain.id
  ssl_state           = "SniEnabled"
}

resource "azurerm_virtual_network" "southeast_asia" {
  name                = "southeast-asia-network"
  address_space       = ["10.0.0.0/16"]
  location            = azurerm_resource_group.marisehat.location
  resource_group_name = azurerm_resource_group.marisehat.name
}

resource "azurerm_subnet" "internal" {
  name                 = "one"
  resource_group_name  = azurerm_resource_group.marisehat.name
  virtual_network_name = azurerm_virtual_network.southeast_asia.name
  address_prefixes     = ["10.0.1.0/24"]
}

resource "azurerm_public_ip" "grafana_monitoring" {
  name                = "grafana-monitoring-ip"
  resource_group_name = azurerm_resource_group.marisehat.name
  location            = azurerm_resource_group.marisehat.location
  allocation_method   = "Dynamic"
}

resource "azurerm_network_interface" "main" {
  name                = "main-nic"
  location            = azurerm_resource_group.marisehat.location
  resource_group_name = azurerm_resource_group.marisehat.name

  ip_configuration {
    name                          = "configuration1"
    subnet_id                     = azurerm_subnet.internal.id
    private_ip_address_allocation = "Dynamic"
    public_ip_address_id          = azurerm_public_ip.grafana_monitoring.id
  }
}

resource "azurerm_network_security_group" "monitoring" {
  name                = "monitoring-security-group"
  location            = azurerm_resource_group.marisehat.location
  resource_group_name = azurerm_resource_group.marisehat.name

  security_rule {
    name                       = "SSH"
    protocol                   = "TCP"
    priority                   = 1001
    direction                  = "Inbound"
    access                     = "Allow"
    source_port_range          = "*"
    destination_port_range     = "22"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
}

resource "azurerm_network_interface_security_group_association" "monitoring_nsg" {
  network_interface_id      = azurerm_network_interface.main.id
  network_security_group_id = azurerm_network_security_group.monitoring.id
}

resource "azurerm_linux_virtual_machine" "grafana_monitoring" {
  name                  = "grafana-monitoring"
  location              = azurerm_resource_group.marisehat.location
  resource_group_name   = azurerm_resource_group.marisehat.name
  network_interface_ids = [azurerm_network_interface.main.id]
  size                  = "Standard_B1ms"

  admin_username = var.vm_username

  admin_ssh_key {
    username   = var.vm_username
    public_key = var.vm_public_key
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "UbuntuServer"
    sku       = "18.04-LTS"
    version   = "latest"
  }

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }
}

resource "azurerm_subnet" "integration" {
  name                 = "two"
  resource_group_name  = azurerm_resource_group.marisehat.name
  virtual_network_name = azurerm_virtual_network.southeast_asia.name
  address_prefixes     = ["10.0.2.0/24"]

  delegation {
    name = "delegation"

    service_delegation {
      name    = "Microsoft.Web/serverFarms"
      actions = ["Microsoft.Network/virtualNetworks/subnets/action"]
    }
  }
}

resource "azurerm_app_service_virtual_network_swift_connection" "integration" {
  app_service_id = azurerm_app_service.marisehat.id
  subnet_id      = azurerm_subnet.integration.id
}
