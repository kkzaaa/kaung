package hms.gui.patient;

import hms.model.Patient;
import hms.service.PatientService;
import hms.util.Constants;
import hms.util.FileHandler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class MedicalHistoryPanel extends JPanel {
    private final PatientService patientService = new PatientService();
    private final Patient patient;

    private final DefaultTableModel assessmentsModel = new DefaultTableModel(
            new String[] { "Assessment ID", "Doctor ID", "Vitals", "Lab results", "Notes", "Date" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel prescriptionsModel = new DefaultTableModel(
            new String[] { "Rx ID", "Doctor ID", "Medication", "Dosage", "Instructions", "Date" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public MedicalHistoryPanel(Patient patient) {
        this.patient = patient;
        setLayout(new GridLayout(2, 1, 5, 5));

        JPanel assessmentsPanel = new JPanel(new BorderLayout());
        assessmentsPanel.add(new JLabel("Assessment history"), BorderLayout.NORTH);
        assessmentsPanel.add(new JScrollPane(new JTable(assessmentsModel)), BorderLayout.CENTER);

        JPanel prescriptionsPanel = new JPanel(new BorderLayout());
        prescriptionsPanel.add(new JLabel("Prescriptions"), BorderLayout.NORTH);
        prescriptionsPanel.add(new JScrollPane(new JTable(prescriptionsModel)), BorderLayout.CENTER);
        JButton refreshButton = new JButton("Refresh");
        prescriptionsPanel.add(refreshButton, BorderLayout.SOUTH);

        add(assessmentsPanel);
        add(prescriptionsPanel);

        refreshButton.addActionListener(e -> loadHistory());
        loadHistory();
    }

    private void loadHistory() {
        assessmentsModel.setRowCount(0);
        for (String[] row : patientService.getMedicalHistory(patient.getPatientID())) {
            assessmentsModel.addRow(new Object[] { row[0], row[3], row[5], row[6], row[7], row[8] });
        }

        prescriptionsModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.PRESCRIPTIONS_FILE)) {
            if (row[2].equals(patient.getPatientID())) {
                prescriptionsModel.addRow(new Object[] { row[0], row[3], row[4], row[5], row[6], row[7] });
            }
        }
    }
}
