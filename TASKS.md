## วัตถุประสงค์

ไฟล์นี้อธิบาย **Task / หน้าที่ของแต่ละโฟลเดอร์และไฟล์หลัก** ในโปรเจกต์ `student-management` เพื่อให้เห็นภาพรวมว่าส่วนไหนรับผิดชอบงานอะไรบ้าง

---

## Root ของโปรเจกต์

- **pom.xml**: กำหนด dependency, เวอร์ชัน Java, plugin และการ build ทั้งหมดของ Spring Boot project
- **README.md**: รวม requirement, วิธีติดตั้ง, วิธีรันระบบ และตัวอย่างการเรียก API หลัก ๆ
- **PROJECT_STRUCTURE.md**: อธิบายโครงสร้างโฟลเดอร์/เลเยอร์ และ flow หลักของระบบ (Entity, Service, Controller, Security, Gemini)
- **SUMMARY.md**: สรุปภาพรวมเนื้อหาเอกสารอื่น ๆ ในโปรเจกต์
- **INDEX.md**: ทำหน้าที่เหมือนสารบัญลิงก์ไปยังไฟล์เอกสารสำคัญ
- **QUICK_START.md**: Task สำหรับการเริ่มต้นใช้งานอย่างรวดเร็ว (ตั้งค่า, build, run, test)
- **OOP_CONCEPTS.md**: อธิบายแนวคิด OOP ที่ใช้ในโปรเจกต์ (encapsulation, inheritance, polymorphism ฯลฯ)
- **DOCKER_SETUP.md**: Task ที่เกี่ยวข้องกับการรันระบบผ่าน Docker / Docker Compose (database, backend)
- **database-schema.sql**: สคริปต์สร้างตารางใน PostgreSQL ให้ตรงกับ Entity (users, projects, timetable_slots, project_members)
- **postman_collection.json**: รวมชุด request สำหรับทดสอบ API ทุกกลุ่ม (Auth, Student, PM, Admin) ผ่าน Postman
- **.env**: เก็บค่า configuration ที่เป็น secret เช่น DB password, Gemini API key (ไม่ควร commit ขึ้น repo สาธารณะ)
- **.gitignore**: กำหนดไฟล์/โฟลเดอร์ที่ไม่ต้องการให้ Git ติดตาม (เช่น `target/`, log, ไฟล์ temp)

---

## โครงสร้างโค้ดหลัก `src/main/java/com/parttimestudent`

### ระดับ Application

- **StudentManagementApplication.java**  
  - Task: เป็น entry point ของ Spring Boot (`main` method)  
  - รับผิดชอบการ bootstrap แอป, scan component ต่าง ๆ และเริ่ม web server

### โฟลเดอร์ `config/`

- **SecurityConfig.java**  
  - กำหนด Spring Security filter chain และตั้งค่า HTTP security  
  - Map URL แต่ละกลุ่มเข้ากับ Role (STUDENT, PM, ADMIN)  
  - ผูก `JwtAuthenticationFilter` เข้าไปใน filter chain  
  - ตั้งค่า CORS / CSRF / session policy ตามต้องการ

### โฟลเดอร์ `controller/`

- **AuthController.java** (`/auth`)  
  - Task: จัดการการสมัครสมาชิกและเข้าสู่ระบบ  
  - รับ `RegisterRequest`, `LoginRequest`  
  - เรียก `UserService` เพื่อ register/login แล้วตอบกลับเป็น `AuthResponse` (JWT token + ข้อมูลผู้ใช้)

- **StudentController.java** (`/student`)  
  - Task: API สำหรับฝั่ง STUDENT  
  - `GET /student/profile` / `PUT /student/profile`: ดูและอัปเดตโปรไฟล์ของตัวเอง (ทำงานผ่าน `UserService`)  
  - `POST /student/timetable/upload`: อัปโหลดรูปตารางเรียน (delegate ไปที่ `TimetableService`)  
  - `GET /student/timetable`: ดึงตารางเรียน/เวลาว่างของตัวเอง  
  - `GET /student/projects`: ดึงรายการโปรเจกต์ที่ตัวเองเป็นสมาชิก (ผ่าน `ProjectService`)

- **PMController.java** (`/pm`)  
  - Task: API สำหรับ Project Manager  
  - ดึงโปรเจกต์ที่ตัวเองเป็น PM (`getMyManagedProjects`)  
  - ดูรายละเอียดโปรเจกต์รายตัว  
  - เพิ่ม/ลบสมาชิกในโปรเจกต์ (ตรวจสอบก่อนว่า user ปัจจุบันเป็น PM ของโปรเจกต์นั้นจริง)  
  - อัปเดตสถานะโปรเจกต์ (NOT_STARTED, IN_PROCESS, TEST, REVIEW, DONE, HELP)  
  - ดึงรายชื่อนักเรียนที่ว่าง, ดึงตาม specialty และดูตารางเรียนของนักเรียนแต่ละคน

- **AdminController.java** (`/admin`)  
  - Task: API สำหรับ ADMIN  
  - กลุ่ม **User Management**:  
    - ดึง user ทั้งหมด (รองรับ sort ตามชื่อ)  
    - ดึง user ตาม id, แก้ไขข้อมูล, ลบ user  
    - เปลี่ยน role ของ user (STUDENT/PM/ADMIN)  
  - กลุ่ม **Project Management**:  
    - สร้างโปรเจกต์ใหม่จาก `ProjectRequest` (ผูกกับ admin ที่สร้าง)  
    - ดึง/แก้ไข/ลบโปรเจกต์  
    - อัปเดตสถานะโปรเจกต์  
    - จัดการสมาชิกในโปรเจกต์ (add/remove member)  
  - กลุ่ม **Project Status Overview**:  
    - รวมข้อมูลภาพรวมโปรเจกต์ (ทั้งหมด, ใกล้ถึง deadline, overdue, ขอความช่วยเหลือ) ผ่าน inner class `ProjectStatusOverview`  
    - ดึงโปรเจกต์ตามสถานะ, โปรเจกต์ที่กำลังจะถึงกำหนด, โปรเจกต์ overdue และโปรเจกต์ที่มีสถานะ HELP  
  - กลุ่ม **Timetable Management (ฝั่ง Admin)**:  
    - ดูตารางเรียนของ user คนใดก็ได้ (ใช้ `TimetableService`)

- **TimetableController.java** (`/student/timetable` - legacy / example)  
  - Task: ตัวอย่าง controller สำหรับอัปโหลดไฟล์ตารางเรียนขึ้น MinIO โดยตรง  
  - ใช้ `MinioService` เพื่ออัปโหลดไฟล์, generate presigned URL และรับ notification หลังอัปโหลดเสร็จ  
  - ส่วน parsing timetable จากรูปเป็น slot ยังเป็น placeholder (คอมเมนต์ไว้)

### โฟลเดอร์ `dto/` (Data Transfer Objects)

- **RegisterRequest.java**: รับข้อมูลสมัครสมาชิก (ชื่อ, ปีการศึกษา, specialty, username, password ฯลฯ)
- **LoginRequest.java**: รับ username/password ตอน login
- **AuthResponse.java**: ส่ง token + ข้อมูลผู้ใช้กลับไปหลัง register/login
- **TimetableResponse.java**: ใช้ส่งข้อมูล slot ตารางเรียน/เวลาว่างกลับไปให้ client
- **ProjectRequest.java**: ข้อมูลที่ใช้สร้าง/อัปเดตโปรเจกต์ (ชื่อโปรเจกต์, ความยาก, จำนวนวัน, PM, วันที่เริ่ม ฯลฯ)
- **ProjectResponse.java**: รูปแบบข้อมูลโปรเจกต์ที่ส่งกลับให้ client (status, deadline, สมาชิก ฯลฯ)

Task รวมของโฟลเดอร์นี้: แยก model ภายในระบบออกจาก payload ที่เข้า/ออกผ่าน API เพื่อความปลอดภัยและความยืดหยุ่น

### โฟลเดอร์ `entity/` (JPA Entities)

- **User.java**  
  - แทนตาราง `users`  
  - เก็บข้อมูลผู้ใช้, role (STUDENT/PM/ADMIN), specialty, สถานะการใช้งาน, timestamp ฯลฯ  
  - มี enum `UserRole` สำหรับกำหนดสิทธิ์

- **TimetableSlot.java**  
  - แทนตาราง `timetable_slots`  
  - เก็บวัน, เวลาเริ่ม/จบ, วิชา และ flag `isFree` เพื่อบอกช่วงที่ว่าง  
  - ผูกสัมพันธ์กับ `User`

- **Project.java**  
  - แทนตาราง `projects`  
  - เก็บข้อมูลโปรเจกต์ (ชื่อ, ระดับความยาก, duration, PM, status, startDate, deadline, createdBy ฯลฯ)  
  - ใช้ enum `ProjectStatus` แทนสถานะโปรเจกต์

- **ProjectMember.java**  
  - แทนตาราง `project_members`  
  - เก็บความสัมพันธ์ N-M ระหว่าง `User` กับ `Project` และเวลา assign

Task รวมของโฟลเดอร์นี้: กำหนดโครงสร้างข้อมูลที่ map กับ database และใช้ร่วมกับ JPA/Hibernate

### โฟลเดอร์ `repository/` (Data Access Layer)

- **UserRepository.java**: interface สำหรับค้นหา/บันทึกผู้ใช้, มี method พิเศษสำหรับค้นหาตาม role, specialty, username ฯลฯ
- **TimetableSlotRepository.java**: จัดการการอ่าน/เขียนตารางเรียนของผู้ใช้
- **ProjectRepository.java**: ดึงโปรเจกต์ตามสถานะ, deadline, PM, admin ฯลฯ
- **ProjectMemberRepository.java**: จัดการสมาชิกโปรเจกต์ (ค้นหาสมาชิกในโปรเจกต์, ลบสมาชิก ฯลฯ)

Task รวมของโฟลเดอร์นี้: เป็นชั้นกลางระหว่าง Service กับ database (ใช้ Spring Data JPA)

### โฟลเดอร์ `security/`

- **JwtAuthenticationFilter.java**  
  - ดึง JWT token จาก `Authorization` header  
  - ตรวจสอบความถูกต้องของ token ผ่าน `JwtService`  
  - สร้าง `Authentication` object และใส่เข้าไปใน SecurityContext ถ้า token valid

- **CustomUserDetailsService.java**  
  - โหลดข้อมูลผู้ใช้จาก `UserRepository` ตาม username  
  - แปลงเป็น `UserDetails` ให้ Spring Security ใช้ตอน authenticate

Task รวมของโฟลเดอร์นี้: จัดการงาน authentication/authorization ระดับ low-level ร่วมกับ Spring Security

### โฟลเดอร์ `service/`

- **UserService.java**  
  - จัดการ business logic เกี่ยวกับผู้ใช้  
  - register ผู้ใช้ใหม่ (สร้าง customId, เข้ารหัสรหัสผ่าน, ตั้ง role)  
  - login (ตรวจรหัสผ่าน, สร้าง JWT ผ่าน `JwtService`)  
  - อัปเดต/ลบผู้ใช้, เปลี่ยน role, ค้นหาผู้ใช้ตามเงื่อนไขต่าง ๆ

- **TimetableService.java**  
  - รับไฟล์ตารางเรียน/ข้อมูลจาก Gemini  
  - ประมวลผลและแปลงเป็น `TimetableSlot` หลายรายการ  
  - บันทึกลง database และสร้าง `TimetableResponse` ให้ controller ใช้ตอบกลับ  
  - ใช้ดึงตารางเรียนของ user ทั้งจากมุมมอง student, PM, admin

- **ProjectService.java**  
  - สร้าง/อัปเดต/ลบโปรเจกต์  
  - จัดการสมาชิกโปรเจกต์ (add/remove)  
  - ปรับสถานะโปรเจกต์ตาม workflow  
  - คำนวณโปรเจกต์ที่ใกล้ deadline, overdue, ต้องการความช่วยเหลือ  
  - แปลง entity เป็น `ProjectResponse` ผ่าน method `convertToResponse`

- **JwtService.java**  
  - สร้าง JWT token จากข้อมูลผู้ใช้  
  - แยก claim, ตรวจสอบวันหมดอายุ และ validate token  
  - ใช้ร่วมกับ `JwtAuthenticationFilter` เพื่อยืนยันตัวตนทุก request

- **GeminiService.java**  
  - เรียก Google Gemini API ผ่าน WebClient/HTTP client  
  - แปลงรูปตารางเรียนเป็น base64, ส่ง prompt, รับ JSON ตอบกลับ  
  - Mapping JSON → โครงสร้าง slot เพื่อนำไปใช้ใน `TimetableService`

- **MinioService.java**  
  - จัดการเชื่อมต่อ MinIO (endpoint, credential, bucket)  
  - อัปโหลดไฟล์ตารางเรียนไปเก็บใน object storage  
  - generate presigned URL ให้ frontend อัปโหลดไฟล์ตรง  
  - (ใช้โดย `TimetableController`)

Task รวมของโฟลเดอร์นี้: รวม business logic หลักทั้งหมดของระบบ แยกออกจาก controller

---

## โฟลเดอร์ `src/main/resources`

- **application.properties**  
  - เก็บ configuration หลักของแอป เช่น  
    - การเชื่อมต่อฐานข้อมูล PostgreSQL  
    - ค่า `jwt.secret` สำหรับสร้าง/ตรวจ JWT  
    - ค่า `gemini.api.key` สำหรับเรียก Google Gemini  
    - setting อื่น ๆ ของ Spring Boot (logging, port ฯลฯ)

Task ของโฟลเดอร์นี้: รวมไฟล์ configuration ที่ใช้ตอน runtime

---

## โฟลเดอร์ `.github/java-upgrade/`

- **plan.md, progress.md, summary.md** และไฟล์ log ต่าง ๆ  
  - เก็บแผน, log และผลการรัน automation ที่ใช้ช่วย upgrade Java / dependency ของโปรเจกต์  
  - ไม่กระทบ logic หลักของระบบ แต่ใช้ติดตามงาน maintenance

---

## สรุปภาพรวม Task ตามเลเยอร์

- **Controller Layer**: รับ/ตอบ HTTP request, map endpoint → service, ตรวจสิทธิ์เบื้องต้นตาม role  
- **Service Layer**: ธุรกิจหลักทั้งหมด (สมัคร/ล็อกอิน, จัดการ user, timetable, project, สถานะ, สถิติ)  
- **Repository Layer**: ทำงานกับ PostgreSQL ผ่าน JPA, ซ่อนรายละเอียดการ query  
- **Entity Layer**: กำหนดโครงสร้างข้อมูลที่ผูกกับตารางในฐานข้อมูล  
- **DTO Layer**: รูปแบบข้อมูลที่ใช้สื่อสารกับ client ทาง API  
- **Security Layer**: จัดการ JWT, การยืนยันตัวตน, และ RBAC  
- **Config/Resources/Docs**: ตั้งค่าระบบ, การ deploy, การใช้งาน และเอกสารอธิบายโปรเจกต์

