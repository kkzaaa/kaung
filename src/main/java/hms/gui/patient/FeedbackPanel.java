package hms.gui.patient;

import hms.gui.UITheme;
import hms.model.Feedback;
import hms.model.Patient;
import hms.service.PatientService;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.IdGenerator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.Vector;

public class FeedbackPanel extends JPanel {
    private final PatientService patientService = new PatientService();
    private final Patient patient;

    private final JComboBox<String> apptBox = new JComboBox<>();
    private final JComboBox<Integer> ratingBox = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
    private final JTextArea commentArea = new JTextArea(4, 20);

    public FeedbackPanel(Patient patient) {
        this.patient = patient;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Completed appointment"), gbc);
        gbc.gridx = 1;
        add(apptBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Rating"), gbc);
        gbc.gridx = 1;
        add(ratingBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Comment"), gbc);
        gbc.gridx = 1;
        add(new JScrollPane(commentArea), gbc);

        JButton refreshButton = new JButton("Refresh");
        JButton submitButton = UITheme.primaryButton("Submit feedback");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(refreshButton, gbc);
        gbc.gridx = 1;
        add(submitButton, gbc);

        refreshButton.addActionListener(e -> loadEligibleAppointments());
        submitButton.addActionListener(e -> submitFeedback());

        loadEligibleAppointments();
    }

    private void loadEligibleAppointments() {
        Vector<String> apptIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            boolean ownedByPatient = row[1].equals(patient.getPatientID());
            boolean completed = row[6].equals(Constants.STATUS_COMPLETED);
            if (ownedByPatient && completed && !alreadyReviewed(row[0])) {
                apptIDs.add(row[0]);
            }
        }
        apptBox.setModel(new DefaultComboBoxModel<>(apptIDs));
    }

    private boolean alreadyReviewed(String apptID) {
        for (String[] row : FileHandler.readRecords(Constants.FEEDBACK_FILE)) {
            if (row[3].equals(apptID)) {
                return true;
            }
        }
        return false;
    }

    private void submitFeedback() {
        String apptID = (String) apptBox.getSelectedItem();
        if (apptID == null) {
            JOptionPane.showMessageDialog(this, "No completed appointment available for feedback.");
            return;
        }
        String doctorID = findDoctorForAppointment(apptID);
        int rating = (int) ratingBox.getSelectedItem();
        String comment = commentArea.getText().trim();

        String feedbackID = IdGenerator.nextId(Constants.FEEDBACK_FILE, "F");
        Feedback feedback = new Feedback(feedbackID, patient.getPatientID(), doctorID, apptID, rating, comment,
                LocalDate.now().toString());
        patientService.submitFeedback(feedback);

        JOptionPane.showMessageDialog(this, "Feedback submitted. Thank you!");
        commentArea.setText("");
        loadEligibleAppointments();
    }

    private String findDoctorForAppointment(String apptID) {
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (row[0].equals(apptID)) {
                return row[2];
            }
        }
        return "";
    }
}
