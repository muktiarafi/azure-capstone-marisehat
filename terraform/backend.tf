terraform {
  backend "remote" {
    organization = "muktiarafi"

    workspaces {
      name = "marisehat"
    }
  }
}
