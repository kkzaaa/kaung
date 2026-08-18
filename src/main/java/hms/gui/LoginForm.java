package hms.gui;

import hms.model.User;
import hms.service.AuthService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginForm extends JPanel {
    private final AuthService authService = new AuthService();
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JLabel errorLabel = new JLabel(" ");

    public LoginForm(MainFrame mainFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Log in");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginButton, gbc);

        errorLabel.setForeground(java.awt.Color.RED);
        gbc.gridy = 3;
        add(errorLabel, gbc);

        loginButton.addActionListener(e -> {
            User user = authService.login(usernameField.getText(), new String(passwordField.getPassword()));
            if (user == null) {
                errorLabel.setText("Invalid username or password.");
                return;
            }
            errorLabel.setText(" ");
            mainFrame.openDashboardFor(user);
        });
    }
}
