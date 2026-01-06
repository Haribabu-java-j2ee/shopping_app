# ==============================================================================
# Terraform Outputs
# ==============================================================================

output "vpc_id" {
  description = "VPC ID"
  value       = module.networking.vpc_id
}

output "eks_cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = module.eks.cluster_endpoint
}

output "eks_cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "rds_auth_endpoint" {
  description = "Auth service RDS endpoint"
  value       = module.rds_auth.db_endpoint
  sensitive   = true
}

output "rds_order_endpoint" {
  description = "Order service RDS endpoint"
  value       = module.rds_order.db_endpoint
  sensitive   = true
}

output "rds_report_endpoint" {
  description = "Report service RDS endpoint"
  value       = module.rds_report.db_endpoint
  sensitive   = true
}

output "redis_endpoint" {
  description = "ElastiCache Redis endpoint"
  value       = module.elasticache.redis_endpoint
}

output "kafka_bootstrap_brokers" {
  description = "MSK Kafka bootstrap brokers"
  value       = module.msk.bootstrap_brokers
  sensitive   = true
}

output "ecr_auth_repository_url" {
  description = "ECR repository URL for auth service"
  value       = aws_ecr_repository.auth_service.repository_url
}

output "ecr_order_repository_url" {
  description = "ECR repository URL for order service"
  value       = aws_ecr_repository.order_service.repository_url
}

output "ecr_report_repository_url" {
  description = "ECR repository URL for report service"
  value       = aws_ecr_repository.report_service.repository_url
}








