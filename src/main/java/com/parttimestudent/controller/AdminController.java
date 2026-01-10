package com.parttimestudent.controller;

import com.parttimestudent.dto.ProjectRequest;
import com.parttimestudent.dto.ProjectResponse;
import com.parttimestudent.dto.RegisterRequest;
import com.parttimestudent.dto.TimetableResponse;
import com.parttimestudent.entity.Project;
import com.parttimestudent.entity.User;
import com.parttimestudent.service.ProjectService;
import com.parttimestudent.service.TimetableService;
import com.parttimestudent.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TimetableService timetableService;
    
    // ===== User Management =====

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
    
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, 
                                          @Valid @RequestBody RegisterRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
    
    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, 
                                              @RequestParam String role) {
        User.UserRole userRole = User.UserRole.valueOf(role);
        User user = userService.updateUserRole(id, userRole);
        return ResponseEntity.ok(user);
    }
    
    // ===== Project Management =====
    
    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {
        User admin = userService.getUserByUsername(authentication.getName());
        Project project = projectService.createProject(request, admin.getId());
        ProjectResponse response = projectService.convertToResponse(project);
        return ResponseEntity.ok(response);
    }
    
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
    
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        ProjectResponse response = projectService.convertToResponse(project);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        Project project = projectService.updateProject(id, request);
        ProjectResponse response = projectService.convertToResponse(project);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project deleted successfully");
    }
    
    @PutMapping("/projects/{id}/status")
    public ResponseEntity<ProjectResponse> updateProjectStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Project project = projectService.updateProjectStatus(id, status);
        ProjectResponse response = projectService.convertToResponse(project);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/projects/{projectId}/members/{userId}")
    public ResponseEntity<String> addMemberToProject(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        try {
            projectService.addMemberToProject(projectId, userId);
            return ResponseEntity.ok("Member added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/projects/{projectId}/members/{userId}")
    public ResponseEntity<String> removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        try {
            projectService.removeMemberFromProject(projectId, userId);
            return ResponseEntity.ok("Member removed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // ===== Project Status Overview =====
    
    @GetMapping("/projects/status/overview")
    public ResponseEntity<ProjectStatusOverview> getProjectStatusOverview() {
        ProjectStatusOverview overview = new ProjectStatusOverview();
        overview.setAllProjects(projectService.getAllProjectsOrderedByDeadline()
                .stream().map(projectService::convertToResponse).collect(Collectors.toList()));
        overview.setProjectsDueSoon(projectService.getProjectsDueSoon(7)
                .stream().map(projectService::convertToResponse).collect(Collectors.toList()));
        overview.setOverdueProjects(projectService.getOverdueProjects()
                .stream().map(projectService::convertToResponse).collect(Collectors.toList()));
        overview.setProjectsNeedingHelp(projectService.getProjectsNeedingHelp()
                .stream().map(projectService::convertToResponse).collect(Collectors.toList()));
        return ResponseEntity.ok(overview);
    }
    
    @GetMapping("/projects/status/{status}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByStatus(@PathVariable String status) {
        Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status);
        List<Project> projects = projectService.getProjectsByStatus(projectStatus);
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/projects/due-soon")
    public ResponseEntity<List<ProjectResponse>> getProjectsDueSoon(@RequestParam(defaultValue = "7") int days) {
        List<Project> projects = projectService.getProjectsDueSoon(days);
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/projects/overdue")
    public ResponseEntity<List<ProjectResponse>> getOverdueProjects() {
        List<Project> projects = projectService.getOverdueProjects();
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/projects/help")
    public ResponseEntity<List<ProjectResponse>> getProjectsNeedingHelp() {
        List<Project> projects = projectService.getProjectsNeedingHelp();
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    // Inner class for project status overview
    public static class ProjectStatusOverview {
        private List<ProjectResponse> allProjects;
        private List<ProjectResponse> projectsDueSoon;
        private List<ProjectResponse> overdueProjects;
        private List<ProjectResponse> projectsNeedingHelp;
        
        public List<ProjectResponse> getAllProjects() {
            return allProjects;
        }
        
        public void setAllProjects(List<ProjectResponse> allProjects) {
            this.allProjects = allProjects;
        }
        
        public List<ProjectResponse> getProjectsDueSoon() {
            return projectsDueSoon;
        }
        
        public void setProjectsDueSoon(List<ProjectResponse> projectsDueSoon) {
            this.projectsDueSoon = projectsDueSoon;
        }
        
        public List<ProjectResponse> getOverdueProjects() {
            return overdueProjects;
        }
        
        public void setOverdueProjects(List<ProjectResponse> overdueProjects) {
            this.overdueProjects = overdueProjects;
        }
        
        public List<ProjectResponse> getProjectsNeedingHelp() {
            return projectsNeedingHelp;
        }
        
        public void setProjectsNeedingHelp(List<ProjectResponse> projectsNeedingHelp) {
            this.projectsNeedingHelp = projectsNeedingHelp;
        }
    }

    // ===== Timetable Management =====

    @GetMapping("/users/{userId}/timetable")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TimetableResponse> getUserTimetable(@PathVariable Long userId) {
        TimetableResponse timetable = timetableService.getUserTimetableResponse(userId);
        return ResponseEntity.ok(timetable);
    }
}