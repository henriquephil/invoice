# ── ECS Cluster ───────────────────────────────────────────────
resource "aws_ecs_cluster" "main" {
  name = replace(var.github_repo, "/", "-")

  tags = { Name = "${var.github_repo}-cluster" }
}

# ── IAM Role for ECS Tasks ────────────────────────────────────
# Allows ECS to pull images from ECR and write logs to CloudWatch
resource "aws_iam_role" "ecs_task_execution" {
  name = "ecs-task-execution-${replace(var.github_repo, "/", "-")}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Effect    = "Allow"
      Principal = { Service = "ecs-tasks.amazonaws.com" }
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# Allows ECS to read secrets from Secrets Manager and SSM
resource "aws_iam_role_policy" "ecs_task_execution_secrets" {
  name = "ecs-task-execution-secrets"
  role = aws_iam_role.ecs_task_execution.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "secretsmanager:GetSecretValue",
          "ssm:GetParameters",
          "ssm:GetParameter"
        ]
        Resource = "*"
      }
    ]
  })
}

# ── CloudWatch Log Groups (one per service) ───────────────────
resource "aws_cloudwatch_log_group" "services" {
  for_each          = toset(var.services)
  name              = "/ecs/${replace(var.github_repo, "/", "-")}/${each.value}"
  retention_in_days = 7

  tags = { Service = each.value }
}

# ── Service Discovery (Cloud Map) ─────────────────────────────
resource "aws_service_discovery_private_dns_namespace" "main" {
  name        = "invoice.local"
  description = "Private DNS namespace for invoice services"
  vpc         = aws_vpc.main.id
}

resource "aws_service_discovery_service" "services" {
  for_each = toset(var.services)
  name     = each.value

  dns_config {
    namespace_id = aws_service_discovery_private_dns_namespace.main.id

    dns_records {
      ttl  = 10
      type = "A"
    }

    routing_policy = "MULTIVALUE"
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}