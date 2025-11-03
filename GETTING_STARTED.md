# Getting Started - E-Commerce Microservices Platform

## 🎉 Welcome!

This comprehensive e-commerce platform demonstrates production-grade microservices architecture suitable for a **Principal Software Engineer** role at companies like Amazon.

## 📚 What's Included

### 🏗️ Architecture & Services
- **3 Microservices**: Auth, Order Management, and Reporting
- **Event-Driven Architecture**: Apache Kafka for inter-service communication
- **Design Patterns**: Factory, Strategy, Builder, Saga, CQRS, Repository, and more
- **Distributed Caching**: Redis/ElastiCache for session management
- **Database per Service**: PostgreSQL with Flyway migrations

### 🔧 Infrastructure & DevOps
- **Docker**: Multi-stage Dockerfiles with detailed comments
- **Kubernetes**: Production-ready manifests for EKS with HPA
- **Terraform**: Complete AWS infrastructure (EKS, RDS, ElastiCache, MSK)
- **Jenkins Pipeline**: CI/CD with integration tests, performance tests, and approval gates
- **Docker Compose**: Local development environment

### 🧪 Testing & Quality
- **Integration Tests**: TestContainers-based tests
- **Performance Tests**: JMeter test plans with latency/memory/CPU metrics
- **Code Quality**: SonarQube integration
- **Security**: OWASP dependency scanning

### 📖 Documentation
- **Deployment Guide**: Step-by-step instructions
- **API Documentation**: Complete REST API reference
- **Cost Estimate**: Detailed AWS cost breakdown in INR
- **JavaDoc**: Comprehensive code documentation

## 🚀 Quick Start (3 Steps)

### Step 1: Local Development
```bash
# Clone and build
git clone <repo-url>
cd shopping-app
mvn clean install

# Start services
docker-compose up -d

# Test
curl http://localhost:8081/api/v1/auth/health
```

### Step 2: Deploy to AWS
```bash
# Provision infrastructure
cd infrastructure/terraform
terraform init
terraform apply

# Deploy to Kubernetes
cd ../kubernetes
kubectl apply -f .
```

### Step 3: Setup CI/CD
```bash
# Configure Jenkins
# - Install plugins
# - Add credentials
# - Create pipeline with Jenkinsfile
```

## 📂 Project Structure

```
shopping-app/
├── auth-service/              # Authentication & User Management
│   ├── src/main/java/...     # Source code
│   ├── src/main/resources/   # Configuration & migrations
│   └── Dockerfile            # Container definition
├── order-service/            # Order Management
├── report-service/           # Analytics & Reporting
├── common-lib/               # Shared utilities
├── infrastructure/
│   ├── terraform/            # AWS IaC
│   ├── kubernetes/           # K8s manifests
│   └── docker/               # Docker configs
├── docs/                     # Comprehensive documentation
├── Jenkinsfile              # CI/CD pipeline
└── docker-compose.yml       # Local development
```

## 🎯 Key Features for Principal Engineer Interview

### Advanced Design Patterns
1. **Saga Pattern**: Distributed transaction management in Order Service
2. **CQRS**: Read-optimized reporting with separate models
3. **Event Sourcing**: Order state tracking via Kafka events
4. **Circuit Breaker**: Resilience4j for fault tolerance
5. **Repository Pattern**: Clean data access abstraction
6. **Factory Pattern**: User type creation strategies
7. **Strategy Pattern**: Multiple payment/authentication methods
8. **Builder Pattern**: Complex object construction
9. **State Pattern**: Order status transitions with validation
10. **Observer Pattern**: Event-driven architecture with Kafka

### Production-Grade Features
- **High Availability**: 3 replicas per service with pod anti-affinity
- **Auto-scaling**: Horizontal Pod Autoscaler (3-10 replicas)
- **Security**: Non-root containers, secrets management, RBAC
- **Monitoring**: Prometheus metrics, health checks, logging
- **Database**: Multi-AZ RDS with automated backups
- **Caching**: Distributed Redis sessions
- **Load Balancing**: AWS ALB with path-based routing

### DevOps Excellence
- **Infrastructure as Code**: Complete Terraform modules
- **GitOps Ready**: Kubernetes manifests in Git
- **Pipeline Automation**: Multi-stage Jenkins pipeline
- **Testing**: Unit, integration, and performance tests
- **Security**: Vulnerability scanning, dependency checks
- **Documentation**: JavaDoc, API docs, deployment guides

## 📊 Cost Breakdown

**Total Monthly Cost: ₹51,000** (approximately $620 USD)

Major components:
- RDS (3 databases): ₹22,500 (44%)
- MSK (Kafka): ₹18,600 (36%)
- EC2 (EKS nodes): ₹9,300 (18%)
- EKS Control Plane: ₹6,200 (12%)

**With Reserved Instances (1 year): ₹38,000/month (26% savings)**

See `docs/COST_ESTIMATE.md` for detailed breakdown.

## 🔑 Default Credentials (Development Only)

### Database
- Username: `postgres`
- Password: `postgres`

### Test Users (from seed data)
- Admin: `admin@ecommerce.com` / `Password123!`
- Customer: `john.doe@example.com` / `Password123!`

**⚠️ Change these in production!**

## 📚 Learning Resources

### Essential Documentation
1. **README.md** - Project overview and features
2. **docs/DEPLOYMENT.md** - Complete deployment guide
3. **docs/API.md** - API reference and examples
4. **docs/COST_ESTIMATE.md** - AWS cost analysis

### Code Examples
- Auth Service: JWT authentication with Redis sessions
- Order Service: Saga pattern with Kafka events
- Report Service: CQRS with read-optimized queries

### Infrastructure
- `infrastructure/terraform/` - AWS infrastructure modules
- `infrastructure/kubernetes/` - EKS deployment manifests
- `Jenkinsfile` - Complete CI/CD pipeline

## 🎓 Interview Talking Points

### System Design
- **Scalability**: Horizontal scaling with Kubernetes HPA
- **Reliability**: Multi-AZ deployment, circuit breakers
- **Performance**: Redis caching, database indexing
- **Security**: JWT tokens, encrypted passwords, RBAC

### Architecture Decisions
- **Microservices**: Independent deployment and scaling
- **Event-Driven**: Decoupled services via Kafka
- **Database per Service**: Data isolation and autonomy
- **CQRS**: Optimized reads for reporting

### DevOps Practices
- **CI/CD**: Automated testing and deployment
- **IaC**: Version-controlled infrastructure
- **Observability**: Metrics, logs, and tracing
- **Cost Optimization**: Right-sizing and reserved instances

## 🐛 Troubleshooting

### Services won't start locally
```bash
docker-compose down -v
docker-compose up -d --build
docker-compose logs -f
```

### Kubernetes pods failing
```bash
kubectl get pods -n ecommerce
kubectl describe pod <pod-name> -n ecommerce
kubectl logs <pod-name> -n ecommerce
```

### Build failures
```bash
mvn clean install -U
# Update dependencies and rebuild
```

## 🚀 Next Steps

1. **Explore the Code**: Review service implementations
2. **Run Locally**: Test with Docker Compose
3. **Deploy to AWS**: Follow deployment guide
4. **Customize**: Add your features and improvements
5. **Practice**: Explain architecture in mock interviews

## 📞 Support

- Documentation: `/docs` directory
- API Reference: `/docs/API.md`
- Deployment Guide: `/docs/DEPLOYMENT.md`
- Cost Analysis: `/docs/COST_ESTIMATE.md`

## 🌟 Key Highlights for Resume

- Built production-grade microservices with 10+ design patterns
- Implemented event-driven architecture with Kafka
- Created complete AWS infrastructure using Terraform
- Designed Kubernetes deployment with auto-scaling
- Established CI/CD pipeline with automated testing
- Optimized for ₹51K/month AWS cost with HA architecture

---

**Built for learning and interview preparation**  
**Target Role: Principal Software Engineer**  
**Technologies: Java 17, Spring Boot 3, Kafka, PostgreSQL, Redis, Kubernetes, Terraform, Jenkins**

Good luck with your interview preparation! 🎯





