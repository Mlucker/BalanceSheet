# Docker Compose Startup Guide

## ⚠️ IMPORTANT: Stop Local Development Servers First!

Before running Docker Compose, you **must stop** your local development servers:

### 1. Stop Frontend (npm run dev)
In the terminal running `npm run dev`:
- Press `Ctrl+C` to stop the Vite dev server
- Or close that terminal window

### 2. Stop Backend (if running)
If you have Spring Boot running locally:
- Stop the process (Ctrl+C or stop in IDE)

### 3. Stop Local PostgreSQL (if running)
If you have a local PostgreSQL instance on port 5432:
```powershell
# Stop the service
Stop-Service postgresql-x64-15
```

---

## 🚀 Starting with Docker Compose

Once local servers are stopped:

```powershell
# Navigate to project root
cd c:\Users\Nutzer\source\repos\BalanceSheet

# Start all services (builds images first time)
docker-compose up -d --build

# View logs (optional)
docker-compose logs -f

# Check status
docker-compose ps
```

### What Happens:
1. **Database starts** (5-10 seconds)
   - Health check waits for PostgreSQL to be ready
2. **Backend starts** (30-40 seconds)
   - Waits for database to be healthy
   - Builds JAR, starts Spring Boot
   - Initializes demo data
   - Health check waits for API to respond
3. **Frontend starts** (5 seconds)
   - Waits for backend to be healthy
   - Serves static files via Nginx

### Access the Application:
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api
- **Database**: localhost:5432

---

## 🛑 Stopping Docker Compose

```powershell
# Stop all containers
docker-compose down

# Stop and remove volumes (fresh start)
docker-compose down -v
```

---

## 🔄 Switching Between Local Dev and Docker

### Use Local Development (Current Setup):
```powershell
# Terminal 1: Backend
cd backend
.\mvnw spring-boot:run

# Terminal 2: Frontend
cd frontend
npm run dev

# Terminal 3: PostgreSQL (Docker)
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=balancesheet \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  postgres:15-alpine
```

### Use Docker Compose (Portfolio Demo):
```powershell
# Stop all local servers first!
# Then:
docker-compose up -d --build
```

---

## 🐛 Troubleshooting

### Port Already in Use
**Error**: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**Solution**: Stop local backend server

### Database Connection Failed
**Error**: `UnknownHostException: db`

**Solution**: 
1. Make sure you're using `docker-compose up` (not `docker run`)
2. Check network: `docker network ls`
3. Restart: `docker-compose down && docker-compose up -d`

### Backend Won't Start
**Error**: `Unable to build Hibernate SessionFactory`

**Solution**:
1. Check database is healthy: `docker-compose ps`
2. View logs: `docker-compose logs db`
3. Fresh start: `docker-compose down -v && docker-compose up -d --build`

### Slow Startup
- First build takes 2-3 minutes (downloads dependencies)
- Subsequent builds are faster (uses cache)
- Backend startup takes 30-40 seconds (normal for Spring Boot)

---

## 📝 Useful Commands

```powershell
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend

# Restart a service
docker-compose restart backend

# Rebuild and restart
docker-compose up -d --build backend

# Check health status
docker-compose ps

# Execute command in container
docker-compose exec backend bash
docker-compose exec db psql -U postgres -d balancesheet

# Clean everything
docker-compose down -v --rmi all
```

---

## ✅ Recommended Workflow

**For Development** (faster iteration):
- Use local servers (npm run dev + mvnw spring-boot:run)
- Hot reload works
- Faster feedback loop

**For Portfolio Demo** (production-like):
- Use Docker Compose
- Shows deployment skills
- Easier for others to run
- Production-ready setup

**For Screenshots/Video**:
- Use Docker Compose
- Shows the complete, containerized application
