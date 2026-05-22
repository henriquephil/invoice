locals {
  account_id = data.aws_caller_identity.current.account_id
  repo_name  = replace(var.github_repo, "/", "-")

  # Environment variables common to all services
  common_environment = [
    { name = "ENVIRONMENT", value = "prod" },
    { name = "SERVICES_AUTH_DOMAIN",    value = "http://auth.invoice.local" },
    { name = "SERVICES_AUTH_PORT",      value = "8080" },
    { name = "SERVICES_ACCOUNT_DOMAIN", value = "http://account.invoice.local" },
    { name = "SERVICES_ACCOUNT_PORT",   value = "8080" },
    { name = "SERVICES_CATALOG_DOMAIN", value = "http://catalog.invoice.local" },
    { name = "SERVICES_CATALOG_PORT",   value = "8080" },
    { name = "SERVICES_INVOICE_DOMAIN", value = "http://invoice.invoice.local" },
    { name = "SERVICES_INVOICE_PORT",   value = "8080" },
    { name = "SERVICES_GATEWAY_PORT",   value = "8080" },
    { name = "DATABASE_DRIVER_CLASS_NAME",       value = "org.postgresql.Driver" },
    { name = "DATABASE_MAXIMUM_POOL_SIZE",        value = "10" },
    { name = "DATABASE_MINIMUM_IDLE",             value = "2" },
    { name = "DATABASE_AUTO_COMMIT",              value = "false" },
    { name = "DATABASE_JDBC_URL", value = "jdbc:postgresql://ep-polished-sound-aqmv43fn-pooler.c-8.us-east-1.aws.neon.tech:5432/neondb?sslmode=require" },
  ]

  # Secrets common to all services that use the database
  db_secrets = [
    { name = "DATABASE_USERNAME", valueFrom = "arn:aws:secretsmanager:us-east-1:${local.account_id}:secret:/invoice/prod/database/username" },
    { name = "DATABASE_PASSWORD", valueFrom = "arn:aws:secretsmanager:us-east-1:${local.account_id}:secret:/invoice/prod/database/password" },
  ]

  auth_service_secrets = [
    { name = "AUTH_SERVICE_CLIENT_ID",     valueFrom = "arn:aws:secretsmanager:us-east-1:${local.account_id}:secret:/invoice/prod/auth/service_client_id" },
    { name = "AUTH_SERVICE_CLIENT_SECRET", valueFrom = "arn:aws:secretsmanager:us-east-1:${local.account_id}:secret:/invoice/prod/auth/service_client_secret" },
  ]

  auth_user_secrets = [
    { name = "AUTH_USER_CLIENT_ID",     valueFrom = "arn:aws:secretsmanager:us-east-1:${local.account_id}:secret:/invoice/prod/auth/user_client_id" },
    { name = "AUTH_USER_CLIENT_SECRET", valueFrom = "arn:aws:secretsmanager:us-east-1:${local.account_id}:secret:/invoice/prod/auth/user_client_secret" },
  ]

  redis_environment = [
    { name = "REDIS_HOST", value = "just-albacore-133016.upstash.io" },
    { name = "REDIS_PORT", value = "6379" },
    { name = "REDIS_SSL",  value = "true" },
  ]

  redis_secrets = [
    { name = "REDIS_PASSWORD", valueFrom = "arn:aws:secretsmanager:us-east-1:${local.account_id}:secret:/invoice/prod/redis/password" },
  ]

  # Per-service config
  service_config = {
    gateway = {
      environment = concat(local.common_environment, local.redis_environment)
      secrets     = concat(local.auth_service_secrets, local.auth_user_secrets, local.redis_secrets)
    }
    auth = {
      environment = local.common_environment
      secrets     = concat(local.db_secrets, local.auth_user_secrets)
    }
    account = {
      environment = local.common_environment
      secrets     = concat(local.db_secrets, local.auth_service_secrets)
    }
    catalog = {
      environment = local.common_environment
      secrets     = concat(local.db_secrets, local.auth_service_secrets)
    }
    invoice = {
      environment = local.common_environment
      secrets     = concat(local.db_secrets, local.auth_service_secrets)
    }
  }
}

# ── Task Definitions ──────────────────────────────────────────
resource "aws_ecs_task_definition" "services" {
  for_each = local.service_config

  family                   = "${local.repo_name}-${each.key}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = 256   # 0.25 vCPU
  memory                   = 512   # 512 MB
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([{
    name      = each.key
    image = "${data.aws_caller_identity.current.account_id}.dkr.ecr.us-east-1.amazonaws.com/${each.key}:latest"
    essential = true

    portMappings = [{
      containerPort = 8080
      protocol      = "tcp"
    }]

    environment = each.value.environment
    secrets     = each.value.secrets

    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = "/ecs/${local.repo_name}/${each.key}"
        "awslogs-region"        = "us-east-1"
        "awslogs-stream-prefix" = "ecs"
      }
    }

    healthCheck = {
      command     = ["CMD-SHELL", "wget -qO- http://localhost:8080/health || exit 1"]
      interval    = 30
      timeout     = 5
      retries     = 3
      startPeriod = 60
    }
  }])
}

# ── ECS Services ──────────────────────────────────────────────
resource "aws_ecs_service" "services" {
  for_each = local.service_config

  name            = each.key
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.services[each.key].arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = aws_subnet.public[*].id
    security_groups  = [
        each.key == "gateway" ? aws_security_group.gateway.id : aws_security_group.internal.id
    ]
    assign_public_ip = true  # required since we're using public subnets
  }

  service_registries {
    registry_arn = aws_service_discovery_service.services[each.key].arn
  }

  dynamic "load_balancer" {
    for_each = each.key == "gateway" ? [1] : []
    content {
      target_group_arn = aws_lb_target_group.gateway.arn
      container_name   = "gateway"
      container_port   = 8080
    }
  }

  depends_on = [
    aws_lb_listener.https,
    aws_iam_role_policy_attachment.ecs_task_execution
  ]
}
