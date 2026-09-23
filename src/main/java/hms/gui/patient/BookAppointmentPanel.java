package hms.gui.patient;

import hms.gui.UITheme;
import hms.model.Appointment;
import hms.model.Patient;
import hms.service.PatientService;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.IdGenerator;
import hms.util.Validator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BookAppointmentPanel extends JPanel {
    private final PatientService patientService = new PatientService();
    private final Patient patient;

    private final JComboBox<String> doctorBox = new JComboBox<>();
    private final JLabel shiftLabel = new JLabel(" ");
    private final JTextField dateField = new JTextField(10);
    private final JTextField timeField = new JTextField(6);
    private final JTextField roomField = new JTextField(8);

    private final DefaultTableModel scheduleModel = new DefaultTableModel(
            new String[] { "Appt ID", "Date", "Time", "Room", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable scheduleTable = new JTable(scheduleModel);

    public BookAppointmentPanel(Patient patient) {
        this.patient = patient;
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Doctor"), gbc);
        gbc.gridx = 1;
        formPanel.add(doctorBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Working hours"), gbc);
        gbc.gridx = 1;
        formPanel.add(shiftLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date (YYYY-MM-DD)"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Time (HH:MM)"), gbc);
        gbc.gridx = 1;
        formPanel.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Room ID (optional)"), gbc);
        gbc.gridx = 1;
        formPanel.add(roomField, gbc);

        JButton bookButton = UITheme.primaryButton("Book appointment");
        JButton refreshButton = new JButton("Refresh");
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(refreshButton, gbc);
        gbc.gridx = 1;
        formPanel.add(bookButton, gbc);

        add(formPanel, BorderLayout.NORTH);

        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.add(new JLabel("Slots already taken for this doctor"), BorderLayout.NORTH);
        schedulePanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        add(schedulePanel, BorderLayout.CENTER);

        doctorBox.addActionListener(e -> loadSchedule());
        bookButton.addActionListener(e -> bookAppointment());
        refreshButton.addActionListener(e -> loadDoctors());

        loadDoctors();
    }

    private void loadDoctors() {
        Map<String, String> namesByUserID = new HashMap<>();
        for (String[] row : FileHandler.readRecords(Constants.USERS_FILE)) {
            namesByUserID.put(row[0], row[4]);
        }

        Vector<String> doctors = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            String name = namesByUserID.getOrDefault(row[1], "Unknown");
            doctors.add(row[0] + " - " + name + " (" + row[2] + ")");
        }
        doctorBox.setModel(new DefaultComboBoxModel<>(doctors));
        loadSchedule();
    }

    private String selectedDoctorID() {
        String selected = (String) doctorBox.getSelectedItem();
        return selected == null ? null : selected.split(" - ")[0];
    }

    private void loadSchedule() {
        scheduleModel.setRowCount(0);
        String doctorID = selectedDoctorID();
        shiftLabel.setText(" ");
        if (doctorID == null) {
            return;
        }
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            if (row[0].equals(doctorID)) {
                shiftLabel.setText(row[5].isBlank() ? "Not set" : row[5]);
            }
        }
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (row[2].equals(doctorID) && !row[6].equals(Constants.STATUS_CANCELLED)) {
                scheduleModel.addRow(new Object[] { row[0], row[3], row[4], row[5], row[6] });
            }
        }
    }

    private void bookAppointment() {
        String doctorID = selectedDoctorID();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String room = roomField.getText().trim();

        if (doctorID == null || date.isBlank() || time.isBlank()) {
            JOptionPane.showMessageDialog(this, "Select a doctor and enter date and time.");
            return;
        }
        if (!Validator.isValidDate(date)) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.");
            return;
        }
        if (Validator.isPastDate(date)) {
            JOptionPane.showMessageDialog(this, "You cannot book an appointment in the past.");
            return;
        }
        if (!Validator.isValidTime(time)) {
            JOptionPane.showMessageDialog(this, "Time must be in HH:MM format (e.g. 09:30).");
            return;
        }
        if (!patientService.isSlotAvailable(doctorID, date, time)) {
            JOptionPane.showMessageDialog(this, "That slot is already booked. Please choose another.");
            return;
        }

        String apptID = IdGenerator.nextId(Constants.APPOINTMENTS_FILE, "A");
        Appointment appointment = new Appointment(apptID, patient.getPatientID(), doctorID, date, time,
                room.isBlank() ? "R101" : room, Constants.STATUS_BOOKED);
        patientService.bookAppointment(appointment);

        JOptionPane.showMessageDialog(this, "Appointment " + apptID + " booked.");
        dateField.setText("");
        timeField.setText("");
        roomField.setText("");
        loadSchedule();
    }
}
