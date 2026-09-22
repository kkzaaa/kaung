package hms.gui.doctor;

import hms.model.Doctor;
import hms.model.LabRequest;
import hms.service.DoctorService;
import hms.util.Constants;
import hms.util.IdGenerator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;

public class LabRequestPanel extends JPanel {
    private final DoctorService doctorService = new DoctorService();
    private final Doctor doctor;

    private final JTextField patientIdField = new JTextField(15);
    private final JTextField testTypeField = new JTextField(20);

    public LabRequestPanel(Doctor doctor) {
        this.doctor = doctor;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Patient ID"), gbc);
        gbc.gridx = 1;
        add(patientIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Test type (e.g. Blood Test, X-Ray)"), gbc);
        gbc.gridx = 1;
        add(testTypeField, gbc);

        JButton submitButton = new JButton("Submit request");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(submitButton, gbc);

        submitButton.addActionListener(e -> submitRequest());
    }

    private void submitRequest() {
        String patientID = patientIdField.getText().trim();
        String testType = testTypeField.getText().trim();
        if (patientID.isBlank() || testType.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter patient ID and test type.");
            return;
        }
        String requestID = IdGenerator.nextId(Constants.LAB_REQUESTS_FILE, "LR");
        LabRequest labRequest = new LabRequest(requestID, doctor.getDoctorID(), patientID, testType,
                "PENDING", LocalDate.now().toString());
        doctorService.requestLabTest(labRequest);

        JOptionPane.showMessageDialog(this, "Lab request " + requestID + " submitted to Admin.");
        patientIdField.setText("");
        testTypeField.setText("");
    }
}
