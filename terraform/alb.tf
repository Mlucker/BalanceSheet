module "alb" {
  source  = "terraform-aws-modules/alb/aws"
  version = "8.0.0"

  name = "${var.project_name}-alb"

  load_balancer_type = "application"

  vpc_id          = module.vpc.vpc_id
  subnets         = module.vpc.public_subnets
  security_groups = [module.alb_sg.security_group_id]

  target_groups = [
    {
      name_prefix      = "front-"
      backend_protocol = "HTTP"
      backend_port     = 80
      target_type      = "ip"
    },
    {
      name_prefix      = "back-"
      backend_protocol = "HTTP"
      backend_port     = 8080
      target_type      = "ip"
      health_check = {
        path = "/actuator/health"
      }
    }
  ]

  http_tcp_listeners = [
    {
      port               = 80
      protocol           = "HTTP"
      target_group_index = 0 # Default to Frontend
    }
  ]
}

resource "aws_lb_listener_rule" "backend_rule" {
  listener_arn = module.alb.http_tcp_listener_arns[0]
  priority     = 100

  action {
    type             = "forward"
    target_group_arn = module.alb.target_group_arns[1] # Backend Target Group
  }

  condition {
    path_pattern {
      values = ["/api/*"]
    }
  }
}
