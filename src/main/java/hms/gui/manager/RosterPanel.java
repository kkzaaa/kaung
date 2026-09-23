package hms.gui.manager;

import hms.gui.WrapLayout;
import hms.gui.UITheme;
import hms.service.RosterManager;
import hms.util.Constants;
import hms.util.FileHandler;

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
import java.awt.GridLayout;

public class RosterPanel extends JPanel {
    private final RosterManager rosterManager = new RosterManager();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Doctor ID", "Specialty", "Department ID", "Shift schedule" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField doctorIdField = new JTextField(8);
    private final JTextField shiftField = new JTextField(20);
    private final JTextField conflictDateField = new JTextField(10);
    private final JTextField conflictTimeField = new JTextField(6);

    public RosterPanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel assignRow = new JPanel(new WrapLayout(FlowLayout.LEFT));
        assignRow.add(new JLabel("Doctor ID"));
        assignRow.add(doctorIdField);
        assignRow.add(new JLabel("Shift schedule (e.g. Mon-Fri 09:00-17:00)"));
        assignRow.add(shiftField);
        JButton assignButton = UITheme.primaryButton("Assign shift");
        assignRow.add(assignButton);

        JPanel conflictRow = new JPanel(new WrapLayout(FlowLayout.LEFT));
        conflictRow.add(new JLabel("Check conflict for the doctor above - Date"));
        conflictRow.add(conflictDateField);
        conflictRow.add(new JLabel("Time"));
        conflictRow.add(conflictTimeField);
        JButton checkButton = new JButton("Check conflict");
        conflictRow.add(checkButton);
        JButton refreshButton = new JButton("Refresh");
        conflictRow.add(refreshButton);

        JPanel formPanel = new JPanel(new GridLayout(2, 1));
        formPanel.add(assignRow);
        formPanel.add(conflictRow);
        add(formPanel, BorderLayout.SOUTH);

        assignButton.addActionListener(e -> assignShift());
        checkButton.addActionListener(e -> checkConflict());
        refreshButton.addActionListener(e -> loadRoster());

        loadRoster();
    }

    private void loadRoster() {
        tableModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            tableModel.addRow(new Object[] { row[0], row[2], row[3], row[5] });
        }
    }

    private void assignShift() {
        String doctorID = doctorIdField.getText().trim();
        String shift = shiftField.getText().trim();
        if (doctorID.isBlank() || shift.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a doctor ID and shift schedule.");
            return;
        }
        rosterManager.assignShift(doctorID, shift);
        loadRoster();
    }

    private void checkConflict() {
        String doctorID = doctorIdField.getText().trim();
        String date = conflictDateField.getText().trim();
        String time = conflictTimeField.getText().trim();
        if (doctorID.isBlank() || date.isBlank() || time.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter doctor ID, date and time to check.");
            return;
        }
        boolean conflict = rosterManager.checkConflict(doctorID, date, time);
        JOptionPane.showMessageDialog(this, conflict
                ? "Conflict: doctor " + doctorID + " already has a booking at that date/time."
                : "No conflict - slot is free.");
    }
}
