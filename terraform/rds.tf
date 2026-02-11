resource "aws_db_subnet_group" "default" {
  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = module.vpc.private_subnets

  tags = {
    Name = "${var.project_name}-db-subnet-group"
  }
}

module "db" {
  source  = "terraform-aws-modules/rds/aws"
  version = "6.0.0"

  identifier = "${var.project_name}-db"

  engine            = "postgres"
  engine_version    = "14"
  instance_class    = "db.t3.micro"
  allocated_storage = 20

  db_name  = "balancesheet"
  username = var.db_username
  password = var.db_password
  port     = "5432"

  vpc_security_group_ids = [module.rds_sg.security_group_id]

  # Database Deletion Protection
  deletion_protection = false
  skip_final_snapshot = true

  create_db_subnet_group = false
  db_subnet_group_name   = aws_db_subnet_group.default.name

  family = "postgres14"
  major_engine_version = "14"
}
