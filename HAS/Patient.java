package cce105f;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Patient {
    String id;
    String name;
    int urgencyLevel; // 1=Emergency, 2=Urgent, 3=Semi-Urgent, 4=Routine
    LocalDateTime requestTime;
    int estimatedDuration; 
    String requiredSpecialty;
    String contact;
    
    public Patient(String id, String name, int urgencyLevel, LocalDateTime requestTime, 
                   int duration, String specialty, String contact) {
        this.id = id;
        this.name = name;
        this.urgencyLevel = urgencyLevel;
        this.requestTime = requestTime;
        this.estimatedDuration = duration;
        this.requiredSpecialty = specialty;
        this.contact = contact;
    }
    
    public String getUrgencyText() {
        switch(urgencyLevel) {
            case 1: return "Emergency";
            case 2: return "Urgent";
            case 3: return "Semi-Urgent";
            case 4: return "Routine";
            default: return "Unknown";
        }
    }
    
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return id + "|" + name + "|" + urgencyLevel + "|" + requestTime.format(formatter) + 
               "|" + estimatedDuration + "|" + requiredSpecialty + "|" + contact;
    }
    
    public static Patient fromFileString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 7) return null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new Patient(
            parts[0],
            parts[1],
            Integer.parseInt(parts[2]),
            LocalDateTime.parse(parts[3], formatter),
            Integer.parseInt(parts[4]),
            parts[5],
            parts[6]
        );
    }
}