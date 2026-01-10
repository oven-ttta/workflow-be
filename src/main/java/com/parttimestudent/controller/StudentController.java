package com.parttimestudent.controller;

import com.parttimestudent.dto.ProjectResponse;
import com.parttimestudent.dto.RegisterRequest;
import com.parttimestudent.dto.TimetableResponse;
import com.parttimestudent.entity.Project;
import com.parttimestudent.entity.User;
import com.parttimestudent.service.ProjectService;
import com.parttimestudent.service.TimetableService;
import com.parttimestudent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TimetableService timetableService;
    
    @Autowired
    private ProjectService projectService;
    
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(Authentication authentication, 
                                             @RequestBody RegisterRequest request) {
        User currentUser = userService.getUserByUsername(authentication.getName());
        User updatedUser = userService.updateUser(currentUser.getId(), request);
        return ResponseEntity.ok(updatedUser);
    }
    
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
    
    @GetMapping("/timetable")
    public ResponseEntity<TimetableResponse> getTimetable(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        TimetableResponse response = timetableService.getUserTimetableResponse(user.getId());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(Authentication authentication) {
        User student = userService.getUserByUsername(authentication.getName());
        List<Project> projects = projectService.getProjectsByMember(student.getId());

        List<ProjectResponse> responses = projects.stream()
                .map(projectService::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
