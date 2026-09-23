package hms.gui.doctor;

import hms.gui.UITheme;
import hms.model.Doctor;
import hms.model.Prescription;
import hms.service.DoctorService;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.IdGenerator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.Vector;

public class PrescriptionPanel extends JPanel {
    private final DoctorService doctorService = new DoctorService();
    private final Doctor doctor;

    private final JComboBox<String> assessmentBox = new JComboBox<>();
    private final JTextField medicationField = new JTextField(20);
    private final JTextField dosageField = new JTextField(20);
    private final JTextField instructionsField = new JTextField(20);

    public PrescriptionPanel(Doctor doctor) {
        this.doctor = doctor;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Assessment"), gbc);
        gbc.gridx = 1;
        add(assessmentBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Medication"), gbc);
        gbc.gridx = 1;
        add(medicationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Dosage"), gbc);
        gbc.gridx = 1;
        add(dosageField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Instructions"), gbc);
        gbc.gridx = 1;
        add(instructionsField, gbc);

        JButton refreshButton = new JButton("Refresh assessments");
        JButton issueButton = UITheme.primaryButton("Issue prescription");
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(refreshButton, gbc);
        gbc.gridx = 1;
        add(issueButton, gbc);

        refreshButton.addActionListener(e -> loadAssessments());
        issueButton.addActionListener(e -> issuePrescription());

        loadAssessments();
    }

    private void loadAssessments() {
        Vector<String> assessmentIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.ASSESSMENTS_FILE)) {
            if (row[3].equals(doctor.getDoctorID())) {
                assessmentIDs.add(row[0]);
            }
        }
        assessmentBox.setModel(new DefaultComboBoxModel<>(assessmentIDs));
    }

    private void issuePrescription() {
        String assessmentID = (String) assessmentBox.getSelectedItem();
        String medication = medicationField.getText().trim();
        String dosage = dosageField.getText().trim();
        String instructions = instructionsField.getText().trim();

        if (assessmentID == null) {
            JOptionPane.showMessageDialog(this, "No assessment available to prescribe against.");
            return;
        }
        if (medication.isBlank() || dosage.isBlank()) {
            JOptionPane.showMessageDialog(this, "Medication and dosage cannot be empty.");
            return;
        }

        String patientID = findPatientForAssessment(assessmentID);
        String rxID = IdGenerator.nextId(Constants.PRESCRIPTIONS_FILE, "RX");
        Prescription prescription = new Prescription(rxID, assessmentID, patientID, doctor.getDoctorID(),
                medication, dosage, instructions, LocalDate.now().toString());
        doctorService.issuePrescription(prescription);

        JOptionPane.showMessageDialog(this, "Prescription " + rxID + " issued.");
        medicationField.setText("");
        dosageField.setText("");
        instructionsField.setText("");
    }

    private String findPatientForAssessment(String assessmentID) {
        for (String[] row : FileHandler.readRecords(Constants.ASSESSMENTS_FILE)) {
            if (row[0].equals(assessmentID)) {
                return row[4];
            }
        }
        return "";
    }
}
