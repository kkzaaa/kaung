package hms.gui.admin;

import hms.gui.WrapLayout;
import hms.gui.UITheme;
import hms.util.Constants;
import hms.util.FileHandler;

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
import java.util.List;

public class LabRequestsPanel extends JPanel {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Request ID", "Doctor ID", "Patient ID", "Test type", "Status", "Date" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JComboBox<String> filterBox = new JComboBox<>(
            new String[] { "ALL", Constants.LAB_PENDING, Constants.LAB_IN_PROGRESS, Constants.LAB_COMPLETED });
    private final JComboBox<String> statusBox = new JComboBox<>(
            new String[] { Constants.LAB_PENDING, Constants.LAB_IN_PROGRESS, Constants.LAB_COMPLETED });

    public LabRequestsPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Show:"));
        topPanel.add(filterBox);
        JButton refreshButton = new JButton("Refresh");
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("Set status of selected request to"));
        bottomPanel.add(statusBox);
        JButton updateButton = UITheme.primaryButton("Update");
        bottomPanel.add(updateButton);
        add(bottomPanel, BorderLayout.SOUTH);

        filterBox.addActionListener(e -> loadRequests());
        refreshButton.addActionListener(e -> loadRequests());
        updateButton.addActionListener(e -> updateStatus());

        loadRequests();
    }

    private void loadRequests() {
        tableModel.setRowCount(0);
        String filter = (String) filterBox.getSelectedItem();
        for (String[] row : FileHandler.readRecords(Constants.LAB_REQUESTS_FILE)) {
            if (filter.equals("ALL") || filter.equals(row[4])) {
                tableModel.addRow(row);
            }
        }
    }

    private void updateStatus() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a lab request first.");
            return;
        }
        String requestID = (String) tableModel.getValueAt(selected, 0);
        String newStatus = (String) statusBox.getSelectedItem();

        List<String[]> records = FileHandler.readRecords(Constants.LAB_REQUESTS_FILE);
        for (String[] row : records) {
            if (row[0].equals(requestID)) {
                row[4] = newStatus;
                break;
            }
        }
        FileHandler.rewriteFile(Constants.LAB_REQUESTS_FILE, records);
        loadRequests();
    }
}
