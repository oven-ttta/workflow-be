# 📚 Student Part-time Management System - Documentation Index

ยินดีต้อนรับสู่ระบบจัดการน้องนักเรียน Part-time! เอกสารนี้จะช่วยคุณในการเริ่มต้นใช้งานระบบ

## 🚀 เริ่มต้นอย่างรวดเร็ว

เลือกวิธีการติดตั้งที่เหมาะกับคุณ:

### 🏃 Quick Start (แนะนำสำหรับผู้เริ่มต้น)
👉 [QUICK_START.md](QUICK_START.md)
- คำแนะนำทีละขั้นตอน
- ตัวอย่าง API calls พร้อมใช้
- Troubleshooting tips

### 🐳 Docker Setup (แนะนำสำหรับ Production)
👉 [DOCKER_SETUP.md](DOCKER_SETUP.md)
- Setup ด้วย Docker Compose
- Production deployment guide
- Container management

## 📖 เอกสารหลัก

### 1. Overview & Features
👉 [README.md](README.md) - **เริ่มที่นี่!**
- ภาพรวมระบบทั้งหมด
- รายการฟีเจอร์ทั้งหมด
- API Documentation แบบละเอียด
- ตัวอย่าง Request/Response

### 2. Project Architecture
👉 [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)
- โครงสร้างโปรเจคแบบละเอียด
- คำอธิบายแต่ละ Layer
- Database Relations
- Security Flow

### 3. Summary
👉 [SUMMARY.md](SUMMARY.md)
- สรุประบบโดยรวม
- Key Features
- Technology Stack
- Deliverables

## 🗄️ Database

### Database Schema
👉 [database-schema.sql](database-schema.sql)
- SQL script สำหรับสร้าง tables
- Relations และ constraints
- Indexes
- Default admin user

**Tables:**
- `users` - ข้อมูลผู้ใช้ทั้งหมด
- `timetable_slots` - ตารางเรียนของแต่ละคน
- `projects` - ข้อมูลโปรเจค
- `project_members` - สมาชิกในโปรเจค

## ⚙️ Configuration Files

### Maven Configuration
👉 [pom.xml](pom.xml)
- Dependencies ทั้งหมด
- Spring Boot plugins
- Build configuration

### Application Properties
👉 [src/main/resources/application.properties](src/main/resources/application.properties)
- Database connection
- JWT configuration
- Gemini API settings

### Docker Configuration
- 👉 [Dockerfile](Dockerfile) - Build image
- 👉 [docker-compose.yml](docker-compose.yml) - Container orchestration
- 👉 [.env.example](.env.example) - Environment variables template

## 🧪 Testing

### API Collection
👉 [postman_collection.json](postman_collection.json)
- Import ใน Postman
- ครบทุก API endpoints
- พร้อม example requests

## 📂 Source Code Structure

```
src/
└── main/
    ├── java/com/parttimestudent/
    │   ├── StudentManagementApplication.java  ← Main class
    │   ├── config/
    │   │   └── SecurityConfig.java
    │   ├── controller/
    │   │   ├── AuthController.java           ← Register/Login
    │   │   ├── StudentController.java        ← Student APIs
    │   │   ├── PMController.java             ← PM APIs
    │   │   └── AdminController.java          ← Admin APIs
    │   ├── dto/
    │   ├── entity/
    │   ├── repository/
    │   ├── security/
    │   └── service/
    └── resources/
        └── application.properties
```

## 🎯 Use Cases & Examples

### สำหรับ Student
1. ลงทะเบียน → Login
2. Upload รูปตารางเรียน
3. ดูตารางเรียนและเวลาว่าง
4. ดูโปรเจคที่เข้าร่วม

### สำหรับ PM
1. Login → รับโปรเจคจาก Admin
2. เลือกสมาชิกเข้าทีม
3. Update สถานะโปรเจค
4. จัดการทีม

### สำหรับ Admin
1. สร้างโปรเจคใหม่
2. แต่งตั้ง PM
3. ดูภาพรวมโปรเจคทั้งหมด
4. Monitor deadline และ status
5. จัดการ users

## 🔐 Security & Authentication

### Default Credentials
```
Username: admin
Password: admin123
Role: ADMIN
```

⚠️ **สำคัญ**: เปลี่ยน password ทันทีหลัง login ครั้งแรก!

### JWT Token
- Expiration: 24 hours
- Header: `Authorization: Bearer {token}`
- All endpoints require authentication (except `/auth/*`)

## 📊 API Endpoints Quick Reference

| Category | Endpoints | Access |
|----------|-----------|--------|
| Auth | `/auth/register`, `/auth/login` | Public |
| Student | `/student/*` | STUDENT |
| PM | `/pm/*` | PM, ADMIN |
| Admin | `/admin/*` | ADMIN |

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL 15
- **Security**: Spring Security + JWT
- **AI**: Google Gemini API
- **Build**: Maven 3.9
- **Container**: Docker + Docker Compose

## 📋 Requirements

### Minimum Requirements
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Google Gemini API Key

### Recommended
- Docker & Docker Compose
- Postman (for testing)
- 4GB RAM
- 10GB disk space

## 🚦 Getting Started Workflow

### First Time Setup

```bash
# 1. Clone/Download project
cd student-management

# 2. Choose your setup method:

# Option A: Manual Setup
psql -U postgres -d student_management -f database-schema.sql
mvn clean install
mvn spring-boot:run

# Option B: Docker Setup
cp .env.example .env
# Edit .env and add GEMINI_API_KEY
docker-compose up -d

# 3. Test the API
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 📝 Important Notes

1. **Custom ID Generation**: ระบบจะสร้าง ID อัตโนมัติ (TTTP01, TTTP02, ...)
2. **Password Security**: ใช้ BCrypt encryption
3. **JWT Token**: มีอายุ 24 ชั่วโมง
4. **Gemini API**: จำเป็นสำหรับ upload timetable feature
5. **Database**: ใช้ PostgreSQL เท่านั้น

## 🆘 Need Help?

### Documentation
- 📖 [README.md](README.md) - Complete documentation
- 🚀 [QUICK_START.md](QUICK_START.md) - Step-by-step guide
- 🏗️ [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) - Architecture
- 🐳 [DOCKER_SETUP.md](DOCKER_SETUP.md) - Docker guide

### Troubleshooting
ดูในแต่ละเอกสาร มี Troubleshooting section:
- Database connection issues
- Port conflicts
- JWT token problems
- Gemini API errors

### Testing
- Import [postman_collection.json](postman_collection.json)
- ทดสอบทุก API endpoint
- ดู example requests

## 🔄 Update & Maintenance

### Update Application
```bash
# Manual
mvn clean install
mvn spring-boot:run

# Docker
docker-compose up -d --build
```

### Database Backup
```bash
# Manual
pg_dump -U postgres student_management > backup.sql

# Docker
docker exec -t student-management-db pg_dump -U postgres student_management > backup.sql
```

## 📊 Project Statistics

- **Total Files**: 39 files
- **Java Classes**: 31 classes
- **API Endpoints**: 30+ endpoints
- **Database Tables**: 4 tables
- **Documentation**: 5 guides
- **Total Size**: ~15 MB

## 🎯 Next Steps

1. ✅ Read [README.md](README.md) for overview
2. ✅ Follow [QUICK_START.md](QUICK_START.md) to setup
3. ✅ Import [postman_collection.json](postman_collection.json)
4. ✅ Test with default admin account
5. ✅ Create test users and projects
6. ✅ Deploy to production

## 📞 Additional Resources

- **Spring Boot**: https://spring.io/projects/spring-boot
- **PostgreSQL**: https://www.postgresql.org/docs/
- **Google Gemini**: https://ai.google.dev/
- **Docker**: https://docs.docker.com/

---

## 📌 Quick Links

| Document | Description | Size |
|----------|-------------|------|
| [README.md](README.md) | Complete documentation | 11 KB |
| [QUICK_START.md](QUICK_START.md) | Quick start guide | 9 KB |
| [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) | Architecture guide | 7 KB |
| [DOCKER_SETUP.md](DOCKER_SETUP.md) | Docker deployment | 8 KB |
| [SUMMARY.md](SUMMARY.md) | Project summary | 11 KB |
| [database-schema.sql](database-schema.sql) | Database schema | 3 KB |
| [postman_collection.json](postman_collection.json) | API collection | 13 KB |

---

**เริ่มต้นใช้งานเลย!** 🚀

ระบบพร้อมใช้งานครบทุกฟีเจอร์ที่ต้องการ พร้อม documentation ครบถ้วน!