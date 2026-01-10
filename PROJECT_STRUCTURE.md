# Student Part-time Management System - Project Structure

## 📁 โครงสร้างโปรเจค

```
student-management/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── parttimestudent/
│       │           ├── StudentManagementApplication.java  (Main Application)
│       │           │
│       │           ├── config/
│       │           │   └── SecurityConfig.java  (Security Configuration)
│       │           │
│       │           ├── controller/
│       │           │   ├── AuthController.java      (Register/Login)
│       │           │   ├── StudentController.java   (Student APIs)
│       │           │   ├── PMController.java        (PM Management APIs)
│       │           │   └── AdminController.java     (Admin Management APIs)
│       │           │
│       │           ├── dto/
│       │           │   ├── RegisterRequest.java
│       │           │   ├── LoginRequest.java
│       │           │   ├── AuthResponse.java
│       │           │   ├── TimetableResponse.java
│       │           │   ├── ProjectRequest.java
│       │           │   └── ProjectResponse.java
│       │           │
│       │           ├── entity/
│       │           │   ├── User.java                (User Entity)
│       │           │   ├── TimetableSlot.java       (Timetable Entity)
│       │           │   ├── Project.java             (Project Entity)
│       │           │   └── ProjectMember.java       (Project Member Entity)
│       │           │
│       │           ├── repository/
│       │           │   ├── UserRepository.java
│       │           │   ├── TimetableSlotRepository.java
│       │           │   ├── ProjectRepository.java
│       │           │   └── ProjectMemberRepository.java
│       │           │
│       │           ├── security/
│       │           │   ├── JwtAuthenticationFilter.java
│       │           │   └── CustomUserDetailsService.java
│       │           │
│       │           └── service/
│       │               ├── JwtService.java           (JWT Token Service)
│       │               ├── GeminiService.java        (Google Gemini AI)
│       │               ├── UserService.java          (User Management)
│       │               ├── TimetableService.java     (Timetable Management)
│       │               └── ProjectService.java       (Project Management)
│       │
│       └── resources/
│           └── application.properties  (Configuration)
│
├── pom.xml                    (Maven Dependencies)
├── database-schema.sql        (Database Schema)
├── README.md                  (Documentation)
└── postman_collection.json    (API Testing Collection)
```

## 🔧 คำอธิบายแต่ละส่วน

### 1. Entity Layer (JPA Entities)
- **User**: เก็บข้อมูลนักเรียน Part-time, PM, และ Admin
- **TimetableSlot**: เก็บตารางเรียนของแต่ละคน
- **Project**: เก็บข้อมูลโปรเจค
- **ProjectMember**: เก็บความสัมพันธ์ระหว่างโปรเจคและสมาชิก

### 2. Repository Layer
- Interface สำหรับเข้าถึง database ผ่าน Spring Data JPA
- มี query methods สำหรับการค้นหาข้อมูล

### 3. Service Layer
- **JwtService**: จัดการ JWT Token (generate, validate)
- **GeminiService**: เชื่อมต่อ Google Gemini AI สำหรับแกะข้อมูลจากรูป
- **UserService**: จัดการ User (register, login, update, delete)
- **TimetableService**: จัดการตารางเรียน (upload, extract, save)
- **ProjectService**: จัดการโปรเจค (create, update, add members)

### 4. Controller Layer
- **AuthController**: API สำหรับ Register และ Login
- **StudentController**: API สำหรับนักเรียน
- **PMController**: API สำหรับ Project Manager
- **AdminController**: API สำหรับ Admin

### 5. Security Layer
- **SecurityConfig**: กำหนด security rules และ RBAC
- **JwtAuthenticationFilter**: Filter สำหรับตรวจสอบ JWT Token
- **CustomUserDetailsService**: Load user details สำหรับ authentication

### 6. DTO Layer
- Data Transfer Objects สำหรับรับส่งข้อมูลระหว่าง API

## 🗄️ Database Relations

```
users (1) ─────< (M) timetable_slots
  │
  │ (PM)
  ├─< (1) projects (M) >─── project_members (M) >─── (1) users
  │
  │ (Created By)
  └─< (1) projects
```

## 🔐 Security Flow

1. User login → รับ JWT Token
2. ทุก API request ต้องส่ง Token ใน Authorization header
3. JwtAuthenticationFilter ตรวจสอบ Token
4. Security Config ตรวจสอบสิทธิ์ตาม Role

## 🤖 Gemini AI Integration Flow

1. User อัพโหลดรูปตารางเรียน
2. GeminiService แปลงรูปเป็น base64
3. ส่งไปยัง Google Gemini API พร้อม prompt
4. Gemini วิเคราะห์และส่งกลับเป็น JSON
5. บันทึกข้อมูลลง database

## 📊 API Authorization Matrix

| Endpoint | STUDENT | PM | ADMIN |
|----------|---------|-------|-------|
| /auth/* | ✅ | ✅ | ✅ |
| /student/* | ✅ | ❌ | ❌ |
| /pm/* | ❌ | ✅ | ✅ |
| /admin/* | ❌ | ❌ | ✅ |

## 🚀 การใช้งาน

1. **Setup Database**: Run `database-schema.sql`
2. **Configure**: แก้ไข `application.properties`
3. **Build**: `mvn clean install`
4. **Run**: `mvn spring-boot:run`
5. **Test**: Import `postman_collection.json` ใน Postman

## 📝 Important Notes

- Default Admin: username=`admin`, password=`admin123`
- JWT Token มีอายุ 24 ชั่วโมง
- Password ถูก encrypt ด้วย BCrypt
- Custom ID จะถูกสร้างอัตโนมัติ (TTTP01, TTTP02, ...)
- ต้องมี Google Gemini API Key เพื่อใช้ฟีเจอร์ upload timetable

## 🔄 Typical User Flow

### Student Flow:
1. Register → Login → รับ Token
2. Upload Timetable Image
3. View My Profile & Timetable
4. View My Projects

### PM Flow:
1. Login → รับ Token
2. View Managed Projects
3. Add/Remove Members
4. Update Project Status

### Admin Flow:
1. Login → รับ Token
2. Create Project & Assign PM
3. Manage All Users
4. View Project Overview
5. Check Projects Due Soon / Overdue / Need Help

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **AI**: Google Gemini API
- **Build Tool**: Maven
- **ORM**: Hibernate/JPA