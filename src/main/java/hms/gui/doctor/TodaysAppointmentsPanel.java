package hms.gui.doctor;

import hms.gui.WrapLayout;
import hms.gui.UITheme;
import hms.model.Doctor;
import hms.service.DoctorService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;

public class TodaysAppointmentsPanel extends JPanel {
    private final DoctorService doctorService = new DoctorService();
    private final Doctor doctor;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Appt ID", "Patient ID", "Date", "Time", "Room", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField dateField = new JTextField(10);

    public TodaysAppointmentsPanel(Doctor doctor) {
        this.doctor = doctor;
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Date (YYYY-MM-DD)"));
        dateField.setText(LocalDate.now().toString());
        topPanel.add(dateField);
        JButton loadButton = UITheme.primaryButton("Load appointments");
        topPanel.add(loadButton);
        add(topPanel, BorderLayout.NORTH);

        loadButton.addActionListener(e -> loadAppointments());

        loadAppointments();
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        String date = dateField.getText().trim();
        for (String[] row : doctorService.getTodaysAppointments(doctor.getDoctorID(), date)) {
            tableModel.addRow(row);
        }
    }
}
