# E-Commerce Microservices Platform

A production-grade, scalable e-commerce platform built with Spring Boot microservices architecture, designed for cloud deployment on AWS EKS.

## 🏗️ Architecture Overview

This project demonstrates enterprise-level microservices architecture with:

- **3 Core Microservices**: Auth, Order Management, and Reporting
- **Event-Driven Architecture**: Using Apache Kafka
- **Distributed Caching**: Redis (AWS ElastiCache)
- **Database**: PostgreSQL (AWS RDS)
- **Container Orchestration**: Kubernetes on AWS EKS
- **Infrastructure as Code**: Terraform
- **CI/CD**: Jenkins with automated testing and approval gates

## 📋 Services

### 1. Auth Service (Port: 8081)
- User registration and authentication
- JWT token generation and validation
- Password encryption with BCrypt
- Redis-based session management
- **Design Patterns**: Factory, Strategy, Builder

### 2. Order Service (Port: 8082)
- Order creation and management
- Inventory validation
- Event publishing to Kafka
- **Design Patterns**: Repository, Saga, Event Sourcing

### 3. Report Service (Port: 8083)
- Real-time inventory reports
- Profit & Loss calculations
- Sales analytics
- **Design Patterns**: CQRS, Observer, Chain of Responsibility

## 🛠️ Technology Stack

### Backend
- **Java**: 17
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Maven
- **Database**: PostgreSQL 16
- **Cache**: Redis 7.x
- **Message Broker**: Apache Kafka 3.6
- **Security**: Spring Security + JWT

### DevOps & Infrastructure
- **Containerization**: Docker
- **Orchestration**: Kubernetes (EKS)
- **IaC**: Terraform
- **CI/CD**: Jenkins
- **Cloud Provider**: AWS

### Testing
- **Unit Tests**: JUnit 5 + Mockito
- **Integration Tests**: TestContainers
- **Performance Tests**: Apache JMeter
- **API Tests**: REST Assured

## 📁 Project Structure

```
shopping-app/
├── auth-service/           # Authentication & Authorization Service
├── order-service/          # Order Management Service
├── report-service/         # Analytics & Reporting Service
├── common-lib/             # Shared utilities and DTOs
├── infrastructure/
│   ├── terraform/          # AWS infrastructure provisioning
│   ├── kubernetes/         # K8s manifests for EKS
│   └── docker/             # Dockerfiles and compose
├── scripts/
│   ├── sql/                # Database schemas and seed data
│   └── performance/        # JMeter test plans
├── jenkins/                # CI/CD pipeline definitions
└── docs/                   # Comprehensive documentation
```

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- AWS CLI configured
- kubectl
- Terraform 1.6+

### Local Development Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd shopping-app
```

2. **Start infrastructure services**
```bash
docker-compose -f infrastructure/docker/docker-compose.yml up -d
```

3. **Build all services**
```bash
mvn clean install
```

4. **Run services**
```bash
# Terminal 1 - Auth Service
cd auth-service && mvn spring-boot:run

# Terminal 2 - Order Service
cd order-service && mvn spring-boot:run

# Terminal 3 - Report Service
cd report-service && mvn spring-boot:run
```

### Testing

**Integration Tests**
```bash
mvn verify -P integration-tests
```

**Performance Tests**
```bash
cd scripts/performance
jmeter -n -t performance-test-plan.jmx -l results.jtl -e -o ./report
```

## 🐳 Docker Deployment

### Build Images
```bash
./scripts/build-docker-images.sh
```

### Run with Docker Compose
```bash
docker-compose up -d
```

## ☸️ Kubernetes Deployment (EKS)

### 1. Provision AWS Infrastructure
```bash
cd infrastructure/terraform
terraform init
terraform plan
terraform apply
```

### 2. Configure kubectl
```bash
aws eks update-kubeconfig --region us-east-1 --name ecommerce-cluster
```

### 3. Deploy Applications
```bash
kubectl apply -f infrastructure/kubernetes/
```

### 4. Verify Deployment
```bash
kubectl get pods -n ecommerce
kubectl get services -n ecommerce
```

## 🔐 Security Features

- JWT-based authentication with refresh tokens
- Password encryption (BCrypt)
- API rate limiting
- CORS configuration
- SQL injection prevention (JPA/Hibernate)
- XSS protection headers
- HTTPS/TLS encryption

## 📊 Monitoring & Observability

- **Metrics**: Micrometer + Prometheus
- **Logging**: ELK Stack integration ready
- **Tracing**: Spring Cloud Sleuth
- **Health Checks**: Spring Actuator
- **Dashboards**: Grafana configurations

## 🔄 CI/CD Pipeline

Jenkins pipeline with stages:
1. **Build**: Maven compile and package
2. **Unit Tests**: JUnit execution
3. **Integration Tests**: TestContainers-based tests
4. **Performance Tests**: JMeter execution
5. **Code Quality**: SonarQube analysis
6. **Security Scan**: OWASP dependency check
7. **Build Docker Images**
8. **Push to ECR**
9. **Deploy to EKS** (with manual approval)

## 💰 AWS Cost Estimate (Monthly - INR)

| Service | Configuration | Cost (INR) |
|---------|--------------|------------|
| EKS Cluster | 1 cluster | ₹6,200 |
| EC2 (Worker Nodes) | 3x t3.medium | ₹9,300 |
| RDS PostgreSQL | db.t3.medium | ₹7,500 |
| ElastiCache Redis | cache.t3.micro | ₹3,100 |
| MSK (Kafka) | kafka.t3.small (3 brokers) | ₹18,600 |
| ALB | Application Load Balancer | ₹2,500 |
| EBS Volumes | 300GB | ₹2,000 |
| Data Transfer | ~100GB | ₹800 |
| CloudWatch | Logs & Metrics | ₹1,500 |
| ECR | Container Registry | ₹500 |
| **Total** | | **₹51,500** |

*Costs are approximate and based on us-east-1 region. Actual costs may vary based on usage.*

## 🎯 Design Patterns Implemented

1. **Repository Pattern**: Data access abstraction
2. **Factory Pattern**: Object creation for different user types
3. **Strategy Pattern**: Multiple payment methods
4. **Builder Pattern**: Complex object construction
5. **Singleton Pattern**: Configuration managers
6. **Observer Pattern**: Event-driven notifications
7. **Circuit Breaker**: Resilience4j integration
8. **Saga Pattern**: Distributed transactions
9. **CQRS**: Command-Query separation in reports
10. **Event Sourcing**: Order state tracking

## 📖 Documentation

Detailed documentation available in `/docs`:
- [Architecture Guide](docs/ARCHITECTURE.md)
- [API Documentation](docs/API.md)
- [Deployment Guide](docs/DEPLOYMENT.md)
- [Development Guide](docs/DEVELOPMENT.md)
- [Performance Tuning](docs/PERFORMANCE.md)

## 🧪 Sample API Requests

### Register User
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Create Order
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "123",
    "items": [
      {"productId": "PROD-001", "quantity": 2}
    ]
  }'
```

### Get Reports
```bash
curl -X GET http://localhost:8083/api/v1/reports/inventory \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## 🤝 Contributing

This is a learning project for understanding end-to-end microservices development and DevOps practices.

## 📄 License

MIT License - See LICENSE file for details

## 📧 Support

For questions and support, please open an issue in the repository.


