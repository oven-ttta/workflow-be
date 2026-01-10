package com.parttimestudent.repository;

import com.parttimestudent.entity.TimetableSlot;
import com.parttimestudent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    
    List<TimetableSlot> findByUser(User user);
    
    List<TimetableSlot> findByUserId(Long userId);
    
    List<TimetableSlot> findByUserIdAndIsFree(Long userId, Boolean isFree);
    
    List<TimetableSlot> findByUserIdAndDayOfWeek(Long userId, String dayOfWeek);
    
    void deleteByUserId(Long userId);
}