# ── SSM Parameters (non-sensitive config) ────────────────────
locals {
  ssm_prefix = "/invoice/prod"
}

resource "aws_ssm_parameter" "environment" {
  name  = "${local.ssm_prefix}/environment"
  type  = "String"
  value = "prod"
}

resource "aws_ssm_parameter" "database_jdbc_url" {
  name  = "${local.ssm_prefix}/database/jdbc_url"
  type  = "String"
  value = "placeholder"

  lifecycle { ignore_changes = [value] }
}

resource "aws_ssm_parameter" "database_driver_class_name" {
  name  = "${local.ssm_prefix}/database/driver_class_name"
  type  = "String"
  value = "org.postgresql.Driver"
}

resource "aws_ssm_parameter" "database_pool_max_size" {
  name  = "${local.ssm_prefix}/database/maximum_pool_size"
  type  = "String"
  value = "10"
}

resource "aws_ssm_parameter" "database_pool_min_idle" {
  name  = "${local.ssm_prefix}/database/minimum_idle"
  type  = "String"
  value = "2"
}

resource "aws_ssm_parameter" "database_auto_commit" {
  name  = "${local.ssm_prefix}/database/auto_commit"
  type  = "String"
  value = "false"
}

resource "aws_ssm_parameter" "redis_ssl" {
  name  = "${local.ssm_prefix}/redis/ssl"
  type  = "String"
  value = "true"
}

resource "aws_ssm_parameter" "services_ports" {
  for_each = toset(var.services)
  name     = "${local.ssm_prefix}/services/${each.value}/port"
  type     = "String"
  value    = "8080"
}

resource "aws_ssm_parameter" "services_domains" {
  for_each = toset(var.services)
  name     = "${local.ssm_prefix}/services/${each.value}/domain"
  type     = "String"
  value    = "${each.value}.invoice.local"
}

resource "aws_ssm_parameter" "redis_host" {
  name  = "${local.ssm_prefix}/redis/host"
  type  = "String"
  value = "placeholder"

  lifecycle { ignore_changes = [value] }
}

resource "aws_ssm_parameter" "redis_port" {
  name  = "${local.ssm_prefix}/redis/port"
  type  = "String"
  value = "6379"
}

# ── Secrets Manager (sensitive values) ───────────────────────
resource "aws_secretsmanager_secret" "database_username" {
  name                    = "${local.ssm_prefix}/database/username"
  recovery_window_in_days = 0  # allows immediate deletion on terraform destroy
}

resource "aws_secretsmanager_secret" "database_password" {
  name                    = "${local.ssm_prefix}/database/password"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret" "redis_password" {
  name                    = "${local.ssm_prefix}/redis/password"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret" "auth_service_client_id" {
  name                    = "${local.ssm_prefix}/auth/service_client_id"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret" "auth_service_client_secret" {
  name                    = "${local.ssm_prefix}/auth/service_client_secret"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret" "auth_user_client_id" {
  name                    = "${local.ssm_prefix}/auth/user_client_id"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret" "auth_user_client_secret" {
  name                    = "${local.ssm_prefix}/auth/user_client_secret"
  recovery_window_in_days = 0
}


# ── Grafana Open Telemetry ------------───────────────────────
resource "aws_ssm_parameter" "otel_endpoint" {
  name  = "${local.ssm_prefix}/otel/endpoint"
  type  = "String"
  value = "https://otlp-gateway-prod-us-east-3.grafana.net/otlp"
}

resource "aws_ssm_parameter" "otel_protocol" {
  name  = "${local.ssm_prefix}/otel/protocol"
  type  = "String"
  value = "http/protobuf"
}

resource "aws_secretsmanager_secret" "otel_token" {
  name                    = "${local.ssm_prefix}/otel/token"
  recovery_window_in_days = 0
}
