Priority Queue + Greedy Algorithm:
A Hybrid Approach to Hospital Appointment Scheduler

Hospital Appointment Scheduling System (HAS)
Project Description
A Java Swing-based desktop application for managing hospital appointments using a priority queue scheduling algorithm. The system efficiently schedules patient appointments with doctors based on urgency levels, specialty matching, and doctor availability.
Key Features

Patient Management: Create, update, delete, and search patients with urgency levels (Emergency, Urgent, Semi-Urgent, Routine)
Doctor Management: Manage doctors with specialties and multiple time slots
Smart Scheduling: Priority queue algorithm that:

Prioritizes patients by urgency level
Matches patients with doctors by specialty
Finds earliest available time slots
Automatically handles slot fragmentation

File Persistence: Auto-save and load patients/doctors from text files
Performance Analytics: View time and space complexity statistics
Search & Export: Search functionality and text export of schedules

Algorithm Complexity

Time Complexity: O(n × m) where n = patients, m = doctors
Space Complexity: O(n + m + k) where k = appointments

Team Members: 
Nathan Mikhael Beniga
Raphael Omar Espina
King Ruzzel A. Liquin
John Cameron B. Quiamco
Zandrah Nathalie L.Vadil

Instructions on how to run the project:

Getting Started

Launch the Application

Run HAS.java to start the system
The main window will open with three tabs: Patients, Doctors, and Schedule



Step 1: Add Doctors

Click on the "Doctors" tab
Fill in the doctor information:

Name: Enter doctor's full name
Specialty: Select from dropdown (Cardiology, Orthopedics, Dermatology, Neurology, Pediatrics)
Available From: Set start time of availability (date and time)
Available To: Set end time of availability

Click "Create" button to add the doctor
The doctor appears in the table above
Repeat to add multiple doctors

Doctor Management Options:

Update: Click a doctor in the table, modify fields, click "Update" to save changes
Delete: Select a doctor in the table, click "Delete" to remove
Search: Click "Search" to find doctors by name or ID
Clear: Reset all input fields

Step 2: Add Patients

Click on the "Patients" tab
Fill in the patient information:

Name: Enter patient's full name
Contact: Enter phone/email
Urgency: Select level (Emergency, Urgent, Semi-Urgent, Routine)
Specialty: Choose required medical specialty
Duration: Set appointment duration in minutes (15-120 min)

Click "Create" button to add the patient
Patient ID is auto-generated (P001, P002, etc.)
The patient appears in the table above

Patient Management Options:

Update: Click a patient in the table, modify fields, click "Update" to save changes
Delete: Select a patient in the table, click "Delete" to remove
Search: Click "Search" to find patients by name or ID
Clear: Reset all input fields

Step 3: Generate Schedule

Click on the "Schedule" tab
Click "Generate Schedule" button
The algorithm will:

Sort patients by urgency (Emergency patients first)
Match each patient with available doctors by specialty
Assign earliest available time slots

View the scheduled appointments in the table
A success dialog shows number of appointments created
Click "Show Stats" to view performance metrics:

Time Complexity: Execution time and performance analysis
Space Complexity: Memory usage and data structure sizes

Schedule Options:

Clear Schedule: Remove all appointments from the current schedule
Export to Text: View/copy the schedule in text format
Search Doctor: View all appointments for a specific doctor with patient details

Tips for Best Results:

Add doctors first with their available time slots before adding patients
Match specialties: Ensure doctors have specialties that match patient needs
Set realistic time slots: Make sure doctor availability covers the needed appointment durations
Use urgency levels wisely: Emergency patients will always be scheduled first
Search functionality: Use search to quickly locate specific patients or doctors
Review doctor schedules: Use "Search Doctor" in Schedule tab to see individual doctor workloads
