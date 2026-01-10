package com.parttimestudent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableResponse {
    private List<TimeSlot> slots;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String subject;
        private Boolean isFree;
    }
}