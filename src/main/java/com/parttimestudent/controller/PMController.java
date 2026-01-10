package com.parttimestudent.controller;

import com.parttimestudent.dto.ProjectResponse;
import com.parttimestudent.dto.TimetableResponse;
import com.parttimestudent.entity.Project;
import com.parttimestudent.entity.User;
import com.parttimestudent.service.ProjectService;
import com.parttimestudent.service.TimetableService;
import com.parttimestudent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pm")
@CrossOrigin(origins = "*")
public class PMController {
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private TimetableService timetableService;
    
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getMyManagedProjects(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        List<Project> projects = projectService.getProjectsByPm(user.getId());
        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> getProjectDetails(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        ProjectResponse response = projectService.convertToResponse(project);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/projects/{projectId}/members/{userId}")
    public ResponseEntity<String> addMemberToProject(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            User currentUser = userService.getUserByUsername(authentication.getName());
            Project project = projectService.getProjectById(projectId);
            
            // Check if current user is the PM of this project
            if (!project.getPmUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("Only the PM can add members");
            }
            
            projectService.addMemberToProject(projectId, userId);
            return ResponseEntity.ok("Member added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/projects/{projectId}/members/{userId}")
    public ResponseEntity<String> removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            User currentUser = userService.getUserByUsername(authentication.getName());
            Project project = projectService.getProjectById(projectId);
            
            // Check if current user is the PM of this project
            if (!project.getPmUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("Only the PM can remove members");
            }
            
            projectService.removeMemberFromProject(projectId, userId);
            return ResponseEntity.ok("Member removed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/projects/{id}/status")
    public ResponseEntity<ProjectResponse> updateProjectStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {
        try {
            User currentUser = userService.getUserByUsername(authentication.getName());
            Project project = projectService.getProjectById(id);
            
            // Check if current user is the PM of this project
            if (!project.getPmUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(null);
            }
            
            Project updatedProject = projectService.updateProjectStatus(id, status);
            ProjectResponse response = projectService.convertToResponse(updatedProject);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    
    @GetMapping("/students")
    public ResponseEntity<List<User>> getAvailableStudents() {
        List<User> students = userService.getUsersByRole(User.UserRole.STUDENT);
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/students/specialty/{specialty}")
    public ResponseEntity<List<User>> getStudentsBySpecialty(@PathVariable String specialty) {
        List<User> students = userService.getUsersBySpecialty(specialty);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/{userId}/timetable")
    @PreAuthorize("hasAnyAuthority('PM', 'ADMIN')")
    public ResponseEntity<TimetableResponse> getStudentTimetable(@PathVariable Long userId) {
        TimetableResponse timetable = timetableService.getUserTimetableResponse(userId);
        return ResponseEntity.ok(timetable);
    }
}
