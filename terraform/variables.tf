variable "aws_region" {
  description = "AWS Region"
  type        = string
  default     = "eu-north-1"
}

variable "project_name" {
  description = "Project Name"
  type        = string
  default     = "balancesheet"
}

variable "db_password" {
  description = "Database Password"
  type        = string
  sensitive   = true
}

variable "db_username" {
  description = "Database Username"
  type        = string
  default     = "postgres"
}
