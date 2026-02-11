# Variables
$REGION = "eu-north-1"
$ACCOUNT_ID = "515214870588"
$BACKEND_REPO = "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/balancesheet-backend"
$FRONTEND_REPO = "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/balancesheet-frontend"

# Login to ECR
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin "$ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com"

# Build and Push Backend
Write-Host "Building Backend..."
docker build -t balancesheet-backend ../backend
docker tag balancesheet-backend:latest "$BACKEND_REPO` :latest"
docker push "$BACKEND_REPO` :latest"

# Build and Push Frontend
Write-Host "Building Frontend..."
docker build -t balancesheet-frontend ../frontend
docker tag balancesheet-frontend:latest "$FRONTEND_REPO` :latest"
docker push "$FRONTEND_REPO` :latest"

# Force new deployment
aws ecs update-service --cluster balancesheet-cluster --service balancesheet-backend --force-new-deployment
aws ecs update-service --cluster balancesheet-cluster --service balancesheet-frontend --force-new-deployment

Write-Host "Deployment trigger sent!"
