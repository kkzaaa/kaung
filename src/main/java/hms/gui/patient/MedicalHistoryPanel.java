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

    private final DefaultTableModel assessmentsModel = readOnlyModel(
            "Assessment ID", "Doctor ID", "Vitals", "Lab results", "Notes", "Date");
    private final DefaultTableModel prescriptionsModel = readOnlyModel(
            "Rx ID", "Doctor ID", "Medication", "Dosage", "Instructions", "Date");
    private final DefaultTableModel clinicalFeedbackModel = readOnlyModel(
            "Appt ID", "Doctor ID", "Doctor's feedback", "Date");

    public MedicalHistoryPanel(Patient patient) {
        this.patient = patient;
        setLayout(new BorderLayout());

        JPanel tablesPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        tablesPanel.add(titledTable("Assessment history", assessmentsModel));
        tablesPanel.add(titledTable("Prescriptions", prescriptionsModel));
        tablesPanel.add(titledTable("Doctor's clinical feedback", clinicalFeedbackModel));
        add(tablesPanel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        add(refreshButton, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadHistory());
        loadHistory();
    }

    private static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel titledTable(String title, DefaultTableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(title), BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        return panel;
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

        clinicalFeedbackModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.CLINICAL_FEEDBACK_FILE)) {
            if (row[3].equals(patient.getPatientID())) {
                clinicalFeedbackModel.addRow(new Object[] { row[1], row[2], row[4], row[5] });
            }
        }
    }
}
