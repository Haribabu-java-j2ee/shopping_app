# Deployment Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [AWS Infrastructure Provisioning](#aws-infrastructure-provisioning)
4. [Kubernetes Deployment](#kubernetes-deployment)
5. [CI/CD Pipeline Setup](#cicd-pipeline-setup)
6. [Monitoring and Observability](#monitoring-and-observability)
7. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Tools
- **Java 17+** - Application runtime
- **Maven 3.8+** - Build tool
- **Docker 20.10+** - Containerization
- **kubectl 1.28+** - Kubernetes CLI
- **Terraform 1.6+** - Infrastructure as Code
- **AWS CLI 2.x** - AWS command line tool
- **Git** - Version control

### AWS Account Setup
1. Create AWS account
2. Configure IAM user with appropriate permissions:
   - AmazonEKSClusterPolicy
   - AmazonEKSWorkerNodePolicy
   - AmazonRDSFullAccess
   - AmazonElastiCacheFullAccess
   - AmazonMSKFullAccess
   - AmazonEC2ContainerRegistryFullAccess

3. Configure AWS CLI:
```bash
aws configure
# Enter your AWS Access Key ID
# Enter your AWS Secret Access Key
# Default region: us-east-1
# Default output format: json
```

## Local Development Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd shopping-app
```

### 2. Build Application
```bash
# Build all services
mvn clean install

# Or build individual services
cd auth-service && mvn clean package
cd order-service && mvn clean package
cd report-service && mvn clean package
```

### 3. Start Infrastructure with Docker Compose
```bash
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### 4. Access Services
- Auth Service: http://localhost:8081/api/v1/auth/health
- Order Service: http://localhost:8082/api/v1/orders/health
- Report Service: http://localhost:8083/api/v1/reports/health

### 5. Test with Sample Requests
```bash
# Register a user
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123!",
    "firstName": "Test",
    "lastName": "User"
  }'

# Login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "Password123!"
  }'
```

## AWS Infrastructure Provisioning

### 1. Configure Terraform Variables
```bash
cd infrastructure/terraform

# Create terraform.tfvars
cat > terraform.tfvars <<EOF
aws_region = "us-east-1"
project_name = "ecommerce"
environment = "prod"

# Database credentials (use strong passwords in production)
db_master_username = "postgres"
db_master_password = "CHANGE_THIS_PASSWORD"

# EKS Configuration
eks_node_desired_size = 3
eks_node_min_size = 2
eks_node_max_size = 6
EOF
```

### 2. Initialize Terraform
```bash
terraform init
```

### 3. Plan Infrastructure
```bash
terraform plan -out=tfplan
```

### 4. Apply Infrastructure
```bash
terraform apply tfplan

# Save outputs
terraform output > outputs.txt
```

### 5. Configure kubectl
```bash
aws eks update-kubeconfig \
  --region us-east-1 \
  --name ecommerce-prod-cluster
  
# Verify connection
kubectl get nodes
```

## Kubernetes Deployment

### 1. Create Namespace and Secrets
```bash
cd infrastructure/kubernetes

# Create namespace
kubectl apply -f namespace.yaml

# Create secrets (update with actual values)
kubectl apply -f secrets.yaml

# Create configmaps
kubectl apply -f configmap.yaml
```

### 2. Build and Push Docker Images to ECR
```bash
# Login to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

# Build and tag images
docker build -f auth-service/Dockerfile -t auth-service:latest .
docker tag auth-service:latest \
  <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/ecommerce-auth-service:latest
docker push <AWS_ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/ecommerce-auth-service:latest

# Repeat for other services
```

### 3. Deploy Applications
```bash
# Deploy services
kubectl apply -f auth-service-deployment.yaml
kubectl apply -f order-service-deployment.yaml
kubectl apply -f report-service-deployment.yaml

# Deploy ingress
kubectl apply -f ingress.yaml

# Deploy HPA (Horizontal Pod Autoscaler)
kubectl apply -f hpa.yaml
```

### 4. Verify Deployment
```bash
# Check pods
kubectl get pods -n ecommerce

# Check services
kubectl get svc -n ecommerce

# Check ingress
kubectl get ingress -n ecommerce

# Check HPA
kubectl get hpa -n ecommerce
```

### 5. Access Application
```bash
# Get load balancer URL
kubectl get ingress ecommerce-ingress -n ecommerce

# Test health endpoints
curl http://<ALB_URL>/api/v1/auth/health
curl http://<ALB_URL>/api/v1/orders/health
curl http://<ALB_URL>/api/v1/reports/health
```

## CI/CD Pipeline Setup

### Jenkins Setup

1. **Install Jenkins**
```bash
# On Ubuntu/Debian
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt update
sudo apt install jenkins

# Start Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

2. **Install Required Plugins**
   - Kubernetes CLI
   - AWS Steps
   - Docker Pipeline
   - Pipeline
   - Git
   - Maven Integration

3. **Configure Credentials**
   - AWS credentials
   - GitHub credentials
   - Docker registry credentials

4. **Create Pipeline Job**
   - New Item → Pipeline
   - Configure SCM: Git repository URL
   - Script Path: Jenkinsfile
   - Save

5. **Run Pipeline**
   - Click "Build with Parameters"
   - Select environment
   - Start build

## Monitoring and Observability

### Prometheus & Grafana Setup

1. **Install Prometheus**
```bash
kubectl apply -f https://raw.githubusercontent.com/prometheus-operator/prometheus-operator/main/bundle.yaml
```

2. **Install Grafana**
```bash
helm repo add grafana https://grafana.github.io/helm-charts
helm install grafana grafana/grafana -n monitoring
```

3. **Access Grafana**
```bash
# Get admin password
kubectl get secret --namespace monitoring grafana -o jsonpath="{.data.admin-password}" | base64 --decode

# Port forward
kubectl port-forward -n monitoring svc/grafana 3000:80
```

### Application Metrics
- Prometheus endpoint: `/actuator/prometheus`
- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`

## Troubleshooting

### Common Issues

#### Pods Not Starting
```bash
# Check pod status
kubectl get pods -n ecommerce

# Describe pod
kubectl describe pod <pod-name> -n ecommerce

# Check logs
kubectl logs <pod-name> -n ecommerce

# Check events
kubectl get events -n ecommerce --sort-by='.lastTimestamp'
```

#### Database Connection Issues
```bash
# Test connection from pod
kubectl exec -it <pod-name> -n ecommerce -- sh
nc -zv <db-endpoint> 5432
```

#### Service Not Accessible
```bash
# Check service endpoints
kubectl get endpoints -n ecommerce

# Check ingress
kubectl describe ingress ecommerce-ingress -n ecommerce

# Check ALB logs in AWS Console
```

#### High Memory/CPU Usage
```bash
# Check resource usage
kubectl top pods -n ecommerce
kubectl top nodes

# Check HPA status
kubectl get hpa -n ecommerce
```

### Useful Commands

```bash
# Restart deployment
kubectl rollout restart deployment/<deployment-name> -n ecommerce

# Scale deployment
kubectl scale deployment/<deployment-name> --replicas=5 -n ecommerce

# View deployment history
kubectl rollout history deployment/<deployment-name> -n ecommerce

# Rollback deployment
kubectl rollout undo deployment/<deployment-name> -n ecommerce

# Execute command in pod
kubectl exec -it <pod-name> -n ecommerce -- /bin/sh
```

## Scaling

### Horizontal Scaling
```bash
# Manual scaling
kubectl scale deployment/auth-service --replicas=5 -n ecommerce

# Auto-scaling (already configured via HPA)
# Will automatically scale between 3-10 replicas based on CPU/memory
```

### Vertical Scaling
Update resource requests/limits in deployment YAML and apply:
```yaml
resources:
  requests:
    memory: "1Gi"
    cpu: "1000m"
  limits:
    memory: "2Gi"
    cpu: "2000m"
```

## Backup and Disaster Recovery

### Database Backups
```bash
# RDS automated backups are enabled by default
# Manual snapshot
aws rds create-db-snapshot \
  --db-instance-identifier ecommerce-prod-auth \
  --db-snapshot-identifier ecommerce-prod-auth-snapshot-$(date +%Y%m%d)
```

### Application State
- Redis data is ephemeral (session storage)
- Kafka has replication factor 3
- All critical data is in RDS with automated backups

## Security Best Practices

1. **Secrets Management**
   - Use AWS Secrets Manager or External Secrets Operator
   - Rotate credentials regularly
   - Never commit secrets to Git

2. **Network Security**
   - Use private subnets for databases
   - Configure security groups properly
   - Enable VPC Flow Logs

3. **Application Security**
   - Keep dependencies updated
   - Run security scans (OWASP)
   - Use HTTPS/TLS
   - Implement rate limiting

4. **Access Control**
   - Use IAM roles for service accounts
   - Implement RBAC in Kubernetes
   - Enable audit logging

## Cost Optimization

1. **Right-sizing**
   - Monitor resource usage
   - Adjust instance types based on actual needs
   - Use HPA for automatic scaling

2. **Reserved Instances**
   - Consider RDS Reserved Instances (40-60% savings)
   - Consider EC2 Reserved Instances for predictable workloads

3. **Spot Instances**
   - Use Spot Instances for non-critical workloads
   - Configure EKS node groups with Spot

4. **Monitoring**
   - Enable AWS Cost Explorer
   - Set up billing alerts
   - Review AWS Cost and Usage Reports





