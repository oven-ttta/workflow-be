package com.parttimestudent.service;

import com.parttimestudent.dto.TimetableResponse;
import com.parttimestudent.entity.TimetableSlot;
import com.parttimestudent.entity.User;
import com.parttimestudent.repository.TimetableSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimetableService {
    
    @Autowired
    private TimetableSlotRepository timetableSlotRepository;
    
    @Autowired
    private GeminiService geminiService;
    
    @Autowired
    private UserService userService;
    
    @Transactional
    public TimetableResponse uploadTimetable(Long userId, MultipartFile file) throws IOException {
        User user = userService.getUserById(userId);
        
        // Extract timetable from image using Gemini
        TimetableResponse timetableResponse = geminiService.extractTimetableFromImage(file);
        
        // Delete existing timetable slots for this user
        timetableSlotRepository.deleteByUserId(userId);
        
        // Save new timetable slots
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (TimetableResponse.TimeSlot slot : timetableResponse.getSlots()) {
            TimetableSlot timetableSlot = new TimetableSlot();
            timetableSlot.setUser(user);
            timetableSlot.setDayOfWeek(slot.getDayOfWeek());
            timetableSlot.setStartTime(LocalTime.parse(slot.getStartTime(), timeFormatter));
            timetableSlot.setEndTime(LocalTime.parse(slot.getEndTime(), timeFormatter));
            timetableSlot.setSubject(slot.getSubject());
            timetableSlot.setIsFree(slot.getIsFree());
            
            timetableSlotRepository.save(timetableSlot);
        }
        
        return timetableResponse;
    }
    
    public List<TimetableSlot> getUserTimetable(Long userId) {
        return timetableSlotRepository.findByUserId(userId);
    }
    
    public List<TimetableSlot> getUserFreeSlots(Long userId) {
        return timetableSlotRepository.findByUserIdAndIsFree(userId, true);
    }
    
    public List<TimetableSlot> getUserBusySlots(Long userId) {
        return timetableSlotRepository.findByUserIdAndIsFree(userId, false);
    }
    
    public TimetableResponse getUserTimetableResponse(Long userId) {
        List<TimetableSlot> slots = getUserTimetable(userId);
        
        List<TimetableResponse.TimeSlot> timeSlots = slots.stream()
                .map(slot -> new TimetableResponse.TimeSlot(
                        slot.getDayOfWeek(),
                        slot.getStartTime().toString(),
                        slot.getEndTime().toString(),
                        slot.getSubject(),
                        slot.getIsFree()
                ))
                .collect(Collectors.toList());
        
        return new TimetableResponse(timeSlots);
    }
}
