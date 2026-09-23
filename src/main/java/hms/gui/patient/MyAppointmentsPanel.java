package hms.gui.patient;

import hms.gui.WrapLayout;
import hms.gui.UITheme;
import hms.model.Patient;
import hms.service.PatientService;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.Validator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class MyAppointmentsPanel extends JPanel {
    private final PatientService patientService = new PatientService();
    private final Patient patient;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Appt ID", "Doctor ID", "Date", "Time", "Room", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField newDateField = new JTextField(10);
    private final JTextField newTimeField = new JTextField(6);

    public MyAppointmentsPanel(Patient patient) {
        this.patient = patient;
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("New date"));
        formPanel.add(newDateField);
        formPanel.add(new JLabel("New time"));
        formPanel.add(newTimeField);
        JButton rescheduleButton = UITheme.primaryButton("Reschedule selected");
        JButton cancelButton = new JButton("Cancel selected");
        JButton refreshButton = new JButton("Refresh");
        formPanel.add(rescheduleButton);
        formPanel.add(cancelButton);
        formPanel.add(refreshButton);
        add(formPanel, BorderLayout.SOUTH);

        rescheduleButton.addActionListener(e -> reschedule());
        cancelButton.addActionListener(e -> cancel());
        refreshButton.addActionListener(e -> loadAppointments());

        loadAppointments();
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (row[1].equals(patient.getPatientID())) {
                tableModel.addRow(new Object[] { row[0], row[2], row[3], row[4], row[5], row[6] });
            }
        }
    }

    private String selectedApptID() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment first.");
            return null;
        }
        String status = (String) tableModel.getValueAt(row, 5);
        if (status.equals(Constants.STATUS_COMPLETED) || status.equals(Constants.STATUS_CANCELLED)) {
            JOptionPane.showMessageDialog(this, "You can only change upcoming appointments.");
            return null;
        }
        return (String) tableModel.getValueAt(row, 0);
    }

    private void reschedule() {
        String apptID = selectedApptID();
        if (apptID == null) {
            return;
        }
        String newDate = newDateField.getText().trim();
        String newTime = newTimeField.getText().trim();
        if (newDate.isBlank() || newTime.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a new date and time.");
            return;
        }
        if (!Validator.isValidDate(newDate) || !Validator.isValidTime(newTime)) {
            JOptionPane.showMessageDialog(this, "Use YYYY-MM-DD for date and HH:MM for time.");
            return;
        }
        if (Validator.isPastDate(newDate)) {
            JOptionPane.showMessageDialog(this, "You cannot reschedule to a past date.");
            return;
        }
        String doctorID = (String) tableModel.getValueAt(table.getSelectedRow(), 1);
        if (!patientService.isSlotAvailable(doctorID, newDate, newTime)) {
            JOptionPane.showMessageDialog(this, "The doctor is already booked at that time. Choose another slot.");
            return;
        }
        patientService.rescheduleAppointment(apptID, newDate, newTime);
        loadAppointments();
    }

    private void cancel() {
        String apptID = selectedApptID();
        if (apptID == null) {
            return;
        }
        patientService.cancelAppointment(apptID);
        loadAppointments();
    }
}
