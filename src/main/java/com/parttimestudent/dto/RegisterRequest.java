package com.parttimestudent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Year level is required")
    private String yearLevel;
    
    @NotBlank(message = "Specialty is required")
    @Pattern(regexp = "Frontend|Backend|ML Engineer|UX/UI|QA|DevOps", 
             message = "Specialty must be one of: Frontend, Backend, ML Engineer, UX/UI, QA, DevOps")
    private String specialty;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}