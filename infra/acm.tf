# ── ACM Certificate ───────────────────────────────────────────
resource "aws_acm_certificate" "main" {
  domain_name       = "invoice.hphil.dev"
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

# ── Validation records (you add these to Cloudflare manually) ─
output "acm_validation_records" {
  description = "Add these DNS records to Cloudflare to validate the certificate"
  value = {
    for dvo in aws_acm_certificate.main.domain_validation_options : dvo.domain_name => {
      name  = dvo.resource_record_name
      type  = dvo.resource_record_type
      value = dvo.resource_record_value
    }
  }
}

resource "aws_acm_certificate_validation" "main" {
  certificate_arn = aws_acm_certificate.main.arn

  timeouts {
    create = "10m"
  }
}