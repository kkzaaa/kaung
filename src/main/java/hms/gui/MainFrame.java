package hms.gui;

import hms.model.User;

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

    public void showPanel(JPanel panel, String name) {
        cardPanel.add(panel, name);
        cardLayout.show(cardPanel, name);
    }

    public void openDashboardFor(User user) {
        user.showDashboard();
        // Each role's dashboard panel is added by that module under gui/<role>/
        // and swapped in here via showPanel(panel, user.getRole()).
    }
}
