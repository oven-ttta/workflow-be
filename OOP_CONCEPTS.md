# 📚 แนวคิด OOP ที่ใช้ในระบบ Student Part-time Management

เอกสารนี้อธิบายการประยุกต์ใช้แนวคิด Object-Oriented Programming (OOP) และเทคนิคการจัดการข้อมูลต่างๆ ที่ใช้ในโปรเจกต์นี้

---

## 📖 สารบัญ
1. [Inheritance (การสืบทอด)](#1-inheritance-การสืบทอด)
2. [Polymorphism (พหุสัณฐาน)](#2-polymorphism-พหุสัณฐาน)
3. [Aggregation & Composition](#3-aggregation--composition)
4. [File Input (การนำเข้าไฟล์)](#4-file-input-การนำเข้าไฟล์)
5. [Data Sorting (การเรียงลำดับข้อมูล)](#5-data-sorting-การเรียงลำดับข้อมูล)
6. [Data Searching (การค้นหาข้อมูล)](#6-data-searching-การค้นหาข้อมูล)

---

## 1. Inheritance (การสืบทอด)

### 📝 คำอธิบาย
Inheritance คือการที่ class หนึ่งสามารถสืบทอดคุณสมบัติและพฤติกรรมจาก class อื่นได้ ทำให้สามารถนำโค้ดกลับมาใช้ใหม่ได้ (Code Reusability)

### 💻 ตัวอย่างการใช้งาน

#### 1.1 JwtAuthenticationFilter extends OncePerRequestFilter
```java
// ไฟล์: src/main/java/com/parttimestudent/security/JwtAuthenticationFilter.java
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {
        // JWT validation logic
        String token = extractToken(request);
        if (token != null && jwtService.validateToken(token)) {
            // Authenticate user
        }
        filterChain.doFilter(request, response);
    }
}
```

**คำอธิบาย:**
- `JwtAuthenticationFilter` สืบทอดจาก `OncePerRequestFilter` ของ Spring Security
- Override method `doFilterInternal()` เพื่อตรวจสอบ JWT token ทุกครั้งที่มี request เข้ามา

#### 1.2 CustomUserDetailsService implements UserDetailsService
```java
// ไฟล์: src/main/java/com/parttimestudent/security/CustomUserDetailsService.java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }
}
```

**คำอธิบาย:**
- Implement `UserDetailsService` interface ของ Spring Security
- Override method `loadUserByUsername()` เพื่อโหลดข้อมูล user จาก database

#### 1.3 Repository Inheritance
```java
// ไฟล์: src/main/java/com/parttimestudent/repository/UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(User.UserRole role);
    // สืบทอด methods: save(), findById(), findAll(), delete() จาก JpaRepository
}
```

**คำอธิบาย:**
- สืบทอดจาก `JpaRepository<User, Long>`
- ได้ CRUD methods พื้นฐานทั้งหมดโดยอัตโนมัติ
- สามารถเพิ่ม custom query methods ได้

### ✅ ประโยชน์
- **Code Reusability:** ไม่ต้องเขียนโค้ดซ้ำ
- **Maintainability:** แก้ไขที่ parent class จะส่งผลกับ child class ทั้งหมด
- **Extensibility:** สามารถเพิ่มฟีเจอร์ใหม่โดยไม่กระทบโค้ดเดิม

---

## 2. Polymorphism (พหุสัณฐาน)

### 📝 คำอธิบาย
Polymorphism คือความสามารถที่ object สามารถมีหลายรูปแบบได้ แบ่งเป็น:
- **Compile-time Polymorphism:** Method Overloading
- **Runtime Polymorphism:** Method Overriding

### 💻 ตัวอย่างการใช้งาน

#### 2.1 Method Overriding
```java
// Parent interface
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
}

// Child implementation - Polymorphic behavior
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) {
        // Custom implementation for our system
    }
}
```

#### 2.2 Enum with Polymorphic Behavior
```java
// ไฟล์: src/main/java/com/parttimestudent/entity/User.java
public enum UserRole {
    STUDENT,
    PM,
    ADMIN
}

public enum Specialty {
    FRONTEND("Frontend"),
    BACKEND("Backend"),
    ML_ENGINEER("ML Engineer"),
    UX_UI("UX/UI"),
    QA("QA"),
    DEVOPS("DevOps");

    private final String displayName;

    Specialty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

**ตัวอย่างการใช้งาน:**
```java
// Polymorphic behavior - เรียกใช้ enum ได้หลายรูปแบบ
User user = new User();
user.setRole(UserRole.STUDENT);  // ใช้เป็น enum
String roleName = user.getRole().name();  // ใช้เป็น String
String display = Specialty.BACKEND.getDisplayName();  // เรียก custom method
```

#### 2.3 Interface Polymorphism
```java
// ไฟล์: src/main/java/com/parttimestudent/repository/ProjectRepository.java
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Query methods - Polymorphic behavior
    List<Project> findByStatus(Project.ProjectStatus status);
    List<Project> findByPmUserId(Long pmUserId);

    @Query("SELECT p FROM Project p WHERE p.deadline < :date")
    List<Project> findOverdueProjects(LocalDate date);
}
```

**คำอธิบาย:**
- Method name เดียวกันแต่ทำงานต่างกัน (method overloading ของ Spring Data JPA)
- Query methods สร้างจาก method name โดยอัตโนมัติ

### ✅ ประโยชน์
- **Flexibility:** โค้ดยืดหยุ่นและขยายได้ง่าย
- **Maintainability:** แก้ไข implementation โดยไม่กระทบ interface
- **Abstraction:** ซ่อนรายละเอียดการทำงานภายใน

---

## 3. Aggregation & Composition

### 📝 คำอธิบาย
- **Composition (Has-A แบบแน่นแฟ้น):** Object หนึ่งเป็นเจ้าของ object อื่นทั้งหมด เมื่อ parent ถูกลบ child จะถูกลบด้วย
- **Aggregation (Has-A แบบหลวม):** Object หนึ่งอ้างอิง object อื่น แต่สามารถอยู่ได้โดยอิสระ

### 💻 ตัวอย่างการใช้งาน

#### 3.1 Composition (cascade = ALL, orphanRemoval = true)
```java
// ไฟล์: src/main/java/com/parttimestudent/entity/User.java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    // COMPOSITION: User เป็นเจ้าของ TimetableSlot ทั้งหมด
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimetableSlot> timetableSlots = new HashSet<>();

    // COMPOSITION: User เป็นเจ้าของ ProjectMember ทั้งหมด
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> projectMemberships = new HashSet<>();

    // AGGREGATION: User อ้างอิงไปยัง Project แต่ไม่ได้เป็นเจ้าของ
    @OneToMany(mappedBy = "pmUser")
    private Set<Project> managedProjects = new HashSet<>();
}
```

**คำอธิบาย:**
- `cascade = CascadeType.ALL`: เมื่อ save/update/delete User จะกระทบ TimetableSlot ด้วย
- `orphanRemoval = true`: เมื่อตัด relationship จะลบ child object ทิ้ง
- ไม่มี cascade ใน `managedProjects` = Aggregation (ลบ User ไม่ลบ Project)

#### 3.2 Many-to-One Aggregation
```java
// ไฟล์: src/main/java/com/parttimestudent/entity/Project.java
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;

    // AGGREGATION: Project อ้างอิงไปยัง User (PM)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pm_user_id")
    private User pmUser;

    // AGGREGATION: Project อ้างอิงไปยัง User (Creator)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // COMPOSITION: Project เป็นเจ้าของ ProjectMember
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> projectMembers = new HashSet<>();
}
```

#### 3.3 Join Table (Many-to-Many)
```java
// ไฟล์: src/main/java/com/parttimestudent/entity/ProjectMember.java
@Entity
@Table(name = "project_members")
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // AGGREGATION: อ้างอิงไปยัง Project
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // AGGREGATION: อ้างอิงไปยัง User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
```

### 📊 ความสัมพันธ์ในระบบ

```
User (1) ──[COMPOSITION]──> (M) TimetableSlot
       ลบ User → ลบ TimetableSlot ทั้งหมด

User (1) ──[COMPOSITION]──> (M) ProjectMember
       ลบ User → ลบ ProjectMember ทั้งหมด

Project (1) ──[COMPOSITION]──> (M) ProjectMember
       ลบ Project → ลบ ProjectMember ทั้งหมด

User (1) ──[AGGREGATION]──> (M) Project (as PM)
       ลบ User → Project ยังคงอยู่ (PM = null)

User (1) ──[AGGREGATION]──> (M) Project (as Creator)
       ลบ User → Project ยังคงอยู่
```

### ✅ ประโยชน์
- **Data Integrity:** การจัดการความสัมพันธ์ระหว่าง object อย่างชัดเจน
- **Cascade Operations:** ลดโค้ดในการจัดการ related objects
- **Flexibility:** เลือกใช้ Composition หรือ Aggregation ตามความเหมาะสม

---

## 4. File Input (การนำเข้าไฟล์)

### 📝 คำอธิบาย
ระบบรองรับการอัพโหลดไฟล์รูปภาพตารางเรียน แล้วใช้ AI (Google Gemini) ในการแกะข้อมูลออกมาโดยอัตโนมัติ

### 💻 ตัวอย่างการใช้งาน

#### 4.1 Controller Layer - รับไฟล์จาก Client
```java
// ไฟล์: src/main/java/com/parttimestudent/controller/StudentController.java
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private TimetableService timetableService;

    @Autowired
    private UserService userService;

    @PostMapping("/timetable/upload")
    public ResponseEntity<TimetableResponse> uploadTimetable(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = userService.getUserByUsername(authentication.getName());
            TimetableResponse response = timetableService.uploadTimetable(user.getId(), file);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
```

**คำอธิบาย:**
- `@RequestParam("file") MultipartFile file`: รับไฟล์จาก form-data
- `Authentication`: ดึงข้อมูล user ที่ login อยู่
- ส่งต่อไฟล์ไปยัง Service layer

#### 4.2 Service Layer - ประมวลผลไฟล์
```java
// ไฟล์: src/main/java/com/parttimestudent/service/TimetableService.java
@Service
public class TimetableService {

    @Autowired
    private TimetableSlotRepository timetableSlotRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private UserService userService;

    @Transactional
    public TimetableResponse uploadTimetable(Long userId, MultipartFile file)
            throws IOException {
        User user = userService.getUserById(userId);

        // 1. Extract timetable from image using Gemini AI
        TimetableResponse timetableResponse = geminiService.extractTimetableFromImage(file);

        // 2. Delete existing timetable slots
        timetableSlotRepository.deleteByUserId(userId);

        // 3. Save new timetable slots
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (TimetableResponse.TimeSlot slot : timetableResponse.getSlots()) {
            TimetableSlot timetableSlot = new TimetableSlot();
            timetableSlot.setUser(user);
            timetableSlot.setDayOfWeek(slot.getDayOfWeek());
            timetableSlot.setStartTime(LocalTime.parse(slot.getStartTime(), timeFormatter));
            timetableSlot.setEndTime(LocalTime.parse(slot.getEndTime(), timeFormatter));
            timetableSlot.setSubject(slot.getSubject());
            timetableSlot.setIsFree(slot.getIsFree());

            timetableSlotRepository.save(timetableSlot);
        }

        return timetableResponse;
    }
}
```

#### 4.3 AI Service - แกะข้อมูลจากรูป
```java
// ไฟล์: src/main/java/com/parttimestudent/service/GeminiService.java
@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public TimetableResponse extractTimetableFromImage(MultipartFile file)
            throws IOException {
        // 1. Convert MultipartFile to byte array
        byte[] imageBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // 2. Prepare prompt for Gemini
        String prompt = """
            Please extract the timetable information from this image.
            Return data in JSON format with this structure:
            {
              "slots": [
                {
                  "dayOfWeek": "Monday",
                  "startTime": "09:00",
                  "endTime": "10:30",
                  "subject": "Mathematics",
                  "isFree": false
                }
              ]
            }
            """;

        // 3. Call Gemini API
        String jsonResponse = callGeminiAPI(base64Image, prompt);

        // 4. Parse JSON response
        ObjectMapper mapper = new ObjectMapper();
        TimetableResponse response = mapper.readValue(jsonResponse, TimetableResponse.class);

        return response;
    }

    private String callGeminiAPI(String base64Image, String prompt) {
        // Implementation of Gemini API call
    }
}
```

### 📊 Flow การทำงาน

```
1. Client อัพโหลดรูปตารางเรียน
   └─> POST /student/timetable/upload

2. Controller รับไฟล์ (MultipartFile)
   └─> ส่งต่อไปยัง Service

3. Service ประมวลผล
   ├─> เรียก GeminiService.extractTimetableFromImage()
   ├─> แปลง MultipartFile → byte[] → base64
   ├─> ส่ง base64 + prompt ไปยัง Gemini API
   └─> ได้ JSON response กลับมา

4. Parse JSON → TimetableResponse
   └─> บันทึกลง database (TimetableSlot)

5. Return response กลับไปยัง Client
```

### ✅ ประโยชน์
- **Automation:** ไม่ต้องกรอกข้อมูลเอง
- **Accuracy:** AI สามารถอ่านตารางได้แม่นยำ
- **User-Friendly:** แค่อัพโหลดรูป ระบบจัดการให้เอง

---

## 5. Data Sorting (การเรียงลำดับข้อมูล)

### 📝 คำอธิบาย
ระบบรองรับการเรียงลำดับข้อมูลแบบต่างๆ เช่น เรียงตามชื่อ (Alphabetical), เรียงตาม deadline, เรียงแบบผสม

### 💻 ตัวอย่างการใช้งาน

#### 5.1 Repository Layer - Spring Data JPA Method Naming
```java
// ไฟล์: src/main/java/com/parttimestudent/repository/UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // เรียงตามชื่อ (A-Z)
    List<User> findAllByOrderByFirstNameAsc();
    
    // เรียงตามชื่อ (Z-A)
    List<User> findAllByOrderByFirstNameDesc();

    // เรียงตาม Role แล้วเรียงตามชื่อ
    List<User> findByRoleOrderByFirstNameAsc(User.UserRole role);

    // เรียงตาม Specialty แล้วเรียงตามชื่อ
    List<User> findBySpecialtyOrderByFirstNameAsc(String specialty);
}
```

**คำอธิบาย:**
- `OrderBy{FieldName}Asc`: เรียงจากน้อยไปมาก (Ascending)
- `OrderBy{FieldName}Desc`: เรียงจากมากไปน้อย (Descending)
- Spring Data JPA generate SQL query โดยอัตโนมัติจาก method name

```java
// ไฟล์: src/main/java/com/parttimestudent/repository/ProjectRepository.java
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // เรียงตามชื่อโปรเจค (A-Z)
    List<Project> findAllByOrderByProjectNameAsc();

    // เรียงตามชื่อโปรเจค (Z-A)
    List<Project> findAllByOrderByProjectNameDesc();

    // เรียงตาม Status แล้วเรียงตามชื่อ
    List<Project> findByStatusOrderByProjectNameAsc(Project.ProjectStatus status);

    // JPQL Query - เรียงตามชื่อและ deadline
    @Query("SELECT p FROM Project p ORDER BY p.projectName ASC, p.deadline ASC")
    List<Project> findAllOrderByProjectNameAndDeadline();

    // เรียงตาม deadline
    @Query("SELECT p FROM Project p ORDER BY p.deadline ASC")
    List<Project> findAllOrderByDeadline();
}
```

#### 5.2 Service Layer - Business Logic
```java
// ไฟล์: src/main/java/com/parttimestudent/service/UserService.java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // เรียงตามชื่อ (รองรับ asc/desc)
    public List<User> getAllUsersSortedByName(String order) {
        if ("desc".equalsIgnoreCase(order)) {
            return userRepository.findAllByOrderByFirstNameDesc();
        }
        return userRepository.findAllByOrderByFirstNameAsc();
    }

    // เรียงตาม Role แล้วเรียงตามชื่อ
    public List<User> getUsersByRoleSortedByName(User.UserRole role) {
        return userRepository.findByRoleOrderByFirstNameAsc(role);
    }

    // เรียงตาม Specialty แล้วเรียงตามชื่อ
    public List<User> getUsersBySpecialtySortedByName(String specialty) {
        return userRepository.findBySpecialtyOrderByFirstNameAsc(specialty);
    }
}
```

```java
// ไฟล์: src/main/java/com/parttimestudent/service/ProjectService.java
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // เรียงตามชื่อโปรเจค (รองรับ asc/desc)
    public List<Project> getAllProjectsSortedByName(String order) {
        if ("desc".equalsIgnoreCase(order)) {
            return projectRepository.findAllByOrderByProjectNameDesc();
        }
        return projectRepository.findAllByOrderByProjectNameAsc();
    }

    // เรียงตาม Status แล้วเรียงตามชื่อ
    public List<Project> getProjectsByStatusSortedByName(Project.ProjectStatus status) {
        return projectRepository.findByStatusOrderByProjectNameAsc(status);
    }

    // เรียงตามชื่อและ deadline
    public List<Project> getAllProjectsSortedByNameAndDeadline() {
        return projectRepository.findAllOrderByProjectNameAndDeadline();
    }

    // เรียงตาม deadline
    public List<Project> getAllProjectsOrderedByDeadline() {
        return projectRepository.findAllOrderByDeadline();
    }
}
```

#### 5.3 Controller Layer - API Endpoints
```java
// ไฟล์: src/main/java/com/parttimestudent/controller/AdminController.java
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    // GET /admin/users?sortBy=name&order=asc
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        List<User> users;
        if ("name".equalsIgnoreCase(sortBy)) {
            users = userService.getAllUsersSortedByName(order);
        } else {
            users = userService.getAllUsers();
        }
        return ResponseEntity.ok(users);
    }

    // GET /admin/projects?sortBy=name&order=desc
    // GET /admin/projects?sortBy=deadline
    // GET /admin/projects?sortBy=nameAndDeadline
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        List<Project> projects;
        if ("name".equalsIgnoreCase(sortBy)) {
            projects = projectService.getAllProjectsSortedByName(order);
        } else if ("deadline".equalsIgnoreCase(sortBy)) {
            projects = projectService.getAllProjectsOrderedByDeadline();
        } else if ("nameAndDeadline".equalsIgnoreCase(sortBy)) {
            projects = projectService.getAllProjectsSortedByNameAndDeadline();
        } else {
            projects = projectService.getAllProjects();
        }
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
```

### 📊 ตัวอย่างการเรียกใช้ API

```bash
# เรียงผู้ใช้ตามชื่อ A-Z
GET /admin/users?sortBy=name&order=asc

# เรียงผู้ใช้ตามชื่อ Z-A
GET /admin/users?sortBy=name&order=desc

# เรียงโปรเจคตามชื่อ A-Z
GET /admin/projects?sortBy=name&order=asc

# เรียงโปรเจคตาม deadline
GET /admin/projects?sortBy=deadline

# เรียงโปรเจคตามชื่อและ deadline
GET /admin/projects?sortBy=nameAndDeadline
```

### 🎯 ตัวอย่างผลลัพธ์

**Before Sorting:**
```json
[
  {"id": 1, "firstName": "Somchai"},
  {"id": 2, "firstName": "Alice"},
  {"id": 3, "firstName": "Bob"}
]
```

**After Sorting (A-Z):**
```json
[
  {"id": 2, "firstName": "Alice"},
  {"id": 3, "firstName": "Bob"},
  {"id": 1, "firstName": "Somchai"}
]
```

### ✅ ประโยชน์
- **User Experience:** ผู้ใช้หาข้อมูลได้ง่ายขึ้น
- **Performance:** ใช้ database sorting (เร็วกว่า application sorting)
- **Flexibility:** รองรับหลาย sorting criteria

---

## 6. Data Searching (การค้นหาข้อมูล)

### 📝 คำอธิบาย
ระบบรองรับการค้นหาข้อมูลแบบต่างๆ เช่น ค้นหาตามชื่อ, Role, Specialty, Status, Deadline

### 💻 ตัวอย่างการใช้งาน

#### 6.1 Repository Layer - Query Methods
```java
// ไฟล์: src/main/java/com/parttimestudent/repository/UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ค้นหาตาม username (ใช้ใน login)
    Optional<User> findByUsername(String username);

    // ค้นหาตาม custom ID
    Optional<User> findByCustomId(String customId);

    // ค้นหาตาม Role
    List<User> findByRole(User.UserRole role);

    // ค้นหาตาม Specialty
    List<User> findBySpecialty(String specialty);

    // ค้นหาตามสถานะ active/inactive
    List<User> findByIsActive(Boolean isActive);

    // ตรวจสอบว่า username มีอยู่แล้วหรือไม่
    boolean existsByUsername(String username);

    // Custom JPQL Query
    @Query("SELECT MAX(CAST(SUBSTRING(u.customId, 5) AS integer)) " +
           "FROM User u WHERE u.customId LIKE 'TTTP%'")
    Integer findMaxCustomIdNumber();
}
```

**คำอธิบาย:**
- `findBy{FieldName}`: Spring Data JPA generate query จาก method name
- `Optional<T>`: ป้องกัน NullPointerException
- `@Query`: เขียน JPQL query เองสำหรับ query ที่ซับซ้อน

```java
// ไฟล์: src/main/java/com/parttimestudent/repository/ProjectRepository.java
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // ค้นหาตาม Status
    List<Project> findByStatus(Project.ProjectStatus status);

    // ค้นหาโปรเจคที่ PM เป็นคนนี้
    List<Project> findByPmUser(User pmUser);
    List<Project> findByPmUserId(Long pmUserId);

    // ค้นหาโปรเจคที่สร้างโดยคนนี้
    List<Project> findByCreatedBy(User createdBy);

    // ค้นหาโปรเจคที่เลย deadline แล้ว (JPQL)
    @Query("SELECT p FROM Project p " +
           "WHERE p.deadline < :date AND p.status != 'DONE'")
    List<Project> findOverdueProjects(LocalDate date);

    // ค้นหาโปรเจคที่ใกล้ deadline (JPQL)
    @Query("SELECT p FROM Project p " +
           "WHERE p.deadline BETWEEN :startDate AND :endDate " +
           "AND p.status != 'DONE'")
    List<Project> findProjectsDueSoon(LocalDate startDate, LocalDate endDate);

    // ค้นหาโปรเจคที่ user เป็นสมาชิก (JOIN query)
    @Query("SELECT DISTINCT p FROM Project p " +
           "JOIN p.projectMembers m WHERE m.user.id = :userId")
    List<Project> findByMembersId(@Param("userId") Long userId);
}
```

#### 6.2 Service Layer - Business Logic
```java
// ไฟล์: src/main/java/com/parttimestudent/service/UserService.java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ค้นหา user ตาม ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ค้นหา user ตาม username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ค้นหาผู้ใช้ทั้งหมด
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ค้นหาตาม Role
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    // ค้นหาตาม Specialty
    public List<User> getUsersBySpecialty(String specialty) {
        return userRepository.findBySpecialty(specialty);
    }
}
```

```java
// ไฟล์: src/main/java/com/parttimestudent/service/ProjectService.java
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // ค้นหาโปรเจคตาม ID
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    // ค้นหาโปรเจคตาม Status
    public List<Project> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }

    // ค้นหาโปรเจคที่ PM เป็นคนนี้
    public List<Project> getProjectsByPm(Long pmUserId) {
        return projectRepository.findByPmUserId(pmUserId);
    }

    // ค้นหาโปรเจคที่ user เป็นสมาชิก
    public List<Project> getProjectsByMember(Long userId) {
        return projectRepository.findByMembersId(userId);
    }

    // ค้นหาโปรเจคที่ใกล้ deadline
    public List<Project> getProjectsDueSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return projectRepository.findProjectsDueSoon(today, futureDate);
    }

    // ค้นหาโปรเจคที่เลย deadline
    public List<Project> getOverdueProjects() {
        return projectRepository.findOverdueProjects(LocalDate.now());
    }

    // ค้นหาโปรเจคที่ต้องการความช่วยเหลือ
    public List<Project> getProjectsNeedingHelp() {
        return projectRepository.findByStatus(Project.ProjectStatus.HELP);
    }
}
```

#### 6.3 Controller Layer - API Endpoints
```java
// ไฟล์: src/main/java/com/parttimestudent/controller/AdminController.java
@RestController
@RequestMapping("/admin")
public class AdminController {

    // GET /admin/users - ดู users ทั้งหมด
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // GET /admin/users/1 - ดู user ตาม ID
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // GET /admin/projects/status/IN_PROCESS - กรองตาม status
    @GetMapping("/projects/status/{status}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByStatus(
            @PathVariable String status) {
        Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status);
        List<Project> projects = projectService.getProjectsByStatus(projectStatus);
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET /admin/projects/due-soon?days=7 - โปรเจคใกล้ deadline
    @GetMapping("/projects/due-soon")
    public ResponseEntity<List<ProjectResponse>> getProjectsDueSoon(
            @RequestParam(defaultValue = "7") int days) {
        List<Project> projects = projectService.getProjectsDueSoon(days);
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET /admin/projects/overdue - โปรเจคเลย deadline
    @GetMapping("/projects/overdue")
    public ResponseEntity<List<ProjectResponse>> getOverdueProjects() {
        List<Project> projects = projectService.getOverdueProjects();
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET /admin/projects/help - โปรเจคต้องการช่วยเหลือ
    @GetMapping("/projects/help")
    public ResponseEntity<List<ProjectResponse>> getProjectsNeedingHelp() {
        List<Project> projects = projectService.getProjectsNeedingHelp();
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
```

```java
// ไฟล์: src/main/java/com/parttimestudent/controller/PMController.java
@RestController
@RequestMapping("/pm")
public class PMController {

    // GET /pm/students - ดูรายชื่อนักเรียนทั้งหมด
    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        List<User> students = userService.getUsersByRole(User.UserRole.STUDENT);
        return ResponseEntity.ok(students);
    }

    // GET /pm/students/specialty/Backend - ค้นหาตาม specialty
    @GetMapping("/students/specialty/{specialty}")
    public ResponseEntity<List<User>> getStudentsBySpecialty(
            @PathVariable String specialty) {
        List<User> students = userService.getUsersBySpecialty(specialty);
        return ResponseEntity.ok(students);
    }

    // GET /pm/projects - ดูโปรเจคที่ตัวเองเป็น PM
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(
            Authentication authentication) {
        User pm = userService.getUserByUsername(authentication.getName());
        List<Project> projects = projectService.getProjectsByPm(pm.getId());
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
```

### 📊 ตัวอย่างการเรียกใช้ API

```bash
# ค้นหา user ทั้งหมด
GET /admin/users

# ค้นหา user ตาม ID
GET /admin/users/1

# ค้นหา students ตาม specialty
GET /pm/students/specialty/Backend

# ค้นหาโปรเจคตาม status
GET /admin/projects/status/IN_PROCESS

# ค้นหาโปรเจคที่ใกล้ deadline ภายใน 7 วัน
GET /admin/projects/due-soon?days=7

# ค้นหาโปรเจคที่เลย deadline
GET /admin/projects/overdue

# ค้นหาโปรเจคที่ต้องการช่วยเหลือ
GET /admin/projects/help
```

### 🎯 ประเภทของ Query Methods

| Pattern | ตัวอย่าง | SQL Generated |
|---------|---------|---------------|
| `findBy{Field}` | `findByUsername(String username)` | `SELECT * FROM users WHERE username = ?` |
| `findBy{Field1}And{Field2}` | `findByRoleAndIsActive(Role, Boolean)` | `SELECT * FROM users WHERE role = ? AND is_active = ?` |
| `findBy{Field}OrderBy{Field2}` | `findByRoleOrderByFirstNameAsc(Role)` | `SELECT * FROM users WHERE role = ? ORDER BY first_name ASC` |
| `@Query + JPQL` | `@Query("SELECT ... WHERE ...")` | Custom SQL |

### ✅ ประโยชน์
- **Efficiency:** ค้นหาได้รวดเร็วด้วย database indexing
- **Flexibility:** รองรับการค้นหาแบบต่างๆ
- **Type Safety:** ตรวจสอบ type ตอน compile time
- **Auto-generated SQL:** ไม่ต้องเขียน SQL เอง

---

## 📊 สรุปภาพรวม

| แนวคิด | ระดับการใช้งาน | ตำแหน่งในโค้ด |
|--------|---------------|---------------|
| **Inheritance** | ⭐⭐⭐ | Security filters, Repository interfaces |
| **Polymorphism** | ⭐⭐⭐⭐ | Method overriding, Enum behaviors |
| **Aggregation/Composition** | ⭐⭐⭐⭐⭐ | Entity relationships, JPA mappings |
| **File Input** | ⭐⭐⭐⭐⭐ | TimetableService, GeminiService |
| **Data Sorting** | ⭐⭐⭐⭐⭐ | Repository methods, Controller endpoints |
| **Data Searching** | ⭐⭐⭐⭐⭐ | Repository queries, Service layer |

---

## 🎯 ข้อดีของการใช้แนวคิดเหล่านี้

1. **Code Reusability** - ลดการเขียนโค้ดซ้ำซ้อน
2. **Maintainability** - แก้ไขและดูแลรักษาง่าย
3. **Scalability** - ขยายระบบได้ง่ายในอนาคต
4. **Flexibility** - ยืดหยุ่นต่อการเปลี่ยนแปลง
5. **Type Safety** - ตรวจสอบ type ได้ตั้งแต่ compile time
6. **Performance** - ประสิทธิภาพดีด้วย database-level operations

---

## 📚 เอกสารอ้างอิง

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [Java OOP Concepts](https://docs.oracle.com/javase/tutorial/java/concepts/)
- [Hibernate JPA Annotations](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)

---

**หมายเหตุ:** เอกสารนี้อธิบายการใช้แนวคิด OOP และเทคนิคต่างๆ ในโปรเจกต์ Student Part-time Management System พร้อมตัวอย่างโค้ดจริงจากระบบ
