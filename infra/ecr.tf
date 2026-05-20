resource "aws_ecr_repository" "services" {
  for_each = toset(var.services)

  name                 = each.value
  image_tag_mutability = "MUTABLE"
  force_delete         = true   # this will destroy even when not empty

  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_lifecycle_policy" "services" {
  for_each   = aws_ecr_repository.services
  repository = each.value.name
  policy     = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Manter apenas as 10 imagens mais recentes"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 10
      }
      action = { type = "expire" }
    }]
  })
}

output "ecr_urls" {
  value = { for k, v in aws_ecr_repository.services : k => v.repository_url }
}
