package hms.gui;

import hms.model.User;
import hms.service.AuthService;
import hms.util.Constants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class LoginForm extends JPanel {
    private final AuthService authService = new AuthService();
    private final JTextField usernameField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);
    private final JLabel errorLabel = new JLabel(" ");

    public LoginForm(MainFrame mainFrame) {
        setLayout(new GridLayout(1, 2));
        add(buildBrandPanel());
        add(buildFormPanel(mainFrame));
    }

    private JPanel buildBrandPanel() {
        JPanel brand = new JPanel(new GridBagLayout());
        brand.setBackground(UITheme.PRIMARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel logo = new JLabel("+");
        logo.setFont(UITheme.font(Font.BOLD, 72));
        logo.setForeground(Color.WHITE);
        gbc.gridy = 0;
        brand.add(logo, gbc);

        JLabel name = new JLabel("Hospital Management System");
        name.setFont(UITheme.font(Font.BOLD, 28));
        name.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(8, 0, 8, 0);
        brand.add(name, gbc);

        JLabel tagline = new JLabel("Appointments, records and billing in one place.");
        tagline.setFont(UITheme.font(Font.PLAIN, 16));
        tagline.setForeground(UITheme.PRIMARY_LIGHT);
        gbc.gridy = 2;
        brand.add(tagline, gbc);

        JLabel roles = new JLabel("Admin  |  Medical Manager  |  Doctor  |  Patient");
        roles.setFont(UITheme.font(Font.PLAIN, 13));
        roles.setForeground(UITheme.PRIMARY_LIGHT);
        gbc.gridy = 3;
        gbc.insets = new Insets(40, 0, 0, 0);
        brand.add(roles, gbc);
        return brand;
    }

    private JPanel buildFormPanel(MainFrame mainFrame) {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BACKGROUND);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(32, 36, 32, 36)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        card.add(UITheme.title("Sign in"), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 24, 0);
        card.add(UITheme.subtitle("Enter your username and password to continue."), gbc);

        gbc.insets = new Insets(0, 0, 6, 0);
        gbc.gridy = 2;
        card.add(fieldLabel("Username"), gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(usernameField, gbc);

        gbc.insets = new Insets(0, 0, 6, 0);
        gbc.gridy = 4;
        card.add(fieldLabel("Password"), gbc);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(passwordField, gbc);

        JButton loginButton = UITheme.primaryButton("Log in");
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 12, 0);
        card.add(loginButton, gbc);

        errorLabel.setForeground(UITheme.ERROR);
        gbc.gridy = 7;
        card.add(errorLabel, gbc);

        outer.add(card);

        loginButton.addActionListener(e -> {
            User user = authService.login(usernameField.getText(), new String(passwordField.getPassword()));
            if (user == null) {
                errorLabel.setText("Invalid username or password.");
                return;
            }
            if (Constants.STATUS_INACTIVE.equals(user.getStatus())) {
                errorLabel.setText("This account is inactive. Please contact the admin.");
                return;
            }
            errorLabel.setText(" ");
            mainFrame.openDashboardFor(user);
        });
        passwordField.addActionListener(e -> loginButton.doClick());
        return outer;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.font(Font.BOLD, 13));
        label.setForeground(UITheme.TEXT);
        return label;
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setText(" ");
    }
}
