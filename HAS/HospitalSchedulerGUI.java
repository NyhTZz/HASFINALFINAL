package cce105f;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class HospitalSchedulerGUI extends JFrame {
    private List<Patient> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
    private List<Appointment> currentAppointments = new ArrayList<>();
    private PriorityQueueScheduler scheduler = new PriorityQueueScheduler();
    
    private JTable patientTable, doctorTable, appointmentTable;
    private DefaultTableModel patientTableModel, doctorTableModel, appointmentTableModel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public HospitalSchedulerGUI() {
        setTitle("Hospital Appointment Scheduling System with File I/O");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        initializeComponents();
        loadDataFromFiles();
        
        setVisible(true);
    }
    
    private void initializeComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        addMenuItem(fileMenu, "Load Patients", e -> loadPatientsFromFile());
        addMenuItem(fileMenu, "Save Patients", e -> savePatientsToFile());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Load Doctors", e -> loadDoctorsFromFile());
        addMenuItem(fileMenu, "Save Doctors", e -> saveDoctorsToFile());
        fileMenu.addSeparator();
        
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Exit", e -> System.exit(0));
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patients", createPatientPanel());
        tabbedPane.addTab("Doctors", createDoctorPanel());
        tabbedPane.addTab("Schedule", createSchedulePanel());
        add(tabbedPane);
    }
    
    private void addMenuItem(JMenu menu, String name, java.awt.event.ActionListener action) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(action);
        menu.add(item);
    }
    
    private void loadDataFromFiles() {
        File pFile = new File(FileManager.PATIENTS_FILE);
        File dFile = new File(FileManager.DOCTORS_FILE);
        
        if (!pFile.exists() || !dFile.exists()) {
        	System.out.println("Data files not found!");
            return;
        }
        
        try {
            patients = FileManager.loadPatients();
            doctors = FileManager.loadDoctors();
            updatePatientTable();
            updateDoctorTable();
            JOptionPane.showMessageDialog(this, "Loaded " + patients.size() + " patients and " + doctors.size() + " doctors", "Data Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading files: " + e.getMessage(), "File Error", JOptionPane.WARNING_MESSAGE);
            
        }
    }
    
    private void loadPatientsFromFile() {
        try {
            patients = FileManager.loadPatients();
            updatePatientTable();
            JOptionPane.showMessageDialog(this, "Loaded " + patients.size() + " patients", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void savePatientsToFile() {
        try {
            FileManager.savePatients(patients);
            JOptionPane.showMessageDialog(this, "Saved " + patients.size() + " patients", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDoctorsFromFile() {
        try {
            doctors = FileManager.loadDoctors();
            updateDoctorTable();
            JOptionPane.showMessageDialog(this, "Loaded " + doctors.size() + " doctors", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveDoctorsToFile() {
        try {
            FileManager.saveDoctors(doctors);
            JOptionPane.showMessageDialog(this, "Saved " + doctors.size() + " doctors", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] cols = {"ID", "Name", "Urgency", "Contact", "Specialty", "Duration (min)", "Request Time"};
        patientTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; }};
        patientTable = new JTable(patientTableModel);
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Patient Management"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField idF = new JTextField(10), nameF = new JTextField(15), contactF = new JTextField(15);
        idF.setEditable(false);
        idF.setBackground(Color.LIGHT_GRAY);
        JComboBox<String> urgencyCB = new JComboBox<>(new String[]{"Emergency", "Urgent", "Semi-Urgent", "Routine"});
        JComboBox<String> specialtyCB = new JComboBox<>(new String[]{"Cardiology", "Orthopedics", "Dermatology", "Neurology", "Pediatrics"});
        JSpinner durationSP = new JSpinner(new SpinnerNumberModel(30, 15, 120, 15));
        
        addFormField(form, g, 0, "ID:", idF);
        addFormField(form, g, 1, "Name:", nameF);
        addFormField(form, g, 2, "Contact:", contactF);
        addFormField(form, g, 3, "Urgency:", urgencyCB);
        addFormField(form, g, 4, "Specialty:", specialtyCB);
        addFormField(form, g, 5, "Duration (min):", durationSP);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        btnPanel.add(createButton("Create", new Color(76, 175, 80), e -> {
            if (nameF.getText().trim().isEmpty() || contactF.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String id = "P" + String.format("%03d", patients.size() + 1);
            Patient p = new Patient(id, nameF.getText().trim(), urgencyCB.getSelectedIndex() + 1, 
                LocalDateTime.now(), (Integer)durationSP.getValue(), (String)specialtyCB.getSelectedItem(), contactF.getText().trim());
            patients.add(p);
            updatePatientTable();
            autoSave(true);
            clearFields(idF, nameF, contactF);
            JOptionPane.showMessageDialog(this, "Patient created! ID: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
        }));
        
        patientTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = patientTable.getSelectedRow();
                if (row >= 0) {
                    Patient p = patients.get(row);
                    idF.setText(p.id);
                    nameF.setText(p.name);
                    contactF.setText(p.contact);
                    urgencyCB.setSelectedIndex(p.urgencyLevel - 1);
                    specialtyCB.setSelectedItem(p.requiredSpecialty);
                    durationSP.setValue(p.estimatedDuration);
                }
            }
        });
        
        btnPanel.add(createButton("Update", new Color(255, 152, 0), e -> {
            String id = idF.getText().trim();
            if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Load a patient first!", "Error", JOptionPane.ERROR_MESSAGE); return; }
            int idx = findPatientIndex(id);
            if (idx >= 0) {
                Patient old = patients.get(idx);
                patients.set(idx, new Patient(id, nameF.getText().trim(), urgencyCB.getSelectedIndex() + 1, 
                    old.requestTime, (Integer)durationSP.getValue(), (String)specialtyCB.getSelectedItem(), contactF.getText().trim()));
                updatePatientTable();
                autoSave(true);
                clearFields(idF, nameF, contactF);
                JOptionPane.showMessageDialog(this, "Patient updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }));
        
        btnPanel.add(createButton("Delete", new Color(244, 67, 54), e -> {
            int row = patientTable.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Delete patient?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                patients.remove(row);
                updatePatientTable();
                autoSave(true);
                clearFields(idF, nameF, contactF);
                JOptionPane.showMessageDialog(this, "Patient deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }));
        
        btnPanel.add(createButton("Clear", null, e -> clearFields(idF, nameF, contactF)));
        btnPanel.add(createButton("Search", new Color(156, 39, 176),e -> {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter Patient Name or ID to search:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                searchTerm = searchTerm.trim().toLowerCase();
                boolean found = false;
                
                for (int i = 0; i < patients.size(); i++) {
                    Patient p = patients.get(i);
                    if (p.name.toLowerCase().contains(searchTerm) || p.id.toLowerCase().contains(searchTerm)) {
                        patientTable.setRowSelectionInterval(i, i);
                        patientTable.scrollRectToVisible(patientTable.getCellRect(i, 0, true));
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    JOptionPane.showMessageDialog(this, "Patient not found!", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }));
        
        g.gridx = 0; g.gridy = 6; g.gridwidth = 2;
        form.add(btnPanel, g);
        
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] cols = {"ID", "Name", "Specialty", "Available Slots"};
        doctorTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; }};
        doctorTable = new JTable(doctorTableModel);
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Doctor Management"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField idF = new JTextField(10), nameF = new JTextField(15);
        idF.setEditable(false);
        idF.setBackground(Color.LIGHT_GRAY);
        JComboBox<String> specialtyCB = new JComboBox<>(new String[]{"Cardiology", "Orthopedics", "Dermatology", "Neurology", "Pediatrics"});
        JSpinner startSP = new JSpinner(new SpinnerDateModel());
        JSpinner endSP = new JSpinner(new SpinnerDateModel());
        startSP.setEditor(new JSpinner.DateEditor(startSP, "yyyy-MM-dd HH:mm"));
        endSP.setEditor(new JSpinner.DateEditor(endSP, "yyyy-MM-dd HH:mm"));
        
        addFormField(form, g, 0, "ID:", idF);
        addFormField(form, g, 1, "Name:", nameF);
        addFormField(form, g, 2, "Specialty:", specialtyCB);
        addFormField(form, g, 3, "Available From:", startSP);
        addFormField(form, g, 4, "Available To:", endSP);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        btnPanel.add(createButton("Create", new Color(76, 175, 80), e -> {
            if (nameF.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Enter doctor name!", "Error", JOptionPane.ERROR_MESSAGE); return; }
            LocalDateTime start = toLocalDateTime((Date)startSP.getValue());
            LocalDateTime end = toLocalDateTime((Date)endSP.getValue());
            if (end.isBefore(start)) { JOptionPane.showMessageDialog(this, "End time must be after start!", "Error", JOptionPane.ERROR_MESSAGE); return; }
            
            String id = "D" + String.format("%03d", doctors.size() + 1);
            Doctor d = new Doctor(id, nameF.getText().trim(), (String)specialtyCB.getSelectedItem());
            d.addAvailableSlot(start, end);
            doctors.add(d);
            updateDoctorTable();
            autoSave(false);
            clearFields(idF, nameF);
            JOptionPane.showMessageDialog(this, "Doctor created! ID: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
        }));
        
        doctorTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = doctorTable.getSelectedRow();
                if (row >= 0) {
                    Doctor d = doctors.get(row);
                    idF.setText(d.id);
                    nameF.setText(d.name);
                    specialtyCB.setSelectedItem(d.specialty);
                    if (!d.availableSlots.isEmpty()) {
                        TimeSlot s = d.availableSlots.get(0);
                        startSP.setValue(toDate(s.start));
                        endSP.setValue(toDate(s.end));
                    }
                }
            }
        });
        
        btnPanel.add(createButton("Update", new Color(255, 152, 0), e -> {
            String id = idF.getText().trim();
            if (id.isEmpty()) { JOptionPane.showMessageDialog(this, "Load a doctor first!", "Error", JOptionPane.ERROR_MESSAGE); return; }
            int idx = findDoctorIndex(id);
            if (idx >= 0) {
                LocalDateTime start = toLocalDateTime((Date)startSP.getValue());
                LocalDateTime end = toLocalDateTime((Date)endSP.getValue());
                if (end.isBefore(start)) { JOptionPane.showMessageDialog(this, "End time must be after start!", "Error", JOptionPane.ERROR_MESSAGE); return; }
                
                // Create new doctor with new schedule (replaces old schedule)
                Doctor newDoc = new Doctor(id, nameF.getText().trim(), (String)specialtyCB.getSelectedItem());
                newDoc.addAvailableSlot(start, end);
                doctors.set(idx, newDoc);
                updateDoctorTable();
                autoSave(false);
                clearFields(idF, nameF);
                JOptionPane.showMessageDialog(this, "Doctor updated with new schedule!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }));
        
        btnPanel.add(createButton("Delete", new Color(244, 67, 54), e -> {
            int row = doctorTable.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Delete doctor?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                doctors.remove(row);
                updateDoctorTable();
                autoSave(false);
                clearFields(idF, nameF);
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }));
        
        btnPanel.add(createButton("Clear", null, e -> clearFields(idF, nameF)));
        btnPanel.add(createButton("Search", new Color(156, 39, 176), e -> {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter Doctor Name or ID to search:");
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                searchTerm = searchTerm.trim().toLowerCase();
                boolean found = false;
                
                for (int i = 0; i < doctors.size(); i++) {
                    Doctor d = doctors.get(i);
                    if (d.name.toLowerCase().contains(searchTerm) || d.id.toLowerCase().contains(searchTerm)) {
                        doctorTable.setRowSelectionInterval(i, i);
                        doctorTable.scrollRectToVisible(doctorTable.getCellRect(i, 0, true));
                        found = true;
                        break;
                    }
                }
                
                if (!found) {
                    JOptionPane.showMessageDialog(this, "Doctor not found!", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
                
            }
            
        }));
        
        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        form.add(btnPanel, g);
        
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);
        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] cols = {"Patient ID", "Patient Name", "Urgency", "Doctor", "Scheduled Time", "End Time", "Duration"};
        appointmentTableModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; }};
        appointmentTable = new JTable(appointmentTableModel);
        appointmentTable.setRowHeight(25);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.add(createButton("Generate Schedule", new Color(46, 125, 50), e -> generateSchedule()));
        btnPanel.add(createButton("Clear Schedule", null, e -> {
            if (currentAppointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments scheduled yet!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                appointmentTableModel.setRowCount(0);
                currentAppointments.clear();
                JOptionPane.showMessageDialog(this, "Schedule Cleared!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }));
        btnPanel.add(createButton("Export to Text", null, e -> exportSchedule()));
        btnPanel.add(createButton("Search Doctor", new Color(33, 150, 243), e -> searchDoctorAppointments()));
        
        
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createTitledBorder("Algorithm Info"));
        info.add(new JLabel("Algorithm: Priority Queue + Greedy Matching"));
        info.add(new JLabel("• Patients sorted by urgency (Emergency → Routine)"));
        info.add(new JLabel("• Finds earliest available doctor slot with specialty match"));
        
        JPanel top = new JPanel(new BorderLayout());
        top.add(info, BorderLayout.CENTER);
        top.add(btnPanel, BorderLayout.SOUTH);
        
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        return panel;
    }
    
    private void searchDoctorAppointments() {
        if (currentAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments scheduled yet!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String searchTerm = JOptionPane.showInputDialog(this, "Enter Doctor Name or ID to search:");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            searchTerm = searchTerm.trim().toLowerCase();
            List<Appointment> doctorAppointments = new ArrayList<>();
            String doctorName = "";
            
            for (Appointment a : currentAppointments) {
                if (a.doctor.name.toLowerCase().contains(searchTerm) || a.doctor.id.toLowerCase().contains(searchTerm)) {
                    doctorAppointments.add(a);
                    doctorName = a.doctor.name + " (" + a.doctor.id + ")";
                }
            }
            
            if (doctorAppointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments found for this doctor!", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== APPOINTMENTS FOR DR. ").append(doctorName).append(" ===\n\n");
            sb.append("Total Patients: ").append(doctorAppointments.size()).append("\n\n");
            
            for (int i = 0; i < doctorAppointments.size(); i++) {
                Appointment a = doctorAppointments.get(i);
                sb.append((i + 1)).append(". Patient: ").append(a.patient.name).append(" (").append(a.patient.id).append(")\n");
                sb.append("   Urgency: ").append(a.patient.getUrgencyText()).append("\n");
                sb.append("   Specialty: ").append(a.patient.requiredSpecialty).append("\n");
                sb.append("   Scheduled: ").append(a.scheduledTime.format(formatter)).append("\n");
                sb.append("   End Time: ").append(a.endTime.format(formatter)).append("\n");
                sb.append("   Duration: ").append(a.patient.estimatedDuration).append(" minutes\n");
                sb.append("   Contact: ").append(a.patient.contact).append("\n\n");
            }
            
            JTextArea ta = new JTextArea(sb.toString());
            ta.setEditable(false);
            ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(500, 400));
            JOptionPane.showMessageDialog(this, sp, "Doctor's Patient List", JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    private void addFormField(JPanel p, GridBagConstraints g, int row, String label, JComponent comp) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        p.add(comp, g);
    }
    
    private JButton createButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        if (bg != null) { btn.setBackground(bg); btn.setForeground(Color.BLACK); }
        btn.addActionListener(action);
        return btn;
    }
    
    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }
    
    private int findPatientIndex(String id) {
        for (int i = 0; i < patients.size(); i++) if (patients.get(i).id.equals(id)) return i;
        return -1;
    }
    
    private int findDoctorIndex(String id) {
        for (int i = 0; i < doctors.size(); i++) if (doctors.get(i).id.equals(id)) return i;
        return -1;
    }
    
    private LocalDateTime toLocalDateTime(Date d) {
        return LocalDateTime.ofInstant(d.toInstant(), java.time.ZoneId.systemDefault());
    }
    
    private Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
    
    private void autoSave(boolean isPatient) {
        try {
            if (isPatient) FileManager.savePatients(patients);
            else FileManager.saveDoctors(doctors);
        } catch (IOException e) { System.err.println("Auto-save failed: " + e.getMessage()); }
    }
    
    
    
    private void updatePatientTable() {
        patientTableModel.setRowCount(0);
        for (Patient p : patients) {
            patientTableModel.addRow(new Object[]{p.id, p.name, p.getUrgencyText(), p.contact, p.requiredSpecialty, p.estimatedDuration, p.requestTime.format(formatter)});
        }
    }
    
    private void updateDoctorTable() {
        doctorTableModel.setRowCount(0);
        for (Doctor d : doctors) {
            StringBuilder slots = new StringBuilder("<html>");
            for (int i = 0; i < d.availableSlots.size(); i++) {
                TimeSlot s = d.availableSlots.get(i);
                if (i > 0) slots.append("<br>");
                slots.append(s.start.format(formatter)).append(" - ").append(s.end.format(formatter));
            }
            slots.append("</html>");
            doctorTableModel.addRow(new Object[]{d.id, d.name, d.specialty, slots.toString()});
        }
    }
    
    private void generateSchedule() {
        if (patients.isEmpty() || doctors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Need patients and doctors!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        appointmentTableModel.setRowCount(0);
        int numPatients = patients.size();
        int numDoctors = doctors.size();
        int totalSlots = doctors.stream().mapToInt(d -> d.availableSlots.size()).sum();

        long startTime = System.nanoTime();
        currentAppointments = scheduler.schedule(patients, doctors);
        long endTime = System.nanoTime();

        long durationNano = endTime - startTime;
        double durationMs = durationNano / 1_000_000.0;
        double durationSec = durationNano / 1_000_000_000.0;

        // Keep console output for debugging
        System.out.println("========================================");
        System.out.println("SCHEDULING PERFORMANCE ANALYSIS");
        System.out.println("========================================");
        System.out.println("INPUT SIZE:");
        System.out.println(" Patients: " + numPatients);
        System.out.println(" Doctors: " + numDoctors);
        System.out.println(" Total Time Slots: " + totalSlots);
        System.out.println(" Appointments Created: " + currentAppointments.size());
        System.out.println();
        System.out.println("EXECUTION TIME:");
        System.out.println(" Nanoseconds: " + String.format("%,d", durationNano) + " ns");
        System.out.println(" Milliseconds: " + String.format("%.6f", durationMs) + " ms");
        System.out.println(" Seconds: " + String.format("%.9f", durationSec) + " s");
        System.out.println();
        System.out.println("PERFORMANCE METRICS:");
        System.out.println(" Time per patient: " + String.format("%.6f", durationMs / numPatients) + " ms");
        System.out.println(" Time per operation (n*m): " + String.format("%.6f", durationMs / (numPatients * numDoctors)) + " ms");
        System.out.println();
        System.out.println("========================================");
        
        if (currentAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments scheduled!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (Appointment a : currentAppointments) {
            appointmentTableModel.addRow(new Object[]{a.patient.id, a.patient.name, a.patient.getUrgencyText(),
                a.doctor.name + " (" + a.doctor.specialty + ")", a.scheduledTime.format(formatter),
                a.endTime.format(formatter), a.patient.estimatedDuration + " min"});
        }

        // Create custom dialog with "Show Stats" option
        String message = currentAppointments.size() + " appointments scheduled!";
        Object[] options = {"OK", "Show Stats"};
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            "Success",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        // If "Show Stats" was clicked (index 1)
        if (choice == 1) {
            showStatsMenu(numPatients, numDoctors, totalSlots, currentAppointments.size(), 
                         durationNano, durationMs, durationSec);
        }
    }

    private void showStatsMenu(int numPatients, int numDoctors, int totalSlots, 
                              int appointmentsCreated, long durationNano, 
                              double durationMs, double durationSec) {
        String combinedStats = buildCombinedStats(numPatients, numDoctors, totalSlots, 
                                                   appointmentsCreated, durationNano, 
                                                   durationMs, durationSec);
        
        // Display in dialog
        JOptionPane.showMessageDialog(this, combinedStats, "Performance Statistics", 
                                     JOptionPane.INFORMATION_MESSAGE);
        
        // Display in console
        System.out.println(combinedStats);
    }

    private String buildCombinedStats(int numPatients, int numDoctors, int totalSlots, 
                                     int appointmentsCreated, long durationNano, 
                                     double durationMs, double durationSec) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024; // KB
        
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("COMPLETE PERFORMANCE ANALYSIS\n");
        sb.append("========================================\n\n");
        
        // TIME COMPLEXITY SECTION
        sb.append("TIME COMPLEXITY ANALYSIS\n");
        sb.append("-------------------------------------\n");
        sb.append("INPUT SIZE:\n");
        sb.append("  Patients: ").append(numPatients).append("\n");
        sb.append("  Doctors: ").append(numDoctors).append("\n");
        sb.append("  Total Time Slots: ").append(totalSlots).append("\n");
        sb.append("  Appointments Created: ").append(appointmentsCreated).append("\n\n");
        sb.append("EXECUTION TIME:\n");
        sb.append("  Nanoseconds: ").append(String.format("%,d", durationNano)).append(" ns\n");
        sb.append("  Milliseconds: ").append(String.format("%.6f", durationMs)).append(" ms\n");
        sb.append("  Seconds: ").append(String.format("%.9f", durationSec)).append(" s\n\n");
        sb.append("PERFORMANCE METRICS:\n");
        sb.append("  Time per patient: ").append(String.format("%.6f", durationMs / numPatients)).append(" ms\n");
        sb.append("  Time per operation (n*m): ").append(String.format("%.6f", durationMs / (numPatients * numDoctors))).append(" ms\n");
        sb.append("\nCOMPLEXITY: O(n × m)\n");
        sb.append("  where n = patients, m = doctors\n\n");
        
        // SPACE COMPLEXITY SECTION
        sb.append("SPACE COMPLEXITY ANALYSIS\n");
        sb.append("-------------------------------------\n");
        sb.append("DATA STRUCTURES:\n");
        sb.append("  Patient List: ").append(numPatients).append(" objects\n");
        sb.append("  Doctor List: ").append(numDoctors).append(" objects\n");
        sb.append("  Appointments Created: ").append(appointmentsCreated).append(" objects\n\n");
        sb.append("MEMORY USAGE:\n");
        sb.append("  Current Memory: ").append(String.format("%,d", usedMemory)).append(" KB\n");
        sb.append("  Total Memory: ").append(String.format("%,d", runtime.totalMemory() / 1024)).append(" KB\n");
        sb.append("  Free Memory: ").append(String.format("%,d", runtime.freeMemory() / 1024)).append(" KB\n\n");
        sb.append("ESTIMATED SPACE:\n");
        sb.append("  Patient storage: O(n) = ").append(numPatients).append("\n");
        sb.append("  Doctor storage: O(m) = ").append(numDoctors).append("\n");
        sb.append("  Appointment storage: O(k) = ").append(appointmentsCreated).append("\n");
        sb.append("\nCOMPLEXITY: O(n + m + k)\n");
        sb.append("  where n = patients, m = doctors, k = appointments\n");
        sb.append("========================================\n");
        
        return sb.toString();
    }
    
    private void exportSchedule() {
        if (appointmentTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No schedule to export!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("=== HOSPITAL APPOINTMENT SCHEDULE ===\n\n");
        for (int i = 0; i < appointmentTableModel.getRowCount(); i++) {
            sb.append("Appointment ").append(i + 1).append(":\n");
            for (int j = 0; j < appointmentTableModel.getColumnCount(); j++) {
                sb.append("  ").append(appointmentTableModel.getColumnName(j)).append(": ")
                  .append(appointmentTableModel.getValueAt(i, j)).append("\n");
            }
            sb.append("\n");
        }
        
        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(500, 400));
        JOptionPane.showMessageDialog(this, sp, "Schedule Export", JOptionPane.PLAIN_MESSAGE);
    }
    
    
    
}
