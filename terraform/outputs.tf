output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "alb_dns_name" {
  description = "The DNS name of the ALB"
  value       = module.alb.lb_dns_name
}

output "ecr_backend_url" {
  description = "The URL of the backend ECR repository"
  value       = aws_ecr_repository.backend.repository_url
}

output "ecr_frontend_url" {
  description = "The URL of the frontend ECR repository"
  value       = aws_ecr_repository.frontend.repository_url
}

output "rds_endpoint" {
  description = "The endpoint of the RDS instance"
  value       = module.db.db_instance_endpoint
}
