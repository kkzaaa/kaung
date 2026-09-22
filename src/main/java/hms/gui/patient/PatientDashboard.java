package hms.gui.patient;

import hms.gui.ProfilePanel;
import hms.model.Patient;

import javax.swing.JTabbedPane;

public class PatientDashboard extends JTabbedPane {

    public PatientDashboard(Patient patient) {
        addTab("Book Appointment", new BookAppointmentPanel(patient));
        addTab("My Appointments", new MyAppointmentsPanel(patient));
        addTab("Medical History", new MedicalHistoryPanel(patient));
        addTab("Feedback", new FeedbackPanel(patient));
        addTab("My Profile", new ProfilePanel(patient));
    }
}
