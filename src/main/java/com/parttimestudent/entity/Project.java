package com.parttimestudent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "project_name", nullable = false, length = 200)
    private String projectName;
    
    @Column(name = "difficulty_level", nullable = false)
    private Integer difficultyLevel;
    
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pm_user_id")
    private User pmUser;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.NOT_STARTED;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column
    private LocalDate deadline;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> projectMembers = new HashSet<>();
    
    // Enum for Project Status
    public enum ProjectStatus {
        NOT_STARTED("Not Started"),
        IN_PROCESS("In Process"),
        TEST("Test"),
        REVIEW("Review"),
        DONE("Done"),
        HELP("Help!!!");
        
        private final String displayName;
        
        ProjectStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Helper method to add member
    public void addMember(User user) {
        ProjectMember member = new ProjectMember();
        member.setProject(this);
        member.setUser(user);
        projectMembers.add(member);
        user.getProjectMemberships().add(member);
    }
    
    // Helper method to remove member
    public void removeMember(User user) {
        projectMembers.removeIf(member -> 
            member.getUser().getId().equals(user.getId()));
        user.getProjectMemberships().removeIf(member -> 
            member.getProject().getId().equals(this.getId()));
    }
}