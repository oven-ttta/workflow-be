package com.parttimestudent.service;

import com.parttimestudent.dto.ProjectRequest;
import com.parttimestudent.dto.ProjectResponse;
import com.parttimestudent.entity.Project;
import com.parttimestudent.entity.ProjectMember;
import com.parttimestudent.entity.User;
import com.parttimestudent.repository.ProjectMemberRepository;
import com.parttimestudent.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    
    @Autowired
    private UserService userService;
    
    @Transactional
    public Project createProject(ProjectRequest request, Long createdById) {
        User createdBy = userService.getUserById(createdById);
        
        Project project = new Project();
        project.setProjectName(request.getProjectName());
        project.setDifficultyLevel(request.getDifficultyLevel());
        project.setDurationDays(request.getDurationDays());
        project.setStatus(Project.ProjectStatus.NOT_STARTED);
        project.setCreatedBy(createdBy);
        
        // Set PM if provided
        if (request.getPmUserId() != null) {
            User pmUser = userService.getUserById(request.getPmUserId());
            project.setPmUser(pmUser);
            
            // Update PM role
            if (pmUser.getRole() == User.UserRole.STUDENT) {
                pmUser.setRole(User.UserRole.PM);
            }
        }
        
        // Set dates
        LocalDate startDate = (request.getStartDate() != null) ? 
                             request.getStartDate() : LocalDate.now();
        project.setStartDate(startDate);
        project.setDeadline(startDate.plusDays(request.getDurationDays()));
        
        return projectRepository.save(project);
    }
    
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
    
    @Transactional
    public Project updateProject(Long id, ProjectRequest request) {
        Project project = getProjectById(id);
        
        project.setProjectName(request.getProjectName());
        project.setDifficultyLevel(request.getDifficultyLevel());
        project.setDurationDays(request.getDurationDays());
        
        if (request.getPmUserId() != null) {
            User pmUser = userService.getUserById(request.getPmUserId());
            project.setPmUser(pmUser);
            
            if (pmUser.getRole() == User.UserRole.STUDENT) {
                pmUser.setRole(User.UserRole.PM);
            }
        }
        
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
            project.setDeadline(request.getStartDate().plusDays(request.getDurationDays()));
        }
        
        return projectRepository.save(project);
    }
    
    @Transactional
    public Project updateProjectStatus(Long id, String status) {
        Project project = getProjectById(id);
        project.setStatus(Project.ProjectStatus.valueOf(status));
        return projectRepository.save(project);
    }
    
    @Transactional
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
    
    @Transactional
    public void addMemberToProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        User user = userService.getUserById(userId);
        
        // Check if user is already a member
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new RuntimeException("User is already a member of this project");
        }
        
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        
        projectMemberRepository.save(member);
    }
    
    @Transactional
    public void removeMemberFromProject(Long projectId, Long userId) {
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new RuntimeException("User is not a member of this project");
        }
        
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }
    
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    public List<Project> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }
    
    public List<Project> getProjectsByPm(Long pmUserId) {
        return projectRepository.findByPmUserId(pmUserId);
    }
    
    public List<Project> getUserProjects(Long userId) {
        List<ProjectMember> memberships = projectMemberRepository.findByUserId(userId);
        return memberships.stream()
                .map(ProjectMember::getProject)
                .collect(Collectors.toList());
    }

    public List<Project> getProjectsByMember(Long userId) {
        return projectRepository.findByMembersId(userId);
    }
    
    public List<Project> getProjectsDueSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return projectRepository.findProjectsDueSoon(today, futureDate);
    }
    
    public List<Project> getOverdueProjects() {
        return projectRepository.findOverdueProjects(LocalDate.now());
    }
    
    public List<Project> getProjectsNeedingHelp() {
        return projectRepository.findByStatus(Project.ProjectStatus.HELP);
    }
    
    public List<Project> getAllProjectsOrderedByDeadline() {
        return projectRepository.findAllOrderByDeadline();
    }

    // Sorting methods - Alphabetical order
    public List<Project> getAllProjectsSortedByName(String order) {
        if ("desc".equalsIgnoreCase(order)) {
            return projectRepository.findAllByOrderByProjectNameDesc();
        }
        return projectRepository.findAllByOrderByProjectNameAsc();
    }

    public List<Project> getProjectsByStatusSortedByName(Project.ProjectStatus status) {
        return projectRepository.findByStatusOrderByProjectNameAsc(status);
    }

    public List<Project> getAllProjectsSortedByNameAndDeadline() {
        return projectRepository.findAllOrderByProjectNameAndDeadline();
    }

    public ProjectResponse convertToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setProjectName(project.getProjectName());
        response.setDifficultyLevel(project.getDifficultyLevel());
        response.setDurationDays(project.getDurationDays());
        response.setStatus(project.getStatus().name());
        response.setStartDate(project.getStartDate());
        response.setDeadline(project.getDeadline());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        
        // Set PM info
        if (project.getPmUser() != null) {
            User pm = project.getPmUser();
            response.setPmUser(new ProjectResponse.PmInfo(
                    pm.getId(), pm.getCustomId(), pm.getFirstName(), pm.getUsername()));
        }
        
        // Set members info
        List<ProjectMember> members = projectMemberRepository.findByProjectId(project.getId());
        List<ProjectResponse.MemberInfo> memberInfos = members.stream()
                .map(member -> {
                    User user = member.getUser();
                    return new ProjectResponse.MemberInfo(
                            user.getId(), user.getCustomId(), 
                            user.getFirstName(), user.getSpecialty());
                })
                .collect(Collectors.toList());
        response.setMembers(memberInfos);
        
        return response;
    }
}