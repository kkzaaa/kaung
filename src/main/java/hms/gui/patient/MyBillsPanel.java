package hms.gui.patient;

import hms.gui.WrapLayout;
import hms.model.Patient;
import hms.service.BillingService;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class MyBillsPanel extends JPanel {
    private final BillingService billingService = new BillingService();
    private final Patient patient;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Bill ID", "Appt ID", "Amount", "Grade", "Insurance", "Payment status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public MyBillsPanel(Patient patient) {
        this.patient = patient;
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadBills());
        loadBills();
    }

    private void loadBills() {
        tableModel.setRowCount(0);
        for (String[] row : billingService.getBillsByPatient(patient.getPatientID())) {
            tableModel.addRow(new Object[] { row[0], row[2], row[3], row[4], row[5], row[6] });
        }
    }
}
