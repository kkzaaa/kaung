package hms.gui.doctor;

import hms.gui.UITheme;
import hms.model.ClinicalFeedback;
import hms.model.Doctor;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.Vector;

public class CompleteAppointmentPanel extends JPanel {
    private final DoctorService doctorService = new DoctorService();
    private final Doctor doctor;

    private final JComboBox<String> apptBox = new JComboBox<>();
    private final JTextArea feedbackArea = new JTextArea(5, 30);

    public CompleteAppointmentPanel(Doctor doctor) {
        this.doctor = doctor;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Appointment"), gbc);
        gbc.gridx = 1;
        add(apptBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Clinical feedback for patient"), gbc);
        gbc.gridx = 1;
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        add(new JScrollPane(feedbackArea), gbc);

        JButton refreshButton = new JButton("Refresh");
        JButton completeButton = UITheme.primaryButton("Mark completed & send feedback");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(refreshButton, gbc);
        gbc.gridx = 1;
        add(completeButton, gbc);

        refreshButton.addActionListener(e -> loadAppointments());
        completeButton.addActionListener(e -> completeAppointment());

        loadAppointments();
    }

    private void loadAppointments() {
        Vector<String> items = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            boolean ownedByDoctor = row[2].equals(doctor.getDoctorID());
            boolean open = row[6].equals(Constants.STATUS_BOOKED) || row[6].equals(Constants.STATUS_RESCHEDULED);
            if (ownedByDoctor && open) {
                items.add(row[0] + " - patient " + row[1] + " - " + row[3] + " " + row[4]);
            }
        }
        apptBox.setModel(new DefaultComboBoxModel<>(items));
    }

    private void completeAppointment() {
        String selected = (String) apptBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "No open appointments to complete.");
            return;
        }
        String comment = feedbackArea.getText().trim();
        if (comment.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please write clinical feedback for the patient.");
            return;
        }

        String apptID = selected.split(" - ")[0];
        String patientID = "";
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (row[0].equals(apptID)) {
                patientID = row[1];
                break;
            }
        }

        doctorService.completeAppointment(apptID);
        String feedbackID = IdGenerator.nextId(Constants.CLINICAL_FEEDBACK_FILE, "CF");
        doctorService.giveClinicalFeedback(new ClinicalFeedback(feedbackID, apptID, doctor.getDoctorID(),
                patientID, comment, LocalDate.now().toString()));

        JOptionPane.showMessageDialog(this, "Appointment " + apptID + " marked COMPLETED.");
        feedbackArea.setText("");
        loadAppointments();
    }
}
