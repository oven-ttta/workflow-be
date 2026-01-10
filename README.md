# Student Part-time Management System API

ระบบจัดการน้องนักเรียน Part-time แบบ RESTful API พัฒนาด้วย Java Spring Boot และ PostgreSQL

## 🚀 Features

- **User Management**
  - ลงทะเบียนนักเรียน Part-time พร้อมสร้าง ID อัตโนมัติ (TTTP01, TTTP02, ...)
  - Login/Authentication ด้วย JWT
  - อัพเดตข้อมูลส่วนตัว
  - จัดการ Role (STUDENT, PM, ADMIN)

- **Timetable Management**
  - อัพโหลดรูปตารางเรียน
  - ใช้ Google Gemini AI แกะข้อมูลจากรูปภาพ
  - บันทึกตารางเรียนและเวลาว่าง
  - ดูตารางเรียนของตัวเอง

- **Project Management**
  - Admin สร้างโปรเจคและแต่งตั้ง PM
  - PM เลือกสมาชิกเข้าโปรเจค
  - อัพเดตสถานะโปรเจค (NOT_STARTED, IN_PROCESS, TEST, REVIEW, DONE, HELP)
  - ตรวจสอบโปรเจคที่ใกล้ Deadline
  - ดูโปรเจคที่ต้องการความช่วยเหลือ

## 📋 Prerequisites

- Java 17 หรือสูงกว่า
- Maven 3.6+
- PostgreSQL 12+
- Google Gemini API Key

## 🛠️ Installation

### 1. Clone Repository

```bash
git clone <repository-url>
cd student-management
```

### 2. Setup Database

สร้าง PostgreSQL database:

```sql
CREATE DATABASE student_management;
```

Run SQL script สำหรับสร้าง tables:

```bash
psql -U postgres -d student_management -f database-schema.sql
```

### 3. Configure Application

แก้ไขไฟล์ `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/student_management
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password

# JWT Secret
jwt.secret=YourSuperSecretKeyForJWTTokenGenerationMustBeLongEnough

# Google Gemini API
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

## 📚 API Endpoints

### Authentication

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "สมชาย",
  "yearLevel": "ปี 3",
  "specialty": "Backend",
  "username": "somchai",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "customId": "TTTP01",
  "username": "somchai",
  "firstName": "สมชาย",
  "role": "STUDENT"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "somchai",
  "password": "password123"
}
```

### Student Endpoints

#### Get Profile
```http
GET /api/student/profile
Authorization: Bearer {token}
```

#### Update Profile
```http
PUT /api/student/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "สมชาย",
  "yearLevel": "ปี 4",
  "specialty": "Backend",
  "username": "somchai",
  "password": "newpassword123"
}
```

#### Upload Timetable
```http
POST /api/student/timetable/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [image file]
```

Response:
```json
{
  "slots": [
    {
      "dayOfWeek": "Monday",
      "startTime": "09:00",
      "endTime": "10:30",
      "subject": "Mathematics",
      "isFree": false
    },
    {
      "dayOfWeek": "Monday",
      "startTime": "10:30",
      "endTime": "12:00",
      "subject": "Free",
      "isFree": true
    }
  ]
}
```

#### Get Timetable
```http
GET /api/student/timetable
Authorization: Bearer {token}
```

#### Get My Projects
```http
GET /api/student/projects
Authorization: Bearer {token}
```

### PM Endpoints

#### Get Managed Projects
```http
GET /api/pm/projects
Authorization: Bearer {token}
```

#### Get Project Details
```http
GET /api/pm/projects/{projectId}
Authorization: Bearer {token}
```

#### Add Member to Project
```http
POST /api/pm/projects/{projectId}/members/{userId}
Authorization: Bearer {token}
```

#### Remove Member from Project
```http
DELETE /api/pm/projects/{projectId}/members/{userId}
Authorization: Bearer {token}
```

#### Update Project Status
```http
PUT /api/pm/projects/{projectId}/status?status=IN_PROCESS
Authorization: Bearer {token}
```

Status values: `NOT_STARTED`, `IN_PROCESS`, `TEST`, `REVIEW`, `DONE`, `HELP`

#### Get Available Students
```http
GET /api/pm/students
Authorization: Bearer {token}
```

#### Get Students by Specialty
```http
GET /api/pm/students/specialty/Backend
Authorization: Bearer {token}
```

### Admin Endpoints

#### Get All Users
```http
GET /api/admin/users
Authorization: Bearer {token}
```

#### Get User by ID
```http
GET /api/admin/users/{userId}
Authorization: Bearer {token}
```

#### Update User
```http
PUT /api/admin/users/{userId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "สมชาย",
  "yearLevel": "ปี 4",
  "specialty": "Backend",
  "username": "somchai",
  "password": "newpassword"
}
```

#### Delete User
```http
DELETE /api/admin/users/{userId}
Authorization: Bearer {token}
```

#### Update User Role
```http
PUT /api/admin/users/{userId}/role?role=PM
Authorization: Bearer {token}
```

Role values: `STUDENT`, `PM`, `ADMIN`

#### Create Project
```http
POST /api/admin/projects
Authorization: Bearer {token}
Content-Type: application/json

{
  "projectName": "E-Commerce Website",
  "difficultyLevel": 4,
  "durationDays": 30,
  "pmUserId": 2,
  "startDate": "2025-01-01"
}
```

#### Get All Projects
```http
GET /api/admin/projects
Authorization: Bearer {token}
```

#### Get Project by ID
```http
GET /api/admin/projects/{projectId}
Authorization: Bearer {token}
```

#### Update Project
```http
PUT /api/admin/projects/{projectId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "projectName": "E-Commerce Website v2",
  "difficultyLevel": 5,
  "durationDays": 45,
  "pmUserId": 2,
  "startDate": "2025-01-01"
}
```

#### Delete Project
```http
DELETE /api/admin/projects/{projectId}
Authorization: Bearer {token}
```

#### Update Project Status
```http
PUT /api/admin/projects/{projectId}/status?status=IN_PROCESS
Authorization: Bearer {token}
```

#### Add Member to Project
```http
POST /api/admin/projects/{projectId}/members/{userId}
Authorization: Bearer {token}
```

#### Remove Member from Project
```http
DELETE /api/admin/projects/{projectId}/members/{userId}
Authorization: Bearer {token}
```

#### Get Project Status Overview
```http
GET /api/admin/projects/status/overview
Authorization: Bearer {token}
```

Response:
```json
{
  "allProjects": [...],
  "projectsDueSoon": [...],
  "overdueProjects": [...],
  "projectsNeedingHelp": [...]
}
```

#### Get Projects by Status
```http
GET /api/admin/projects/status/IN_PROCESS
Authorization: Bearer {token}
```

#### Get Projects Due Soon
```http
GET /api/admin/projects/due-soon?days=7
Authorization: Bearer {token}
```

#### Get Overdue Projects
```http
GET /api/admin/projects/overdue
Authorization: Bearer {token}
```

#### Get Projects Needing Help
```http
GET /api/admin/projects/help
Authorization: Bearer {token}
```

## 🗄️ Database Schema

### Users Table
- `id`: Primary key
- `custom_id`: รหัสนักเรียน (TTTP01, TTTP02, ...)
- `first_name`: ชื่อจริง
- `year_level`: ชั้นปี
- `specialty`: ความถนัด (Frontend, Backend, ML Engineer, UX/UI, QA, DevOps)
- `username`: Username
- `password`: Password (encrypted)
- `role`: บทบาท (STUDENT, PM, ADMIN)
- `is_active`: สถานะการใช้งาน
- `created_at`, `updated_at`: Timestamps

### Timetable Slots Table
- `id`: Primary key
- `user_id`: Foreign key to Users
- `day_of_week`: วันในสัปดาห์
- `start_time`: เวลาเริ่ม
- `end_time`: เวลาจบ
- `subject`: วิชา
- `is_free`: ว่างหรือไม่
- `created_at`: Timestamp

### Projects Table
- `id`: Primary key
- `project_name`: ชื่อโปรเจค
- `difficulty_level`: ระดับความยาก (1-5)
- `duration_days`: ระยะเวลา (วัน)
- `pm_user_id`: Foreign key to Users (PM)
- `status`: สถานะโปรเจค
- `start_date`: วันเริ่มต้น
- `deadline`: วันส่ง
- `created_by`: Foreign key to Users (Admin)
- `created_at`, `updated_at`: Timestamps

### Project Members Table
- `id`: Primary key
- `project_id`: Foreign key to Projects
- `user_id`: Foreign key to Users
- `assigned_at`: Timestamp

## 🔐 Security

- JWT-based authentication
- Password encryption ด้วย BCrypt
- Role-based access control (RBAC)
  - STUDENT: เข้าถึงข้อมูลส่วนตัวและโปรเจคของตัวเอง
  - PM: จัดการโปรเจคที่ตัวเองเป็น PM
  - ADMIN: เข้าถึงและแก้ไขทุกอย่าง

## 🤖 Google Gemini Integration

ระบบใช้ Google Gemini AI สำหรับ:
- แกะข้อมูลจากรูปตารางเรียน
- แปลงเป็น JSON format
- ระบุวัน เวลา วิชา และช่วงเวลาว่าง

## 📝 Project Status

สถานะโปรเจคที่มี:
- `NOT_STARTED`: ยังไม่เริ่ม
- `IN_PROCESS`: กำลังดำเนินการ
- `TEST`: กำลัง Test
- `REVIEW`: กำลัง Review
- `DONE`: เสร็จสมบูรณ์
- `HELP`: ต้องการความช่วยเหลือ!!!

## 👥 User Roles

- **STUDENT**: นักเรียน Part-time
- **PM**: Project Manager
- **ADMIN**: ผู้ดูแลระบบ

## 🎯 Specialty Types

- Frontend
- Backend
- ML Engineer
- UX/UI
- QA
- DevOps

## 📦 Default Admin Account

- Username: `admin`
- Password: `admin123`
- Custom ID: `ADMIN001`
- Role: `ADMIN`

⚠️ **แนะนำให้เปลี่ยน password หลังจาก login ครั้งแรก**

## 🐛 Troubleshooting

### Database Connection Error
- ตรวจสอบว่า PostgreSQL กำลังรันอยู่
- ตรวจสอบ username/password ใน application.properties
- ตรวจสอบว่าสร้าง database แล้ว

### JWT Token Expired
- Token มีอายุ 24 ชั่วโมง
- Login ใหม่เพื่อรับ token ใหม่

### Gemini API Error
- ตรวจสอบ API key
- ตรวจสอบ quota และ billing

## 📄 License

This project is licensed under the MIT License.