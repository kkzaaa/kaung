package hms.gui.doctor;

import hms.model.Doctor;
import hms.model.LabRequest;
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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.Vector;

public class LabRequestPanel extends JPanel {
    private final DoctorService doctorService = new DoctorService();
    private final Doctor doctor;

    private final JComboBox<String> patientBox = new JComboBox<>();
    private final JComboBox<String> testTypeBox = new JComboBox<>(
            new String[] { "Blood Test", "Urine Test", "X-Ray", "CT Scan", "MRI", "ECG" });

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Request ID", "Patient ID", "Test type", "Status", "Date" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public LabRequestPanel(Doctor doctor) {
        this.doctor = doctor;
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Patient"));
        formPanel.add(patientBox);
        formPanel.add(new JLabel("Test type"));
        formPanel.add(testTypeBox);
        JButton submitButton = new JButton("Submit request to Admin");
        JButton refreshButton = new JButton("Refresh");
        formPanel.add(submitButton);
        formPanel.add(refreshButton);
        add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JLabel("My lab/X-ray requests (status is updated by Admin)"), BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> submitRequest());
        refreshButton.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        Vector<String> patientIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.PATIENTS_FILE)) {
            patientIDs.add(row[0]);
        }
        patientBox.setModel(new DefaultComboBoxModel<>(patientIDs));

        tableModel.setRowCount(0);
        for (String[] row : doctorService.getLabRequestsByDoctor(doctor.getDoctorID())) {
            tableModel.addRow(new Object[] { row[0], row[2], row[3], row[4], row[5] });
        }
    }

    private void submitRequest() {
        String patientID = (String) patientBox.getSelectedItem();
        String testType = (String) testTypeBox.getSelectedItem();
        if (patientID == null) {
            JOptionPane.showMessageDialog(this, "No patients registered yet.");
            return;
        }
        String requestID = IdGenerator.nextId(Constants.LAB_REQUESTS_FILE, "LR");
        doctorService.requestLabTest(new LabRequest(requestID, doctor.getDoctorID(), patientID, testType,
                Constants.LAB_PENDING, LocalDate.now().toString()));

        JOptionPane.showMessageDialog(this, "Lab request " + requestID + " submitted to Admin.");
        loadData();
    }
}
