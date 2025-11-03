# ==============================================================================
# Main Terraform Configuration
# ==============================================================================
# Provisions complete AWS infrastructure for e-commerce microservices platform
# ==============================================================================

terraform {
  required_version = ">= 1.6"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.24"
    }
  }
  
  # Backend configuration for state management
  # Uncomment and configure for production
  # backend "s3" {
  #   bucket         = "ecommerce-terraform-state"
  #   key            = "prod/terraform.tfstate"
  #   region         = "us-east-1"
  #   encrypt        = true
  #   dynamodb_table = "terraform-lock"
  # }
}

# ==============================================================================
# Provider Configuration
# ==============================================================================

provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Project     = "E-Commerce Platform"
      Environment = var.environment
      ManagedBy   = "Terraform"
      Owner       = "DevOps Team"
    }
  }
}

# ==============================================================================
# Data Sources
# ==============================================================================

# Get available availability zones
data "aws_availability_zones" "available" {
  state = "available"
}

# ==============================================================================
# Networking Module
# ==============================================================================

module "networking" {
  source = "./modules/networking"
  
  project_name        = var.project_name
  environment         = var.environment
  vpc_cidr            = var.vpc_cidr
  availability_zones  = data.aws_availability_zones.available.names
  public_subnet_cidrs = var.public_subnet_cidrs
  private_subnet_cidrs= var.private_subnet_cidrs
}

# ==============================================================================
# EKS Cluster Module
# ==============================================================================

module "eks" {
  source = "./modules/eks"
  
  project_name       = var.project_name
  environment        = var.environment
  vpc_id             = module.networking.vpc_id
  private_subnet_ids = module.networking.private_subnet_ids
  
  cluster_version    = var.eks_cluster_version
  node_instance_types= var.eks_node_instance_types
  node_desired_size  = var.eks_node_desired_size
  node_min_size      = var.eks_node_min_size
  node_max_size      = var.eks_node_max_size
}

# ==============================================================================
# RDS PostgreSQL Instances
# ==============================================================================

# Auth Service Database
module "rds_auth" {
  source = "./modules/rds"
  
  project_name       = var.project_name
  environment        = var.environment
  service_name       = "auth"
  
  vpc_id             = module.networking.vpc_id
  subnet_ids         = module.networking.private_subnet_ids
  allowed_cidr_blocks= [var.vpc_cidr]
  
  instance_class     = var.rds_instance_class
  allocated_storage  = var.rds_allocated_storage
  database_name      = "auth_db"
  master_username    = var.db_master_username
  master_password    = var.db_master_password
}

# Order Service Database
module "rds_order" {
  source = "./modules/rds"
  
  project_name       = var.project_name
  environment        = var.environment
  service_name       = "order"
  
  vpc_id             = module.networking.vpc_id
  subnet_ids         = module.networking.private_subnet_ids
  allowed_cidr_blocks= [var.vpc_cidr]
  
  instance_class     = var.rds_instance_class
  allocated_storage  = var.rds_allocated_storage
  database_name      = "order_db"
  master_username    = var.db_master_username
  master_password    = var.db_master_password
}

# Report Service Database
module "rds_report" {
  source = "./modules/rds"
  
  project_name       = var.project_name
  environment        = var.environment
  service_name       = "report"
  
  vpc_id             = module.networking.vpc_id
  subnet_ids         = module.networking.private_subnet_ids
  allowed_cidr_blocks= [var.vpc_cidr]
  
  instance_class     = var.rds_instance_class
  allocated_storage  = var.rds_allocated_storage
  database_name      = "report_db"
  master_username    = var.db_master_username
  master_password    = var.db_master_password
}

# ==============================================================================
# ElastiCache Redis Module
# ==============================================================================

module "elasticache" {
  source = "./modules/elasticache"
  
  project_name       = var.project_name
  environment        = var.environment
  
  vpc_id             = module.networking.vpc_id
  subnet_ids         = module.networking.private_subnet_ids
  allowed_cidr_blocks= [var.vpc_cidr]
  
  node_type          = var.redis_node_type
  num_cache_nodes    = var.redis_num_nodes
}

# ==============================================================================
# MSK (Managed Streaming for Kafka) Module
# ==============================================================================

module "msk" {
  source = "./modules/msk"
  
  project_name       = var.project_name
  environment        = var.environment
  
  vpc_id             = module.networking.vpc_id
  subnet_ids         = module.networking.private_subnet_ids
  allowed_cidr_blocks= [var.vpc_cidr]
  
  kafka_version      = var.kafka_version
  instance_type      = var.kafka_instance_type
  number_of_brokers  = var.kafka_number_of_brokers
}

# ==============================================================================
# ECR Repositories
# ==============================================================================

resource "aws_ecr_repository" "auth_service" {
  name                 = "${var.project_name}-auth-service"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
  
  encryption_configuration {
    encryption_type = "AES256"
  }
}

resource "aws_ecr_repository" "order_service" {
  name                 = "${var.project_name}-order-service"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "report_service" {
  name                 = "${var.project_name}-report-service"
  image_tag_mutability = "MUTABLE"
  
  image_scanning_configuration {
    scan_on_push = true
  }
}





