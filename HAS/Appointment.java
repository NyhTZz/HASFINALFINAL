package cce105f;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Appointment {
    Patient patient;
    Doctor doctor;
    LocalDateTime scheduledTime;
    LocalDateTime endTime;
    
    public Appointment(Patient patient, Doctor doctor, LocalDateTime scheduledTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.scheduledTime = scheduledTime;
        this.endTime = scheduledTime.plusMinutes(patient.estimatedDuration);
    }
    
    // Convert to file format
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return patient.id + "|" + patient.name + "|" + patient.getUrgencyText() + "|" +
               doctor.name + " (" + doctor.specialty + ")|" + 
               scheduledTime.format(formatter) + "|" + endTime.format(formatter) + "|" +
               patient.estimatedDuration + " min";
    }
}