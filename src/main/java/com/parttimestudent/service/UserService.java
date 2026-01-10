package com.parttimestudent.service;

import com.parttimestudent.dto.AuthResponse;
import com.parttimestudent.dto.LoginRequest;
import com.parttimestudent.dto.RegisterRequest;
import com.parttimestudent.entity.User;
import com.parttimestudent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Generate custom ID (TTTP01, TTTP02, etc.)
        String customId = generateCustomId();
        
        // Create new user
        User user = new User();
        user.setCustomId(customId);
        user.setFirstName(request.getFirstName());
        user.setYearLevel(request.getYearLevel());
        user.setSpecialty(request.getSpecialty());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.UserRole.STUDENT);
        user.setIsActive(true);
        
        user = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername());
        
        return new AuthResponse(token, user.getId(), user.getCustomId(), 
                               user.getUsername(), user.getFirstName(), 
                               user.getRole().name());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        if (!user.getIsActive()) {
            throw new RuntimeException("User account is inactive");
        }
        
        String token = jwtService.generateToken(user.getUsername());
        
        return new AuthResponse(token, user.getId(), user.getCustomId(), 
                               user.getUsername(), user.getFirstName(), 
                               user.getRole().name());
    }
    
    private String generateCustomId() {
        Integer maxNumber = userRepository.findMaxCustomIdNumber();
        int nextNumber = (maxNumber == null) ? 1 : maxNumber + 1;
        return String.format("TTTP%02d", nextNumber);
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional
    public User updateUser(Long id, RegisterRequest request) {
        User user = getUserById(id);
        
        // Check if username is being changed and if it already exists
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        user.setFirstName(request.getFirstName());
        user.setYearLevel(request.getYearLevel());
        user.setSpecialty(request.getSpecialty());
        user.setUsername(request.getUsername());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    @Transactional
    public User updateUserRole(Long id, User.UserRole role) {
        User user = getUserById(id);
        user.setRole(role);
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getUsersBySpecialty(String specialty) {
        return userRepository.findBySpecialty(specialty);
    }

    // Sorting methods
    public List<User> getAllUsersSortedByName(String order) {
        if ("desc".equalsIgnoreCase(order)) {
            return userRepository.findAllByOrderByFirstNameDesc();
        }
        return userRepository.findAllByOrderByFirstNameAsc();
    }

    public List<User> getUsersByRoleSortedByName(User.UserRole role) {
        return userRepository.findByRoleOrderByFirstNameAsc(role);
    }

    public List<User> getUsersBySpecialtySortedByName(String specialty) {
        return userRepository.findBySpecialtyOrderByFirstNameAsc(specialty);
    }
}