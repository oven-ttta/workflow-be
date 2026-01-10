package com.parttimestudent.repository;

import com.parttimestudent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByCustomId(String customId);
    
    boolean existsByUsername(String username);
    
    boolean existsByCustomId(String customId);
    
    List<User> findByRole(User.UserRole role);
    
    List<User> findBySpecialty(String specialty);

    @Query("SELECT MAX(CAST(SUBSTRING(u.customId, 5) AS integer)) FROM User u WHERE u.customId LIKE 'TTTP%'")
    Integer findMaxCustomIdNumber();

    List<User> findByIsActive(Boolean isActive);

    // Sorting methods
    List<User> findAllByOrderByFirstNameAsc();

    List<User> findAllByOrderByFirstNameDesc();

    List<User> findByRoleOrderByFirstNameAsc(User.UserRole role);

    List<User> findBySpecialtyOrderByFirstNameAsc(String specialty);
}