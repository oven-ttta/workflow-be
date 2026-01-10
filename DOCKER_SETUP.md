# 🐳 Docker Setup Guide

## การรัน Application ด้วย Docker

### Prerequisites
- Docker
- Docker Compose
- Google Gemini API Key

### Quick Start

#### 1. Setup Environment Variables

```bash
# Copy .env.example เป็น .env
cp .env.example .env

# แก้ไข .env และใส่ Gemini API key
nano .env
```

เนื้อหาใน `.env`:
```env
GEMINI_API_KEY=your_actual_gemini_api_key_here
```

#### 2. Build และ Run

```bash
# Build และ Start containers
docker-compose up -d

# ดู logs
docker-compose logs -f

# หรือ ดู logs เฉพาะ app
docker-compose logs -f app
```

#### 3. ตรวจสอบสถานะ

```bash
# ตรวจสอบว่า containers กำลังรันอยู่
docker-compose ps

# ควรเห็นผลลัพธ์แบบนี้:
# NAME                         STATUS          PORTS
# student-management-app      Up              0.0.0.0:8080->8080/tcp
# student-management-db       Up (healthy)    0.0.0.0:5432->5432/tcp
```

#### 4. ทดสอบ API

```bash
# Test health check
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Docker Commands

```bash
# Start containers
docker-compose up -d

# Stop containers
docker-compose down

# Stop และลบ volumes (ระวัง! จะลบข้อมูลทั้งหมด)
docker-compose down -v

# Rebuild containers
docker-compose up -d --build

# View logs
docker-compose logs -f [service-name]

# Access database
docker exec -it student-management-db psql -U postgres -d student_management

# Access app container
docker exec -it student-management-app bash
```

## 📋 Service Details

### PostgreSQL Database
- **Container Name**: student-management-db
- **Port**: 5432
- **Database**: student_management
- **Username**: postgres
- **Password**: postgres123
- **Volume**: postgres-data

### Spring Boot Application
- **Container Name**: student-management-app
- **Port**: 8080
- **Base URL**: http://localhost:8080/api

## 🔧 Configuration

### Environment Variables

แก้ไขใน `docker-compose.yml` หรือใช้ `.env` file:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/student_management
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: postgres123
  JWT_SECRET: YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough
  JWT_EXPIRATION: 86400000
  GEMINI_API_KEY: ${GEMINI_API_KEY}
```

### Port Mapping

ถ้าต้องการเปลี่ยน port:

```yaml
services:
  app:
    ports:
      - "8081:8080"  # เปลี่ยนจาก 8080 เป็น 8081
```

## 🗄️ Database Management

### Access Database

```bash
# เข้าถึง PostgreSQL
docker exec -it student-management-db psql -U postgres -d student_management

# ดู tables
\dt

# ดูข้อมูลใน users table
SELECT * FROM users;

# ออกจาก psql
\q
```

### Backup Database

```bash
# Export database
docker exec -t student-management-db pg_dump -U postgres student_management > backup.sql

# Import database
docker exec -i student-management-db psql -U postgres student_management < backup.sql
```

### Reset Database

```bash
# Stop และลบ containers พร้อม volumes
docker-compose down -v

# Start ใหม่ (database จะถูกสร้างใหม่)
docker-compose up -d
```

## 🐛 Troubleshooting

### Problem: Container ไม่สามารถ start ได้

```bash
# ดู logs
docker-compose logs app

# ตรวจสอบว่า database พร้อมหรือยัง
docker-compose logs postgres
```

### Problem: Database connection error

```bash
# ตรวจสอบว่า postgres container กำลังรัน และ healthy
docker-compose ps

# Restart containers
docker-compose restart
```

### Problem: Port already in use

```bash
# ตรวจสอบว่า port 8080 ถูกใช้งานอยู่หรือไม่
lsof -i :8080

# แก้ไข port ใน docker-compose.yml
# เปลี่ยนจาก "8080:8080" เป็น "8081:8080"
```

### Problem: Out of memory

```bash
# เพิ่ม memory limit ใน docker-compose.yml
services:
  app:
    deploy:
      resources:
        limits:
          memory: 1G
```

### Problem: Cannot connect to Gemini API

```bash
# ตรวจสอบว่าใส่ API key แล้ว
docker-compose exec app env | grep GEMINI

# Update API key
# แก้ไขใน .env และ restart
docker-compose restart app
```

## 📊 Monitoring

### View Application Logs

```bash
# All logs
docker-compose logs -f

# Only app logs
docker-compose logs -f app

# Only database logs
docker-compose logs -f postgres

# Last 100 lines
docker-compose logs --tail=100 app
```

### Resource Usage

```bash
# ดูการใช้ resources
docker stats

# ดูเฉพาะ containers ของเรา
docker stats student-management-app student-management-db
```

## 🚀 Production Deployment

### Security Best Practices

1. **เปลี่ยน default passwords**

```yaml
environment:
  POSTGRES_PASSWORD: your_secure_password_here
  JWT_SECRET: your_very_long_random_secret_key_here
```

2. **ใช้ secrets management**

```bash
# ใช้ Docker secrets แทน environment variables
docker secret create jwt_secret jwt_secret.txt
```

3. **Enable HTTPS**

ใช้ reverse proxy เช่น Nginx:

```yaml
services:
  nginx:
    image: nginx:alpine
    ports:
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
```

4. **Limit resource usage**

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

### Health Checks

```yaml
services:
  app:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

## 📝 Example Workflows

### Development Workflow

```bash
# 1. Start services
docker-compose up -d

# 2. Watch logs
docker-compose logs -f app

# 3. Make code changes

# 4. Rebuild และ restart
docker-compose up -d --build app

# 5. Test changes
curl http://localhost:8080/api/...
```

### Production Deployment

```bash
# 1. Pull latest code
git pull origin main

# 2. Build images
docker-compose build

# 3. Stop old containers
docker-compose down

# 4. Start new containers
docker-compose up -d

# 5. Verify deployment
docker-compose ps
docker-compose logs -f app
```

## 🔄 Updating

### Update Application Code

```bash
# Rebuild และ restart
docker-compose up -d --build app
```

### Update Database Schema

```bash
# 1. เข้าถึง database
docker exec -it student-management-db psql -U postgres -d student_management

# 2. Run migration commands
ALTER TABLE ...
```

## 📦 Backup & Restore

### Full Backup

```bash
# Backup database
docker exec -t student-management-db pg_dump -U postgres student_management > backup_$(date +%Y%m%d).sql

# Backup volumes
docker run --rm -v student-management_postgres-data:/data -v $(pwd):/backup alpine tar czf /backup/postgres-data-backup.tar.gz -C /data .
```

### Full Restore

```bash
# Restore database
docker exec -i student-management-db psql -U postgres student_management < backup.sql

# Restore volumes
docker run --rm -v student-management_postgres-data:/data -v $(pwd):/backup alpine tar xzf /backup/postgres-data-backup.tar.gz -C /data
```

## 🎯 Next Steps

1. Setup `.env` file with your Gemini API key
2. Run `docker-compose up -d`
3. Access application at http://localhost:8080/api
4. Import Postman collection for testing
5. Deploy to production server

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [PostgreSQL Docker Image](https://hub.docker.com/_/postgres)