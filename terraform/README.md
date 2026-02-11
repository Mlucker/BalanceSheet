# AWS Deployment with Terraform

This directory contains the Terraform configuration to deploy the BalanceSheet application to AWS.

## Prerequisites

1.  **Terraform**: Install Terraform (v1.0+).
2.  **AWS CLI**: Install and configure the AWS CLI with your credentials (`aws configure`).
3.  **Docker**: Install Docker to build and push images.

## Configuration

Initialize Terraform:

```bash
terraform init
```

## Deployment Steps

1.  **Provision Infrastructure**:

    ```bash
    terraform apply -var="db_password=YOUR_SECURE_PASSWORD"
    ```
    *Note: Replace `YOUR_SECURE_PASSWORD` with a strong password for the RDS database.*

2.  **Get Outputs**:
    After a successful apply, Terraform will output resource details. Note the following:
    -   `ecr_backend_url`
    -   `ecr_frontend_url`
    -   `alb_dns_name`
    -   `rds_endpoint`

3.  **Build and Push Docker Images**:

    **Backend:**
    ```bash
    aws ecr get-login-password --region eu-north-1 | docker login --username AWS --password-stdin <ECR_BACKEND_URL_DOMAIN>
    docker build -t balancesheet-backend ../backend
    docker tag balancesheet-backend:latest <ECR_BACKEND_URL>:latest
    docker push <ECR_BACKEND_URL>:latest
    ```

    **Frontend:**
    ```bash
    aws ecr get-login-password --region eu-north-1 | docker login --username AWS --password-stdin <ECR_FRONTEND_URL_DOMAIN>
    docker build -t balancesheet-frontend ../frontend
    docker tag balancesheet-frontend:latest <ECR_FRONTEND_URL>:latest
    docker push <ECR_FRONTEND_URL>:latest
    ```

4.  **Restart ECS Services** (to pick up new images):
    
    You can force a new deployment via the AWS Console or CLI:
    ```bash
    aws ecs update-service --cluster balancesheet-cluster --service balancesheet-backend --force-new-deployment
    aws ecs update-service --cluster balancesheet-cluster --service balancesheet-frontend --force-new-deployment
    ```

5.  **Access Application**:
    Open the `alb_dns_name` in your browser.

## cleanup

To destroy all resources:

```bash
terraform destroy -var="db_password=YOUR_SECURE_PASSWORD"
```
