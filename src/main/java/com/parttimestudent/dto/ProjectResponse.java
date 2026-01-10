package com.parttimestudent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String projectName;
    private Integer difficultyLevel;
    private Integer durationDays;
    private String status;
    private LocalDate startDate;
    private LocalDate deadline;
    private PmInfo pmUser;
    private List<MemberInfo> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PmInfo {
        private Long id;
        private String customId;
        private String firstName;
        private String username;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long id;
        private String customId;
        private String firstName;
        private String specialty;
    }
}