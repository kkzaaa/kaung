package hms.gui.admin;

import hms.gui.WrapLayout;
import hms.gui.UITheme;
import hms.service.BillingService;
import hms.util.Constants;
import hms.util.FileHandler;

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
import java.util.Vector;

public class BillingPanel extends JPanel {
    private final BillingService billingService = new BillingService();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Bill ID", "Patient ID", "Appt ID", "Amount", "Grade", "Insurance", "Payment" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JComboBox<String> apptBox = new JComboBox<>();
    private final JComboBox<String> insuranceBox = new JComboBox<>(new String[] { "COVERED", "NOT_COVERED" });

    public BillingPanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Appointment"));
        formPanel.add(apptBox);
        formPanel.add(new JLabel("Insurance"));
        formPanel.add(insuranceBox);
        JButton generateButton = UITheme.primaryButton("Generate bill");
        JButton markPaidButton = new JButton("Mark selected as PAID");
        JButton refreshButton = new JButton("Refresh");
        formPanel.add(generateButton);
        formPanel.add(markPaidButton);
        formPanel.add(refreshButton);
        add(formPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generateBill());
        markPaidButton.addActionListener(e -> markPaid());
        refreshButton.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        Vector<String> apptIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (!alreadyBilled(row[0])) {
                apptIDs.add(row[0]);
            }
        }
        apptBox.setModel(new DefaultComboBoxModel<>(apptIDs));

        tableModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.BILLING_FILE)) {
            tableModel.addRow(row);
        }
    }

    private boolean alreadyBilled(String apptID) {
        for (String[] row : FileHandler.readRecords(Constants.BILLING_FILE)) {
            if (row[2].equals(apptID)) {
                return true;
            }
        }
        return false;
    }

    private void generateBill() {
        String apptID = (String) apptBox.getSelectedItem();
        if (apptID == null) {
            JOptionPane.showMessageDialog(this, "No unbilled appointment available.");
            return;
        }
        String patientID = null;
        String doctorID = null;
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (row[0].equals(apptID)) {
                patientID = row[1];
                doctorID = row[2];
                break;
            }
        }
        String deptID = findDeptForDoctor(doctorID);
        double amount = billingService.getRateForDept(deptID);
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "No consultation rate configured for department " + deptID
                    + ". Set one under Rates & Insurance first.");
            return;
        }
        String insuranceStatus = (String) insuranceBox.getSelectedItem();
        billingService.createBill(patientID, apptID, amount, insuranceStatus);
        loadData();
    }

    private String findDeptForDoctor(String doctorID) {
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            if (row[0].equals(doctorID)) {
                return row[3];
            }
        }
        return "";
    }

    private void markPaid() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a bill first.");
            return;
        }
        String billID = (String) tableModel.getValueAt(row, 0);
        billingService.updatePaymentStatus(billID, "PAID");
        loadData();
    }
}
