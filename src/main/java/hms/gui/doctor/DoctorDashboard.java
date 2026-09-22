package hms.gui.doctor;

import hms.gui.ProfilePanel;
import hms.model.Doctor;

import javax.swing.JTabbedPane;

public class DoctorDashboard extends JTabbedPane {

    public DoctorDashboard(Doctor doctor) {
        addTab("Today's Appointments", new TodaysAppointmentsPanel(doctor));
        addTab("Record Assessment", new AssessmentEntryPanel(doctor));
        addTab("Issue Prescription", new PrescriptionPanel(doctor));
        addTab("Lab/X-Ray Request", new LabRequestPanel(doctor));
        addTab("My Profile", new ProfilePanel(doctor));
    }
}
