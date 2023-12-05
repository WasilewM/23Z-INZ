variable "admin_username" {
  type = string
}

variable "public_key_path" {
  type = string
}

variable "resource_group_name" {
  type = string
}

variable "resource_group_location" {
  type = string
}

variable "server_app_vms" {
  type = map(object({
    private_ip = string
  }))
}

variable "master_db_private_ip" {
  type = string
}

variable "replica_db_private_ip" {
  type = string
}

variable "observability_private_ip" {
  type = string
}

variable "nginx_private_ip" {
  type = string
}