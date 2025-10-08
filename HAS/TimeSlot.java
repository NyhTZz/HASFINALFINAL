package cce105f;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class TimeSlot {
    LocalDateTime start;
    LocalDateTime end;
    
    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }
    
    public boolean canFit(int durationMinutes) {
        return ChronoUnit.MINUTES.between(start, end) >= durationMinutes;
    }
}