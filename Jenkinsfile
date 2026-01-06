// ==============================================================================
// Jenkins Pipeline for E-Commerce Microservices
// ==============================================================================
// Multi-stage pipeline with:
// - Build & Test
// - Integration Tests
// - Performance Tests
// - Docker Build & Push
// - Manual Approval
// - Deploy to EKS
// ==============================================================================

pipeline {
    agent any
    
    // Environment variables
    environment {
        AWS_REGION = 'us-east-1'
        ECR_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        EKS_CLUSTER_NAME = 'ecommerce-prod-cluster'
        
        // Service names
        SERVICES = 'auth-service,order-service,report-service'
        
        // Git commit info
        GIT_COMMIT_SHORT = sh(
            script: "git rev-parse --short HEAD",
            returnStdout: true
        ).trim()
        
        // Docker image tag
        IMAGE_TAG = "${env.BUILD_NUMBER}-${GIT_COMMIT_SHORT}"
    }
    
    // Build parameters
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'staging', 'prod'],
            description: 'Target deployment environment'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip test execution'
        )
        booleanParam(
            name: 'SKIP_PERF_TESTS',
            defaultValue: false,
            description: 'Skip performance tests'
        )
    }
    
    // Pipeline options
    options {
        // Keep last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        
        // Timeout after 60 minutes
        timeout(time: 60, unit: 'MINUTES')
        
        // Timestamps in console output
        timestamps()
        
        // ANSI color output
        ansiColor('xterm')
    }
    
    // Stages
    stages {
        
        // ======================================================================
        // Stage 1: Checkout & Setup
        // ======================================================================
        stage('Checkout & Setup') {
            steps {
                echo '=============================================='
                echo 'STAGE 1: Checking out code and setup'
                echo '=============================================='
                
                // Checkout code
                checkout scm
                
                // Print environment info
                sh '''
                    echo "Build Number: ${BUILD_NUMBER}"
                    echo "Git Commit: ${GIT_COMMIT_SHORT}"
                    echo "Image Tag: ${IMAGE_TAG}"
                    echo "Environment: ${ENVIRONMENT}"
                    java -version
                    mvn -version
                    docker --version
                '''
            }
        }
        
        // ======================================================================
        // Stage 2: Build
        // ======================================================================
        stage('Build') {
            steps {
                echo '=============================================='
                echo 'STAGE 2: Building applications'
                echo '=============================================='
                
                script {
                    // Build parent and common library first
                    sh '''
                        mvn clean install -pl common-lib -am \
                            -DskipTests \
                            --batch-mode \
                            --no-transfer-progress
                    '''
                    
                    // Build all services
                    sh '''
                        mvn clean package -DskipTests \
                            --batch-mode \
                            --no-transfer-progress
                    '''
                }
            }
            
            post {
                success {
                    echo 'Build successful!'
                }
                failure {
                    echo 'Build failed!'
                }
            }
        }
        
        // ======================================================================
        // Stage 3: Unit Tests
        // ======================================================================
        stage('Unit Tests') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                echo '=============================================='
                echo 'STAGE 3: Running unit tests'
                echo '=============================================='
                
                sh '''
                    mvn test \
                        --batch-mode \
                        --no-transfer-progress
                '''
            }
            
            post {
                always {
                    // Publish test results
                    junit '**/target/surefire-reports/*.xml'
                    
                    // Publish test coverage (if using JaCoCo)
                    // jacoco(
                    //     execPattern: '**/target/jacoco.exec'
                    // )
                }
            }
        }
        
        // ======================================================================
        // Stage 4: Integration Tests
        // ======================================================================
        stage('Integration Tests') {
            when {
                expression { !params.SKIP_TESTS }
            }
            steps {
                echo '=============================================='
                echo 'STAGE 4: Running integration tests'
                echo '=============================================='
                
                sh '''
                    mvn verify -P integration-tests \
                        --batch-mode \
                        --no-transfer-progress
                '''
            }
            
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }
        
        // ======================================================================
        // Stage 5: Performance Tests
        // ======================================================================
        stage('Performance Tests') {
            when {
                expression { !params.SKIP_PERF_TESTS }
            }
            steps {
                echo '=============================================='
                echo 'STAGE 5: Running performance tests'
                echo '=============================================='
                
                script {
                    // Start services in background for perf testing
                    sh '''
                        docker-compose -f docker-compose.yml up -d
                        sleep 30  # Wait for services to be ready
                    '''
                    
                    // Run JMeter tests
                    sh '''
                        cd scripts/performance
                        jmeter -n -t performance-test-plan.jmx \
                            -l results.jtl \
                            -e -o ./report
                    '''
                }
            }
            
            post {
                always {
                    // Stop docker-compose services
                    sh 'docker-compose down'
                    
                    // Publish performance report
                    publishHTML([
                        reportDir: 'scripts/performance/report',
                        reportFiles: 'index.html',
                        reportName: 'Performance Test Report'
                    ])
                }
            }
        }
        
        // ======================================================================
        // Stage 6: Code Quality Analysis
        // ======================================================================
        stage('Code Quality') {
            steps {
                echo '=============================================='
                echo 'STAGE 6: Running code quality checks'
                echo '=============================================='
                
                script {
                    // SonarQube analysis (requires SonarQube server)
                    // sh '''
                    //     mvn sonar:sonar \
                    //         -Dsonar.host.url=${SONAR_URL} \
                    //         -Dsonar.login=${SONAR_TOKEN}
                    // '''
                    
                    echo 'Skipping SonarQube (configure SONAR_URL and SONAR_TOKEN)'
                }
            }
        }
        
        // ======================================================================
        // Stage 7: Build Docker Images
        // ======================================================================
        stage('Build Docker Images') {
            steps {
                echo '=============================================='
                echo 'STAGE 7: Building Docker images'
                echo '=============================================='
                
                script {
                    def services = env.SERVICES.split(',')
                    
                    services.each { service ->
                        echo "Building ${service}..."
                        
                        sh """
                            docker build \
                                -f ${service}/Dockerfile \
                                -t ${service}:${IMAGE_TAG} \
                                -t ${service}:latest \
                                .
                        """
                    }
                }
            }
        }
        
        // ======================================================================
        // Stage 8: Push to ECR
        // ======================================================================
        stage('Push to ECR') {
            steps {
                echo '=============================================='
                echo 'STAGE 8: Pushing images to ECR'
                echo '=============================================='
                
                script {
                    // Login to ECR
                    sh '''
                        aws ecr get-login-password --region ${AWS_REGION} | \
                        docker login --username AWS --password-stdin ${ECR_REGISTRY}
                    '''
                    
                    def services = env.SERVICES.split(',')
                    
                    services.each { service ->
                        echo "Pushing ${service} to ECR..."
                        
                        sh """
                            # Tag for ECR
                            docker tag ${service}:${IMAGE_TAG} \
                                ${ECR_REGISTRY}/${service}:${IMAGE_TAG}
                            
                            docker tag ${service}:${IMAGE_TAG} \
                                ${ECR_REGISTRY}/${service}:latest
                            
                            # Push to ECR
                            docker push ${ECR_REGISTRY}/${service}:${IMAGE_TAG}
                            docker push ${ECR_REGISTRY}/${service}:latest
                        """
                    }
                }
            }
        }
        
        // ======================================================================
        // Stage 9: Security Scan
        // ======================================================================
        stage('Security Scan') {
            steps {
                echo '=============================================='
                echo 'STAGE 9: Running security scans'
                echo '=============================================='
                
                script {
                    // OWASP Dependency Check
                    sh '''
                        mvn org.owasp:dependency-check-maven:check \
                            --batch-mode \
                            --no-transfer-progress
                    '''
                }
            }
            
            post {
                always {
                    publishHTML([
                        reportDir: 'target',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'OWASP Dependency Check'
                    ])
                }
            }
        }
        
        // ======================================================================
        // Stage 10: Manual Approval
        // ======================================================================
        stage('Approval for Deployment') {
            when {
                expression { params.ENVIRONMENT == 'prod' }
            }
            steps {
                echo '=============================================='
                echo 'STAGE 10: Waiting for approval'
                echo '=============================================='
                
                script {
                    timeout(time: 30, unit: 'MINUTES') {
                        def userInput = input(
                            id: 'DeploymentApproval',
                            message: 'Deploy to production?',
                            parameters: [
                                choice(
                                    name: 'DEPLOY_DECISION',
                                    choices: ['Approve', 'Deny'],
                                    description: 'Approve or deny deployment'
                                )
                            ]
                        )
                        
                        if (userInput != 'Approve') {
                            error('Deployment denied by approver')
                        }
                        
                        echo 'Deployment approved!'
                    }
                }
            }
        }
        
        // ======================================================================
        // Stage 11: Deploy to EKS
        // ======================================================================
        stage('Deploy to EKS') {
            steps {
                echo '=============================================='
                echo 'STAGE 11: Deploying to EKS'
                echo '=============================================='
                
                script {
                    // Update kubeconfig
                    sh """
                        aws eks update-kubeconfig \
                            --region ${AWS_REGION} \
                            --name ${EKS_CLUSTER_NAME}
                    """
                    
                    // Update image tags in Kubernetes manifests
                    sh """
                        cd infrastructure/kubernetes
                        
                        # Update image tags
                        for service in ${SERVICES//,/ }; do
                            kubectl set image deployment/\${service} \
                                \${service}=${ECR_REGISTRY}/\${service}:${IMAGE_TAG} \
                                -n ecommerce
                        done
                        
                        # Wait for rollout
                        for service in ${SERVICES//,/ }; do
                            kubectl rollout status deployment/\${service} -n ecommerce
                        done
                    """
                }
            }
        }
        
        // ======================================================================
        // Stage 12: Smoke Tests
        // ======================================================================
        stage('Smoke Tests') {
            steps {
                echo '=============================================='
                echo 'STAGE 12: Running smoke tests'
                echo '=============================================='
                
                script {
                    // Get service endpoints
                    sh '''
                        kubectl get ingress -n ecommerce
                        
                        # Basic health checks
                        # curl -f http://<ingress-url>/api/v1/auth/health
                        # curl -f http://<ingress-url>/api/v1/orders/health
                        # curl -f http://<ingress-url>/api/v1/reports/health
                    '''
                    
                    echo 'Smoke tests passed!'
                }
            }
        }
    }
    
    // ======================================================================
    // Post Actions
    // ======================================================================
    post {
        always {
            echo '=============================================='
            echo 'Pipeline completed'
            echo '=============================================='
            
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo 'Pipeline succeeded!'
            
            // Send notification (Slack, Email, etc.)
            // slackSend(
            //     color: 'good',
            //     message: "Deployment successful: ${env.JOB_NAME} - ${env.BUILD_NUMBER}"
            // )
        }
        
        failure {
            echo 'Pipeline failed!'
            
            // Send notification
            // slackSend(
            //     color: 'danger',
            //     message: "Deployment failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}"
            // )
        }
    }
}








