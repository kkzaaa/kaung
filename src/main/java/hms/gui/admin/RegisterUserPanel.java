package hms.gui.admin;

import hms.gui.UITheme;
import hms.model.Admin;
import hms.model.Doctor;
import hms.model.MedicalManager;
import hms.model.Patient;
import hms.model.User;
import hms.service.UserManager;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.IdGenerator;
import hms.util.Validator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class RegisterUserPanel extends JPanel {
    private final UserManager userManager = new UserManager();

    private final JTextField usernameField = new JTextField(18);
    private final JTextField passwordField = new JTextField(18);
    private final JTextField fullNameField = new JTextField(18);
    private final JTextField icField = new JTextField(18);
    private final JTextField contactField = new JTextField(18);
    private final JTextField emailField = new JTextField(18);
    private final JComboBox<String> roleBox = new JComboBox<>(
            new String[] { Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_DOCTOR, Constants.ROLE_PATIENT });

    private final CardLayout extraLayout = new CardLayout();
    private final JPanel extraPanel = new JPanel(extraLayout);

    private final JTextField managerDeptField = new JTextField(18);
    private final JTextField specialtyField = new JTextField(18);
    private final JTextField doctorDeptField = new JTextField(18);
    private final JTextField doctorManagerField = new JTextField(18);
    private final JTextField shiftField = new JTextField(18);
    private final JTextField dobField = new JTextField(18);
    private final JTextField genderField = new JTextField(18);
    private final JTextField bloodTypeField = new JTextField(18);
    private final JTextField addressField = new JTextField(18);

    public RegisterUserPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addRow(gbc, 0, "Role", roleBox);
        addRow(gbc, 1, "Username", usernameField);
        addRow(gbc, 2, "Password", passwordField);
        addRow(gbc, 3, "Full name", fullNameField);
        addRow(gbc, 4, "IC number", icField);
        addRow(gbc, 5, "Contact no.", contactField);
        addRow(gbc, 6, "Email", emailField);

        buildExtraPanels();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(extraPanel, gbc);
        gbc.gridwidth = 1;

        JButton registerButton = UITheme.primaryButton("Register user");
        gbc.gridx = 1;
        gbc.gridy = 8;
        add(registerButton, gbc);

        roleBox.addActionListener(e -> extraLayout.show(extraPanel, (String) roleBox.getSelectedItem()));
        registerButton.addActionListener(e -> register());
    }

    private void buildExtraPanels() {
        extraPanel.add(new JPanel(), Constants.ROLE_ADMIN);

        JPanel managerExtra = new JPanel(new GridBagLayout());
        addField(managerExtra, "Department ID", managerDeptField);
        extraPanel.add(managerExtra, Constants.ROLE_MANAGER);

        JPanel doctorExtra = new JPanel(new GridBagLayout());
        addField(doctorExtra, "Specialty", specialtyField);
        addField(doctorExtra, "Department ID", doctorDeptField);
        addField(doctorExtra, "Manager ID", doctorManagerField);
        addField(doctorExtra, "Shift schedule", shiftField);
        extraPanel.add(doctorExtra, Constants.ROLE_DOCTOR);

        JPanel patientExtra = new JPanel(new GridBagLayout());
        addField(patientExtra, "Date of birth", dobField);
        addField(patientExtra, "Gender", genderField);
        addField(patientExtra, "Blood type", bloodTypeField);
        addField(patientExtra, "Address", addressField);
        extraPanel.add(patientExtra, Constants.ROLE_PATIENT);
    }

    private void addField(JPanel panel, String label, JTextField field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = panel.getComponentCount() / 2;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void addRow(GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(field, gbc);
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String ic = icField.getText().trim();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleBox.getSelectedItem();

        if (username.isBlank() || password.isBlank() || fullName.isBlank() || email.isBlank()
                || ic.isBlank() || contact.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
            return;
        }
        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format (e.g. name@hms.com).");
            return;
        }
        if (!Validator.isValidIC(ic)) {
            JOptionPane.showMessageDialog(this, "Invalid IC number format (e.g. 900101-01-1234).");
            return;
        }
        if (!Validator.isValidContact(contact)) {
            JOptionPane.showMessageDialog(this, "Invalid contact number (digits only, e.g. 0121234567).");
            return;
        }
        if (role.equals(Constants.ROLE_PATIENT) && !dobField.getText().isBlank()
                && !Validator.isValidDate(dobField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Date of birth must be YYYY-MM-DD.");
            return;
        }
        if (userManager.isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(this, "Username already taken.");
            return;
        }

        String userID = IdGenerator.nextId(Constants.USERS_FILE, "U");
        User user;
        switch (role) {
            case Constants.ROLE_ADMIN: {
                String adminID = IdGenerator.nextId(Constants.ADMINS_FILE, "AD");
                user = new Admin(userID, username, password, fullName, ic, contact, email, Constants.STATUS_ACTIVE, adminID);
                userManager.createUser(user);
                FileHandler.appendRecord(Constants.ADMINS_FILE, new String[] { adminID, userID });
                break;
            }
            case Constants.ROLE_MANAGER: {
                String managerID = IdGenerator.nextId(Constants.MANAGERS_FILE, "M");
                String deptID = managerDeptField.getText().trim();
                user = new MedicalManager(userID, username, password, fullName, ic, contact, email,
                        Constants.STATUS_ACTIVE, managerID, deptID);
                userManager.createUser(user);
                FileHandler.appendRecord(Constants.MANAGERS_FILE, new String[] { managerID, userID, deptID });
                break;
            }
            case Constants.ROLE_DOCTOR: {
                String doctorID = IdGenerator.nextId(Constants.DOCTORS_FILE, "D");
                String specialty = specialtyField.getText().trim();
                String deptID = doctorDeptField.getText().trim();
                String managerID = doctorManagerField.getText().trim();
                String shift = shiftField.getText().trim();
                user = new Doctor(userID, username, password, fullName, ic, contact, email, Constants.STATUS_ACTIVE,
                        doctorID, specialty, deptID, managerID, shift);
                userManager.createUser(user);
                FileHandler.appendRecord(Constants.DOCTORS_FILE,
                        new String[] { doctorID, userID, specialty, deptID, managerID, shift });
                break;
            }
            case Constants.ROLE_PATIENT: {
                String patientID = IdGenerator.nextId(Constants.PATIENTS_FILE, "P");
                String dob = dobField.getText().trim();
                String gender = genderField.getText().trim();
                String bloodType = bloodTypeField.getText().trim();
                String address = addressField.getText().trim();
                user = new Patient(userID, username, password, fullName, ic, contact, email, Constants.STATUS_ACTIVE,
                        patientID, dob, gender, bloodType, address);
                userManager.createUser(user);
                FileHandler.appendRecord(Constants.PATIENTS_FILE,
                        new String[] { patientID, userID, dob, gender, bloodType, address });
                break;
            }
            default:
                return;
        }

        JOptionPane.showMessageDialog(this, "Registered " + role + " with ID " + userID + ".");
        clearFields();
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        icField.setText("");
        contactField.setText("");
        emailField.setText("");
        managerDeptField.setText("");
        specialtyField.setText("");
        doctorDeptField.setText("");
        doctorManagerField.setText("");
        shiftField.setText("");
        dobField.setText("");
        genderField.setText("");
        bloodTypeField.setText("");
        addressField.setText("");
    }
}
