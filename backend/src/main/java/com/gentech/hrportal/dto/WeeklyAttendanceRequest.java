package com.gentech.hrportal.dto;

import java.time.LocalDate;
import java.util.List;

public class WeeklyAttendanceRequest {
    
    private LocalDate weekStartDate;
    private List<AttendanceRequest> attendances;
    
    public WeeklyAttendanceRequest() {}
    
    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }
    
    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
    
    public List<AttendanceRequest> getAttendances() {
        return attendances;
    }
    
    public void setAttendances(List<AttendanceRequest> attendances) {
        this.attendances = attendances;
    }
}
