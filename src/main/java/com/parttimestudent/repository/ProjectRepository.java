package com.parttimestudent.repository;

import com.parttimestudent.entity.Project;
import com.parttimestudent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(Project.ProjectStatus status);

    List<Project> findByPmUser(User pmUser);

    List<Project> findByPmUserId(Long pmUserId);

    List<Project> findByCreatedBy(User createdBy);

    @Query("SELECT p FROM Project p WHERE p.deadline < :date AND p.status != 'DONE'")
    List<Project> findOverdueProjects(LocalDate date);

    @Query("SELECT p FROM Project p WHERE p.deadline BETWEEN :startDate AND :endDate AND p.status != 'DONE'")
    List<Project> findProjectsDueSoon(LocalDate startDate, LocalDate endDate);

    @Query("SELECT p FROM Project p ORDER BY p.deadline ASC")
    List<Project> findAllOrderByDeadline();

    @Query("SELECT DISTINCT p FROM Project p JOIN p.projectMembers m WHERE m.user.id = :userId")
    List<Project> findByMembersId(@Param("userId") Long userId);

    // Sorting methods - Alphabetical order
    List<Project> findAllByOrderByProjectNameAsc();

    List<Project> findAllByOrderByProjectNameDesc();

    List<Project> findByStatusOrderByProjectNameAsc(Project.ProjectStatus status);

    @Query("SELECT p FROM Project p ORDER BY p.projectName ASC, p.deadline ASC")
    List<Project> findAllOrderByProjectNameAndDeadline();
}