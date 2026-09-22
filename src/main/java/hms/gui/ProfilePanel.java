package hms.gui;

import hms.model.User;
import hms.service.UserManager;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ProfilePanel extends JPanel {
    private final User user;
    private final UserManager userManager = new UserManager();
    private final JTextField fullNameField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JTextField passwordField = new JTextField(20);

    public ProfilePanel(User user) {
        this.user = user;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        fullNameField.setText(user.getFullName());
        contactField.setText(user.getContactNo());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword());

        addRow(gbc, 0, "Full name", fullNameField);
        addRow(gbc, 1, "Contact no.", contactField);
        addRow(gbc, 2, "Email", emailField);
        addRow(gbc, 3, "Password", passwordField);

        JButton saveButton = new JButton("Save changes");
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(saveButton, gbc);

        saveButton.addActionListener(e -> save());
    }

    private void addRow(GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(field, gbc);
    }

    private void save() {
        if (fullNameField.getText().isBlank() || emailField.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Full name and email cannot be empty.");
            return;
        }
        user.setFullName(fullNameField.getText());
        user.setContactNo(contactField.getText());
        user.setEmail(emailField.getText());
        user.setPassword(passwordField.getText());
        userManager.updateUser(user);
        JOptionPane.showMessageDialog(this, "Profile updated.");
    }
}
