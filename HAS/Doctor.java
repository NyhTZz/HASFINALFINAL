package cce105f;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



class Doctor {
    String id;
    String name;
    String specialty;
    List<TimeSlot> availableSlots;
    
    public Doctor(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.availableSlots = new ArrayList<>();
    }
    
    public void addAvailableSlot(LocalDateTime start, LocalDateTime end) {
        availableSlots.add(new TimeSlot(start, end));
    }
    
    public Doctor deepCopy() {
        Doctor copy = new Doctor(this.id, this.name, this.specialty);
        for (TimeSlot slot : this.availableSlots) {
            copy.addAvailableSlot(slot.start, slot.end);
        }
        return copy;
    }
    
    // Convert to file format
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|").append(name).append("|").append(specialty).append("|");
        
        for (int i = 0; i < availableSlots.size(); i++) {
            if (i > 0) sb.append(";");
            TimeSlot slot = availableSlots.get(i);
            sb.append(slot.start.format(formatter)).append(",").append(slot.end.format(formatter));
        }
        
        return sb.toString();
    }
    
    // Parse from file format
    public static Doctor fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 3) return null;
        
        Doctor doctor = new Doctor(parts[0], parts[1], parts[2]);
        
        if (parts.length > 3 && !parts[3].isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String[] slots = parts[3].split(";");
            
            for (String slot : slots) {
                String[] times = slot.split(",");
                if (times.length == 2) {
                    LocalDateTime start = LocalDateTime.parse(times[0], formatter);
                    LocalDateTime end = LocalDateTime.parse(times[1], formatter);
                    doctor.addAvailableSlot(start, end);
                }
            }
        }
        
        return doctor;
    }
    
}