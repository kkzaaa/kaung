package hms.gui;

import hms.gui.admin.AdminDashboard;
import hms.gui.doctor.DoctorDashboard;
import hms.gui.manager.ManagerDashboard;
import hms.gui.patient.PatientDashboard;
import hms.model.Admin;
import hms.model.Doctor;
import hms.model.MedicalManager;
import hms.model.Patient;
import hms.model.User;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    public MainFrame() {
        super("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 720);
        setLocationRelativeTo(null);

        cardPanel.add(new LoginForm(this), "LOGIN");
        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "LOGIN");
    }

    public void showPanel(JComponent panel, String name) {
        cardPanel.add(panel, name);
        cardLayout.show(cardPanel, name);
    }

    public void openDashboardFor(User user) {
        user.showDashboard();
        JComponent dashboard;
        if (user instanceof Admin admin) {
            dashboard = new AdminDashboard(admin);
        } else if (user instanceof MedicalManager manager) {
            dashboard = new ManagerDashboard(manager);
        } else if (user instanceof Doctor doctor) {
            dashboard = new DoctorDashboard(doctor);
        } else if (user instanceof Patient patient) {
            dashboard = new PatientDashboard(patient);
        } else {
            return;
        }
        showPanel(dashboard, user.getRole());
        setTitle("Hospital Management System — " + user.getFullName() + " (" + user.getRole() + ")");
    }
}
