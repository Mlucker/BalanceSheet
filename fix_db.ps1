# Fix Database Password Mismatch
Write-Host "Forcing Database Replacement to fix password mismatch..." -ForegroundColor Cyan

# Navigate to terraform directory
Set-Location terraform

# Run terraform apply with replace
terraform apply -replace="module.db.module.db_instance.aws_db_instance.this[0]" -auto-approve

if ($LASTEXITCODE -eq 0) {
    Write-Host "Database replaced successfully! The app should connect and seed data in 2-3 minutes." -ForegroundColor Green
}
else {
    Write-Host "Terraform failed. Please check the errors above." -ForegroundColor Red
}

# Go back
Set-Location ..
