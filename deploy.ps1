$ErrorActionPreference = "Stop"

$REGION = "eu-north-1"
# Get Account ID securely
$ACCOUNT_ID = aws sts get-caller-identity --query Account --output text

if (-not $ACCOUNT_ID) {
    Write-Error "Could not retrieve AWS Account ID. Please ensure you are logged in via 'aws configure'."
    exit 1
}

$ECR_URL = "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com"

Write-Host "Logging into ECR ($ECR_URL)..."
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ECR_URL

# --- Backend ---
Write-Host "`n=== Deploying Backend ==="
Write-Host "Building Docker Image..."
docker build -t balancesheet-backend ./backend

Write-Host "Tagging & Pushing to ECR..."
docker tag balancesheet-backend:latest "$ECR_URL/balancesheet-backend:latest"
docker push "$ECR_URL/balancesheet-backend:latest"

Write-Host "Updating ECS Service..."
aws ecs update-service --cluster balancesheet-cluster --service balancesheet-backend --force-new-deployment --region $REGION --no-cli-pager | Out-Null

# --- Frontend ---
Write-Host "`n=== Deploying Frontend ==="
Write-Host "Building Docker Image..."
docker build -t balancesheet-frontend ./frontend

Write-Host "Tagging & Pushing to ECR..."
docker tag balancesheet-frontend:latest "$ECR_URL/balancesheet-frontend:latest"
docker push "$ECR_URL/balancesheet-frontend:latest"

Write-Host "Updating ECS Service..."
aws ecs update-service --cluster balancesheet-cluster --service balancesheet-frontend --force-new-deployment --region $REGION --no-cli-pager | Out-Null

Write-Host "`n✅ Deployment Triggered successfully!"
Write-Host "ECS is now cycling the tasks. Please allow 2-5 minutes for the new versions to become active."
