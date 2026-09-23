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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;

public class MainFrame extends JFrame {
    private static final String TITLE = "Hospital Management System";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final LoginForm loginForm;
    private JPanel currentDashboard;

    public MainFrame() {
        super(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 720);
        setLocationRelativeTo(null);

        loginForm = new LoginForm(this);
        cardPanel.add(loginForm, "LOGIN");
        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "LOGIN");
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

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        topBar.add(new JLabel("Logged in as " + user.getFullName() + " (" + user.getRole() + ")"), BorderLayout.WEST);
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topBar.add(logoutButton, BorderLayout.EAST);

        currentDashboard = new JPanel(new BorderLayout());
        currentDashboard.add(topBar, BorderLayout.NORTH);
        currentDashboard.add(dashboard, BorderLayout.CENTER);

        cardPanel.add(currentDashboard, "DASHBOARD");
        cardLayout.show(cardPanel, "DASHBOARD");
        setTitle(TITLE + " - " + user.getFullName() + " (" + user.getRole() + ")");
    }

    public void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        if (currentDashboard != null) {
            cardPanel.remove(currentDashboard);
            currentDashboard = null;
        }
        loginForm.clearFields();
        cardLayout.show(cardPanel, "LOGIN");
        setTitle(TITLE);
    }
}
