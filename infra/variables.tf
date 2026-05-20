variable "environment" {
  description = "Deploy environment (ex: 'local', 'dev', 'prod')."
  type        = string
  default     = "local"
}

variable "github_repo" {
  description = "GitHub repo as org/repo"
}

variable "aws_region" {
  description = "The AWS region to deploy resources into."
  type        = string
}

variable "services" {
  description = "microservices name list"
  type        = list(string)
}
