package com.parttimestudent.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {
    
    @NotBlank(message = "Project name is required")
    private String projectName;
    
    @NotNull(message = "Difficulty level is required")
    @Min(value = 1, message = "Difficulty level must be between 1 and 5")
    @Max(value = 5, message = "Difficulty level must be between 1 and 5")
    private Integer difficultyLevel;
    
    @NotNull(message = "Duration in days is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;
    
    private Long pmUserId;
    
    private LocalDate startDate;
}