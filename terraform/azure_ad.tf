data "azuread_domains" "default" {
  only_initial = true
}

data "azuread_client_config" "current" {}

resource "random_uuid" "admin_role" {
}

resource "random_uuid" "user_role" {
}

resource "random_uuid" "partner_role" {
}

resource "random_uuid" "read_permission" {

}

resource "random_uuid" "write_permission" {

}

resource "azuread_application" "mari_sehat" {
  display_name = "MariSehat"
  owners       = [data.azuread_client_config.current.object_id]

  api {
    mapped_claims_enabled          = true
    requested_access_token_version = 2

    oauth2_permission_scope {
      admin_consent_description  = "Allow the application to read data of the API"
      admin_consent_display_name = "Allow Read"
      enabled                    = true
      id                         = random_uuid.read_permission.id
      type                       = "User"
      user_consent_display_name  = "Read"
      value                      = "read"
    }

    oauth2_permission_scope {
      admin_consent_description  = "Allow the application to modify data of the API"
      admin_consent_display_name = "Allow Write"
      enabled                    = true
      id                         = random_uuid.write_permission.id
      type                       = "Admin"
      value                      = "write"
    }

  }

  app_role {
    allowed_member_types = ["User", "Application"]
    description          = "Admins to manage the API"
    display_name         = "Admin"
    enabled              = true
    id                   = random_uuid.admin_role.id
    value                = "Admin"
  }

  app_role {
    allowed_member_types = ["User"]
    description          = "Users with general endpoint access"
    display_name         = "User"
    enabled              = true
    id                   = random_uuid.user_role.id
    value                = "User"
  }

  app_role {
    allowed_member_types = ["User"]
    description          = "Partner for third party integration"
    display_name         = "Partner"
    enabled              = true
    id                   = random_uuid.partner_role.id
    value                = "partner"
  }

  web {
    redirect_uris = [
      "http://localhost:8080/login/oauth2/code/",
      "http://localhost:8080/swagger-ui/oauth2-redirect.html",
      "https://${var.domain_name}/login/oauth2/code/",
      "https://${var.domain_name}/swagger-ui/oauth2-redirect.html"
    ]

    implicit_grant {
      access_token_issuance_enabled = true
      id_token_issuance_enabled     = true
    }
  }

  required_resource_access {
    resource_app_id = "00000003-0000-0000-c000-000000000000" # Microsoft Graph

    resource_access {
      id   = "df021288-bdef-4463-88db-98f22de89214" # User.Read.All
      type = "Role"
    }

    resource_access {
      id   = "b4e74841-8e56-480b-be8b-910348b18b4c" # User.ReadWrite
      type = "Scope"
    }

    resource_access {
      id   = "741f803b-c850-494e-b5df-cde7c675a1ca" # User.ReadWrite.All
      type = "Role"
    }

    resource_access {
      id   = "5b567255-7703-4780-807c-7be8301ae99b" # Group.Read.All
      type = "Role"
    }

    resource_access {
      id   = "62a82d76-70ea-41e2-9197-370581804d09" # Group.ReadWrite.All
      type = "Role"
    }

    resource_access {
      id   = "98830695-27a2-44f7-8c18-0c3ebc9698f6" # GroupMember.Read.All
      type = "Role"
    }

    resource_access {
      id   = "dbaae8cf-10b5-4b86-a4a1-f871c94c6695" # GroupMember.ReadWrite.All
      type = "Role"
    }
  }
}

resource "azuread_service_principal" "principal" {
  application_id               = azuread_application.mari_sehat.application_id
  app_role_assignment_required = false
}

resource "azuread_user" "admin" {
  user_principal_name = "admin@${var.domain_name}"
  display_name        = "Admin"
  password            = var.azure_ad_admin_password
}

resource "azuread_app_role_assignment" "assign_admin_role" {
  app_role_id         = random_uuid.admin_role.id
  principal_object_id = azuread_user.admin.id
  resource_object_id  = azuread_service_principal.principal.object_id
}

resource "azuread_group" "partner" {
  display_name     = "partner"
  owners           = [data.azuread_client_config.current.object_id, azuread_service_principal.principal.object_id]
  security_enabled = true
}

resource "azuread_app_role_assignment" "assign_partner_role" {
  app_role_id         = random_uuid.partner_role.id
  principal_object_id = azuread_group.partner.id
  resource_object_id  = azuread_service_principal.principal.object_id
}

resource "azuread_group" "user" {
  display_name     = "user"
  owners           = [data.azuread_client_config.current.object_id, azuread_service_principal.principal.object_id]
  security_enabled = true
}

resource "azuread_app_role_assignment" "assign_user_role" {
  app_role_id         = random_uuid.user_role.id
  principal_object_id = azuread_group.user.id
  resource_object_id  = azuread_service_principal.principal.object_id
}
