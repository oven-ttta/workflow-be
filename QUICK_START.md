# 🚀 Quick Start Guide - Student Part-time Management System

## ขั้นตอนการติดตั้งและรัน

### 1. ติดตั้ง Prerequisites

```bash
# ตรวจสอบ Java version (ต้อง 17+)
java -version

# ตรวจสอบ Maven
mvn -version

# ตรวจสอบ PostgreSQL
psql --version
```

### 2. Setup Database

```bash
# Login to PostgreSQL
sudo -u postgres psql

# สร้าง database
CREATE DATABASE student_management;

# ออกจาก psql
\q

# Run schema script
psql -U postgres -d student_management -f database-schema.sql
```

### 3. Configure Application

แก้ไขไฟล์ `src/main/resources/application.properties`:

```properties
# แก้ไข username และ password ของ PostgreSQL
spring.datasource.username=postgres
spring.datasource.password=your_password

# ใส่ Google Gemini API Key
gemini.api.key=your_gemini_api_key_here
```

### 4. Build และ Run

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run
```

Application จะรันที่ `http://localhost:8080/api`

### 5. ทดสอบ API

#### 5.1 Login ด้วย Default Admin

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "customId": "ADMIN001",
  "username": "admin",
  "firstName": "Admin",
  "role": "ADMIN"
}
```

#### 5.2 Register นักเรียนใหม่

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "สมชาย",
    "yearLevel": "ปี 3",
    "specialty": "Backend",
    "username": "somchai",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 2,
  "customId": "TTTP01",
  "username": "somchai",
  "firstName": "สมชาย",
  "role": "STUDENT"
}
```

#### 5.3 สร้างโปรเจค (Admin)

```bash
# ใช้ token ของ admin
curl -X POST http://localhost:8080/api/admin/projects \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "projectName": "E-Commerce Website",
    "difficultyLevel": 4,
    "durationDays": 30,
    "pmUserId": 2,
    "startDate": "2025-01-01"
  }'
```

#### 5.4 Upload Timetable (Student)

```bash
curl -X POST http://localhost:8080/api/student/timetable/upload \
  -H "Authorization: Bearer YOUR_STUDENT_TOKEN" \
  -F "file=@/path/to/timetable.jpg"
```

### 6. ใช้ Postman

1. Import `postman_collection.json` ลงใน Postman
2. Set variable `baseUrl` = `http://localhost:8080/api`
3. Login และคัดลอก token
4. Set variable `token` = token ที่ได้
5. ทดสอบ API ต่างๆ ได้เลย

## 📝 ตัวอย่างการใช้งาน

### Scenario 1: Admin สร้างโปรเจคและแต่งตั้ง PM

```bash
# 1. Admin login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.token')

# 2. Register นักเรียนที่จะเป็น PM
PM_ID=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "สมหญิง",
    "yearLevel": "ปี 4",
    "specialty": "Frontend",
    "username": "somying",
    "password": "password123"
  }' | jq -r '.id')

# 3. Admin สร้างโปรเจคและแต่งตั้ง PM
curl -X POST http://localhost:8080/api/admin/projects \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"projectName\": \"Mobile App Development\",
    \"difficultyLevel\": 5,
    \"durationDays\": 45,
    \"pmUserId\": $PM_ID,
    \"startDate\": \"2025-02-01\"
  }"
```

### Scenario 2: PM เลือกสมาชิกเข้าโปรเจค

```bash
# 1. PM login
PM_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"somying","password":"password123"}' \
  | jq -r '.token')

# 2. ดูโปรเจคที่ตัวเองเป็น PM
curl -X GET http://localhost:8080/api/pm/projects \
  -H "Authorization: Bearer $PM_TOKEN"

# 3. ดูนักเรียนที่มี specialty เป็น Backend
curl -X GET http://localhost:8080/api/pm/students/specialty/Backend \
  -H "Authorization: Bearer $PM_TOKEN"

# 4. เพิ่มสมาชิกเข้าโปรเจค
curl -X POST http://localhost:8080/api/pm/projects/1/members/2 \
  -H "Authorization: Bearer $PM_TOKEN"
```

### Scenario 3: Student อัพโหลดตารางเรียน

```bash
# 1. Student login
STUDENT_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"somchai","password":"password123"}' \
  | jq -r '.token')

# 2. Upload timetable image
curl -X POST http://localhost:8080/api/student/timetable/upload \
  -H "Authorization: Bearer $STUDENT_TOKEN" \
  -F "file=@timetable.jpg"

# 3. ดูตารางเรียนของตัวเอง
curl -X GET http://localhost:8080/api/student/timetable \
  -H "Authorization: Bearer $STUDENT_TOKEN"
```

## 🔧 Troubleshooting

### ปัญหา: Database connection failed

```bash
# ตรวจสอบว่า PostgreSQL กำลังรันอยู่
sudo systemctl status postgresql

# Start PostgreSQL
sudo systemctl start postgresql
```

### ปัญหา: Port 8080 ถูกใช้งานแล้ว

แก้ไขใน `application.properties`:
```properties
server.port=8081
```

### ปัญหา: JWT Token expired

Token มีอายุ 24 ชั่วโมง ให้ login ใหม่เพื่อรับ token ใหม่

### ปัญหา: Gemini API error

1. ตรวจสอบ API key ใน `application.properties`
2. ตรวจสอบว่ามี quota เหลืออยู่
3. ตรวจสอบว่าเปิดใช้งาน API แล้ว

## 📊 ตัวอย่าง Response

### Get All Users (Admin)

```json
[
  {
    "id": 1,
    "customId": "ADMIN001",
    "firstName": "Admin",
    "yearLevel": "N/A",
    "specialty": "N/A",
    "username": "admin",
    "role": "ADMIN",
    "isActive": true
  },
  {
    "id": 2,
    "customId": "TTTP01",
    "firstName": "สมชาย",
    "yearLevel": "ปี 3",
    "specialty": "Backend",
    "username": "somchai",
    "role": "STUDENT",
    "isActive": true
  }
]
```

### Get Project Status Overview (Admin)

```json
{
  "allProjects": [...],
  "projectsDueSoon": [
    {
      "id": 1,
      "projectName": "E-Commerce Website",
      "difficultyLevel": 4,
      "durationDays": 30,
      "status": "IN_PROCESS",
      "deadline": "2025-01-31",
      "pmUser": {
        "id": 2,
        "customId": "TTTP01",
        "firstName": "สมหญิง",
        "username": "somying"
      },
      "members": [
        {
          "id": 3,
          "customId": "TTTP02",
          "firstName": "สมชาย",
          "specialty": "Backend"
        }
      ]
    }
  ],
  "overdueProjects": [],
  "projectsNeedingHelp": []
}
```

## 🎯 Next Steps

1. เปลี่ยน password ของ admin account
2. สร้าง user accounts สำหรับทีม
3. สร้างโปรเจคและแต่งตั้ง PM
4. อัพโหลดตารางเรียนของแต่ละคน
5. PM เลือกสมาชิกเข้าโปรเจค
6. เริ่มทำงานและอัพเดตสถานะโปรเจค

## 📚 Additional Resources

- [README.md](README.md) - Full documentation
- [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) - Project architecture
- [database-schema.sql](database-schema.sql) - Database schema
- [postman_collection.json](postman_collection.json) - API testing collection

## ❓ FAQ

**Q: ต้องใช้ API key อะไรบ้าง?**
A: ต้องมี Google Gemini API key สำหรับฟีเจอร์ upload timetable

**Q: Custom ID สร้างอย่างไร?**
A: ระบบจะสร้างอัตโนมัติตามลำดับ TTTP01, TTTP02, ... เมื่อ register

**Q: PM สามารถเป็น STUDENT ได้ไหม?**
A: ได้ เมื่อ Admin แต่งตั้งให้เป็น PM ระบบจะเปลี่ยน role อัตโนมัติ

**Q: สามารถดูตารางเรียนของคนอื่นได้ไหม?**
A: ไม่ได้ Student ดูได้แค่ของตัวเอง, PM และ Admin ต้องใช้ API อื่น

**Q: โปรเจคที่สถานะ HELP คืออะไร?**
A: เป็นโปรเจคที่ต้องการความช่วยเหลือจาก Admin หรือทีมอื่น