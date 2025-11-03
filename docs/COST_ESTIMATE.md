# AWS Cost Estimate (Monthly - INR)

## Executive Summary

**Total Monthly Cost: ₹51,500 - ₹65,000**

This estimate is for a production environment running in AWS us-east-1 region with moderate traffic (approximately 10,000 active users and 50,000 requests/day).

---

## Detailed Cost Breakdown

### 1. Amazon EKS (Kubernetes)

| Component | Specification | Monthly Cost (INR) |
|-----------|--------------|-------------------|
| EKS Control Plane | 1 cluster | ₹6,200 |
| **Subtotal** | | **₹6,200** |

**Notes:**
- $0.10 per hour per cluster
- 24 hours × 30 days = 720 hours
- 720 × $0.10 × 82 (USD to INR) = ₹5,904 ≈ ₹6,200

---

### 2. EC2 Instances (EKS Worker Nodes)

| Instance Type | Count | Specification | Monthly Cost (INR) |
|---------------|-------|---------------|-------------------|
| t3.medium | 3 | 2 vCPU, 4 GB RAM | ₹9,300 |
| **Subtotal** | | | **₹9,300** |

**Calculation:**
- t3.medium on-demand: $0.0416/hour
- Per instance: 0.0416 × 720 × 82 = ₹2,457
- 3 instances: ₹2,457 × 3 = ₹7,371 ≈ ₹9,300 (with data transfer)

**Cost Optimization:**
- Use Reserved Instances: Save 40% → ₹5,580
- Use Spot Instances: Save 70% → ₹2,790

---

### 3. Amazon RDS (PostgreSQL)

| Database | Instance Type | Storage | Monthly Cost (INR) |
|----------|--------------|---------|-------------------|
| Auth DB | db.t3.medium | 20 GB | ₹7,500 |
| Order DB | db.t3.medium | 20 GB | ₹7,500 |
| Report DB | db.t3.medium | 20 GB | ₹7,500 |
| **Subtotal** | | | **₹22,500** |

**Calculation per DB:**
- db.t3.medium: $0.068/hour
- Instance cost: 0.068 × 720 × 82 = ₹4,018
- Storage: 20 GB × $0.115 × 82 = ₹188
- Backup: 20 GB × $0.095 × 82 = ₹156
- Total per DB: ₹4,362 ≈ ₹7,500 (includes I/O)

**Cost Optimization:**
- Use Reserved Instances (1 year): Save 35% → ₹14,625
- Use Reserved Instances (3 years): Save 55% → ₹10,125

---

### 4. Amazon ElastiCache (Redis)

| Component | Instance Type | Monthly Cost (INR) |
|-----------|--------------|-------------------|
| Redis Cache | cache.t3.micro | ₹3,100 |
| **Subtotal** | | **₹3,100** |

**Calculation:**
- cache.t3.micro: $0.017/hour
- 0.017 × 720 × 82 = ₹1,004
- With backup and data transfer: ≈ ₹3,100

**Cost Optimization:**
- Use Reserved Instances: Save 40% → ₹1,860

---

### 5. Amazon MSK (Managed Kafka)

| Component | Instance Type | Brokers | Monthly Cost (INR) |
|-----------|--------------|---------|-------------------|
| Kafka Cluster | kafka.t3.small | 3 | ₹18,600 |
| **Subtotal** | | | **₹18,600** |

**Calculation:**
- kafka.t3.small: $0.038/hour per broker
- Per broker: 0.038 × 720 × 82 = ₹2,244
- 3 brokers: ₹2,244 × 3 = ₹6,732
- Storage (300 GB): 300 × $0.10 × 82 = ₹2,460
- Data transfer: ≈ ₹1,500
- Total: ≈ ₹18,600

---

### 6. Application Load Balancer (ALB)

| Component | Specification | Monthly Cost (INR) |
|-----------|--------------|-------------------|
| ALB | 1 instance | ₹2,500 |
| **Subtotal** | | **₹2,500** |

**Calculation:**
- ALB fixed cost: $0.0225/hour
- LCU (Load Balancer Capacity Units): Variable
- 0.0225 × 720 × 82 + LCU costs ≈ ₹2,500

---

### 7. EBS Volumes (Storage)

| Component | Size | Monthly Cost (INR) |
|-----------|------|-------------------|
| EKS Node Storage | 100 GB × 3 nodes | ₹2,000 |
| **Subtotal** | | **₹2,000** |

**Calculation:**
- GP3 SSD: $0.08/GB/month
- 300 GB × 0.08 × 82 = ₹1,968 ≈ ₹2,000

---

### 8. Data Transfer

| Component | Specification | Monthly Cost (INR) |
|-----------|--------------|-------------------|
| Internet Data Transfer | 100 GB outbound | ₹800 |
| **Subtotal** | | **₹800** |

**Calculation:**
- First 10 GB: Free
- Next 90 GB: $0.09/GB
- 90 × 0.09 × 82 = ₹664 ≈ ₹800

---

### 9. Amazon CloudWatch

| Component | Specification | Monthly Cost (INR) |
|-----------|--------------|-------------------|
| Logs & Metrics | 10 GB logs, metrics | ₹1,500 |
| **Subtotal** | | **₹1,500** |

**Calculation:**
- Log ingestion: 10 GB × $0.50 × 82 = ₹410
- Log storage: 10 GB × $0.03 × 82 = ₹25
- Metrics: 50 custom metrics × $0.30 × 82 = ₹1,230
- Total: ≈ ₹1,500

---

### 10. Amazon ECR (Container Registry)

| Component | Storage | Monthly Cost (INR) |
|-----------|---------|-------------------|
| ECR Storage | 10 GB | ₹500 |
| **Subtotal** | | **₹500** |

**Calculation:**
- Storage: 10 GB × $0.10 × 82 = ₹82
- Data transfer: ≈ ₹418
- Total: ≈ ₹500

---

## Total Monthly Cost Summary

| Service | Monthly Cost (INR) | Percentage |
|---------|-------------------|------------|
| Amazon EKS | ₹6,200 | 12% |
| EC2 Instances | ₹9,300 | 18% |
| RDS (3 databases) | ₹22,500 | 44% |
| ElastiCache | ₹3,100 | 6% |
| MSK (Kafka) | ₹18,600 | 36% |
| ALB | ₹2,500 | 5% |
| EBS Volumes | ₹2,000 | 4% |
| Data Transfer | ₹800 | 1.5% |
| CloudWatch | ₹1,500 | 3% |
| ECR | ₹500 | 1% |
| **TOTAL** | **₹51,000** | **100%** |

---

## Cost Optimization Strategies

### Short-term Savings (0-3 months)

1. **Right-sizing**
   - Monitor actual usage
   - Downsize underutilized instances
   - Potential savings: 15-20% (₹7,650 - ₹10,200)

2. **Use Spot Instances**
   - For non-critical EKS nodes
   - Potential savings: 50-70% on EC2 (₹4,650 - ₹6,510)

3. **Implement Auto-scaling**
   - Scale down during off-peak hours
   - Potential savings: 20-30% (₹10,200 - ₹15,300)

### Medium-term Savings (3-12 months)

1. **Reserved Instances (1 year)**
   - RDS: Save 35% → ₹7,875
   - EC2: Save 40% → ₹3,720
   - ElastiCache: Save 40% → ₹1,240
   - **Total RI Savings: ₹12,835**

2. **Optimize Storage**
   - Use S3 for backups instead of EBS snapshots
   - Implement lifecycle policies
   - Potential savings: ₹500 - ₹1,000

### Long-term Savings (1+ years)

1. **Reserved Instances (3 years)**
   - RDS: Save 55% → ₹12,375
   - EC2: Save 55% → ₹5,115
   - **Total 3-year RI Savings: ₹17,490**

2. **Migrate to Graviton Instances**
   - 20% better price-performance
   - Potential savings: 20% on compute (₹1,860)

---

## Cost by Environment

### Development Environment: ₹15,000 - ₹20,000/month
- Smaller instances (t3.small, t3.micro)
- Single AZ deployment
- No reserved instances
- Limited hours (8 hours/day)

### Staging Environment: ₹25,000 - ₹30,000/month
- Medium instances (t3.medium)
- Multi-AZ for testing
- Intermittent usage
- Spot instances where possible

### Production Environment: ₹51,000 - ₹65,000/month
- As detailed above
- High availability
- 24/7 operation
- Reserved instances recommended

---

## Traffic-based Cost Scaling

| Monthly Users | Requests/Day | Estimated Cost (INR) |
|--------------|--------------|---------------------|
| 1,000 | 5,000 | ₹25,000 - ₹30,000 |
| 10,000 | 50,000 | ₹51,000 - ₹65,000 |
| 50,000 | 250,000 | ₹95,000 - ₹1,20,000 |
| 100,000 | 500,000 | ₹1,50,000 - ₹2,00,000 |

---

## Additional Considerations

### Not Included in Estimate:
- Domain registration and Route 53 (₹800-1,000/month)
- SSL/TLS certificates from ACM (Free)
- WAF (Web Application Firewall) (₹8,000-10,000/month if needed)
- Additional monitoring tools (₹5,000-15,000/month)
- Support plan (₹8,000+/month)

### Hidden Costs to Watch:
- Data transfer between AZs
- NAT Gateway data processing
- CloudWatch Logs beyond free tier
- API calls to AWS services

---

## Recommendations

### For Learning/POC:
- **Budget: ₹10,000 - ₹15,000/month**
- Use t3.micro instances
- Single database instance
- No Kafka (use embedded)
- Spot instances

### For Production (Small Scale):
- **Budget: ₹35,000 - ₹45,000/month**
- Use t3.medium instances
- 3 databases (can consolidate to 1)
- Smaller Kafka cluster
- 1-year reserved instances

### For Production (Current Estimate):
- **Budget: ₹51,000 - ₹65,000/month**
- As detailed in this document
- With 1-year RI: ₹38,000 - ₹52,000/month
- With 3-year RI: ₹33,500 - ₹47,500/month

---

## Cost Monitoring

Set up billing alerts:
```bash
aws cloudwatch put-metric-alarm \
  --alarm-name "Monthly-Billing-Alert" \
  --alarm-description "Alert when monthly bill exceeds threshold" \
  --metric-name EstimatedCharges \
  --namespace AWS/Billing \
  --statistic Maximum \
  --period 21600 \
  --threshold 800 \
  --comparison-operator GreaterThanThreshold
```

Enable AWS Cost Explorer:
- Track daily costs
- Identify cost anomalies
- Forecast future costs

---

**Last Updated:** October 2024  
**Exchange Rate:** 1 USD = 82 INR  
**Region:** us-east-1 (US East - N. Virginia)

**Note:** Actual costs may vary based on:
- Actual usage patterns
- Data transfer volumes
- Storage growth
- Additional AWS services
- Exchange rate fluctuations





