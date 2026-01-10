package com.parttimestudent.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String customId;
    private String username;
    private String firstName;
    private String role;
    
    public AuthResponse(String token, Long id, String customId, String username, String firstName, String role) {
        this.token = token;
        this.id = id;
        this.customId = customId;
        this.username = username;
        this.firstName = firstName;
        this.role = role;
    }
}