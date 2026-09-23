package hms.gui.manager;

import hms.gui.WrapLayout;
import hms.gui.UITheme;
import hms.service.ReportGenerator;
import hms.util.Constants;
import hms.util.FileHandler;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Map;

public class ReportsPanel extends JPanel {
    private final ReportGenerator reportGenerator = new ReportGenerator();
    private final JLabel revenueLabel = new JLabel("Total revenue: -");
    private final JComboBox<String> reportBox = new JComboBox<>(new String[] {
            "Appointment count by department", "Doctor workload", "Average rating by doctor" });

    private final DefaultTableModel tableModel = new DefaultTableModel(new String[] { "Key", "Count" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public ReportsPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        topPanel.add(revenueLabel);
        topPanel.add(new JLabel("   Report:"));
        topPanel.add(reportBox);
        JButton runButton = UITheme.primaryButton("Run report");
        topPanel.add(runButton);
        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        runButton.addActionListener(e -> {
            refreshRevenue();
            runReport();
        });

        refreshRevenue();
        runReport();
    }

    private void refreshRevenue() {
        double total = reportGenerator.getRevenueSummary(null, null);
        revenueLabel.setText(String.format("Total revenue: %.2f (%d billing records)",
                total, FileHandler.readRecords(Constants.BILLING_FILE).size()));
    }

    private void runReport() {
        tableModel.setRowCount(0);
        String selected = (String) reportBox.getSelectedItem();
        if ("Average rating by doctor".equals(selected)) {
            tableModel.setColumnIdentifiers(new String[] { "Doctor ID", "Average rating (out of 5)" });
            for (Map.Entry<String, Double> entry : reportGenerator.getAverageRatingByDoctor().entrySet()) {
                tableModel.addRow(new Object[] { entry.getKey(), String.format("%.1f", entry.getValue()) });
            }
        } else if ("Doctor workload".equals(selected)) {
            tableModel.setColumnIdentifiers(new String[] { "Doctor ID", "Appointments" });
            for (Map.Entry<String, Integer> entry : reportGenerator.getDoctorWorkload().entrySet()) {
                tableModel.addRow(new Object[] { entry.getKey(), entry.getValue() });
            }
        } else {
            tableModel.setColumnIdentifiers(new String[] { "Department ID", "Appointments" });
            for (Map.Entry<String, Integer> entry : reportGenerator.getAppointmentCountByDept().entrySet()) {
                tableModel.addRow(new Object[] { entry.getKey(), entry.getValue() });
            }
        }
    }
}
