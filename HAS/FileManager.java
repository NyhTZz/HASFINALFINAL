package cce105f;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class FileManager {
    public static final String PATIENTS_FILE = "patients.txt";
    public static final String DOCTORS_FILE = "doctors.txt";
    public static final String LOCATION = java.nio.file.Paths.get("").toAbsolutePath().toString();
    
    // Save patients to file
    public static void savePatients(List<Patient> patients) throws IOException {
        File file = new File(PATIENTS_FILE);
        System.out.println("Saving patients to: " + file.getAbsolutePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {          
            
            for (Patient patient : patients) {
                writer.write(patient.toFileString());
                writer.newLine();
            }
        }
    }
    
    // Load patients from file
    public static List<Patient> loadPatients() throws IOException {
        List<Patient> patients = new ArrayList<>();
        File file = new File(PATIENTS_FILE);
        
        if (!file.exists()) {
            return patients;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                Patient patient = Patient.fromFileString(line);
                if (patient != null) {
                    patients.add(patient);
                }
            }
        }
        
        return patients;
    }
    
    // Save doctors to file
    public static void saveDoctors(List<Doctor> doctors) throws IOException {
        File file = new File(DOCTORS_FILE);
        System.out.println("Saving doctors to: " + file.getAbsolutePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Doctor doctor : doctors) {
                writer.write(doctor.toFileString());
                writer.newLine();
            }
        }
    }
    
    // Load doctors from file
    public static List<Doctor> loadDoctors() throws IOException {
        List<Doctor> doctors = new ArrayList<>();
        File file = new File(DOCTORS_FILE);
        
        if (!file.exists()) {
            return doctors;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                
                Doctor doctor = Doctor.fromFileString(line);
                if (doctor != null) {
                    doctors.add(doctor);
                }
            }
        }
        
        return doctors;
    }
    
    
   
}