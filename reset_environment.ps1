$ErrorActionPreference = "Stop"

Write-Warning "⚠️  WARNING: This script will DESTROY ALL RESOURCES created by Terraform."
Write-Warning "This includes the Database (DATA LOSS), Load Balancer, ECS Service, and ECR Repositories."
Write-Host "Are you sure you want to proceed? (Type 'y' to confirm)" -ForegroundColor Yellow
$confirmation = Read-Host

if ($confirmation -ne 'y') {
    Write-Host "Aborted."
    exit
}

# Navigate to terraform directory
Set-Location ./terraform

Write-Host "`n🔥 Destroying Infrastructure..." -ForegroundColor Red
terraform destroy -auto-approve

Write-Host "`n🏗️  Recreating Infrastructure..." -ForegroundColor Green
terraform apply -auto-approve

# Return to root
Set-Location ..

Write-Host "`n🚀 Deploying Application Code..." -ForegroundColor Cyan
.\deploy.ps1

Write-Host "`n✅ Environment Reset Complete!" -ForegroundColor Green
