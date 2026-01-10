package com.parttimestudent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "custom_id", unique = true, nullable = false, length = 20)
    private String customId;
    
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @Column(name = "year_level", nullable = false, length = 50)
    private String yearLevel;
    
    @Column(nullable = false, length = 50)
    private String specialty;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.STUDENT;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TimetableSlot> timetableSlots = new HashSet<>();
    
    @OneToMany(mappedBy = "pmUser")
    private Set<Project> managedProjects = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> projectMemberships = new HashSet<>();
    
    // Enum for User Roles
    public enum UserRole {
        STUDENT,
        PM,
        ADMIN
    }
    
    // Enum for Specialty
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
}