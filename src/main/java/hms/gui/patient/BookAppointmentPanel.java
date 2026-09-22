package hms.gui.patient;

import hms.model.Appointment;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

public class BookAppointmentPanel extends JPanel {
    private final PatientService patientService = new PatientService();
    private final Patient patient;

    private final JComboBox<String> doctorBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(10);
    private final JTextField timeField = new JTextField(6);
    private final JTextField roomField = new JTextField(8);

    private final DefaultTableModel scheduleModel = new DefaultTableModel(
            new String[] { "Appt ID", "Doctor ID", "Date", "Time", "Room", "Status" }, 0) {
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
        formPanel.add(new JLabel("Date (YYYY-MM-DD)"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Time (HH:MM)"), gbc);
        gbc.gridx = 1;
        formPanel.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Room ID"), gbc);
        gbc.gridx = 1;
        formPanel.add(roomField, gbc);

        JButton bookButton = new JButton("Book appointment");
        JButton refreshButton = new JButton("Refresh doctors/schedule");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(refreshButton, gbc);
        gbc.gridx = 1;
        formPanel.add(bookButton, gbc);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        bookButton.addActionListener(e -> bookAppointment());
        refreshButton.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        Vector<String> doctorIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            doctorIDs.add(row[0]);
        }
        doctorBox.setModel(new DefaultComboBoxModel<>(doctorIDs));

        scheduleModel.setRowCount(0);
        String selectedDoctor = (String) doctorBox.getSelectedItem();
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (selectedDoctor == null || row[2].equals(selectedDoctor)) {
                scheduleModel.addRow(new Object[] { row[0], row[2], row[3], row[4], row[5], row[6] });
            }
        }
    }

    private void bookAppointment() {
        String doctorID = (String) doctorBox.getSelectedItem();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String room = roomField.getText().trim();

        if (doctorID == null || date.isBlank() || time.isBlank()) {
            JOptionPane.showMessageDialog(this, "Select a doctor and enter date and time.");
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
        loadData();
    }
}
