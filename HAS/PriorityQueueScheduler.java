package cce105f;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

class PriorityQueueScheduler {
    
    public List<Appointment> schedule(List<Patient> patients, List<Doctor> doctors) {
        List<Appointment> appointments = new ArrayList<>();
        
        // Create working copies of doctors to preserve original availability
        List<Doctor> workingDoctors = new ArrayList<>();
        for (Doctor d : doctors) {
            workingDoctors.add(d.deepCopy());
        }
        
        // Sort patients by urgency (lower number = higher priority), then by request time
        PriorityQueue<Patient> patientQueue = new PriorityQueue<>(
            Comparator.comparingInt((Patient p) -> p.urgencyLevel)
                     .thenComparing(p -> p.requestTime)
        );
        patientQueue.addAll(patients);
        
        // Process each patient in priority order
        while (!patientQueue.isEmpty()) {
            Patient patient = patientQueue.poll();
            
            // Find earliest available doctor with matching specialty
            Appointment appointment = findEarliestSlot(patient, workingDoctors);
            
            if (appointment != null) {
                appointments.add(appointment);
                // Mark slot as used
                removeUsedSlot(appointment, workingDoctors);
            }
        }
        
        return appointments;
    }
    
    private Appointment findEarliestSlot(Patient patient, List<Doctor> doctors) {
        Appointment earliest = null;
        LocalDateTime earliestTime = null;
        
        for (Doctor doctor : doctors) {
            if (!doctor.specialty.equals(patient.requiredSpecialty)) continue;
            
            for (TimeSlot slot : doctor.availableSlots) {
                if (slot.canFit(patient.estimatedDuration)) {
                    if (earliestTime == null || slot.start.isBefore(earliestTime)) {
                        earliestTime = slot.start;
                        earliest = new Appointment(patient, doctor, slot.start);
                    }
                }
            }
        }
        
        return earliest;
    }
    
    private void removeUsedSlot(Appointment apt, List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            if (doctor.id.equals(apt.doctor.id)) {
                Iterator<TimeSlot> iterator = doctor.availableSlots.iterator();
                while (iterator.hasNext()) {
                    TimeSlot slot = iterator.next();
                    if (slot.start.equals(apt.scheduledTime)) {
                        if (slot.end.isAfter(apt.endTime)) {
                            slot.start = apt.endTime;
                        } else {
                            iterator.remove();
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
}