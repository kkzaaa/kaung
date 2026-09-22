package hms.gui.admin;

import hms.model.Ward;
import hms.service.AssetManager;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.IdGenerator;

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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AssetManagementPanel extends JPanel {
    private final AssetManager assetManager = new AssetManager();
    private static final String[] COLUMNS = { "Asset ID", "Name", "Type", "Capacity", "Status" };

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField nameField = new JTextField(15);
    private final JTextField typeField = new JTextField(15);
    private final JTextField capacityField = new JTextField(6);
    private final JTextField statusField = new JTextField(10);

    public AssetManagementPanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Type (WARD/CONSULTATION_ROOM/LAB/IMAGING_ROOM)"), gbc);
        gbc.gridx = 3;
        formPanel.add(typeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Capacity"), gbc);
        gbc.gridx = 1;
        formPanel.add(capacityField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("Status"), gbc);
        gbc.gridx = 3;
        formPanel.add(statusField, gbc);
        statusField.setText("AVAILABLE");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add asset");
        JButton updateStatusButton = new JButton("Update status of selected");
        JButton refreshButton = new JButton("Refresh");
        buttonPanel.add(addButton);
        buttonPanel.add(updateStatusButton);
        buttonPanel.add(refreshButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addAsset());
        updateStatusButton.addActionListener(e -> updateStatus());
        refreshButton.addActionListener(e -> loadAssets());

        loadAssets();
    }

    private void loadAssets() {
        tableModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.WARDS_FILE)) {
            tableModel.addRow(row);
        }
    }

    private void addAsset() {
        String name = nameField.getText().trim();
        String type = typeField.getText().trim().toUpperCase();
        String capacityText = capacityField.getText().trim();
        String status = statusField.getText().trim().toUpperCase();

        if (name.isBlank() || type.isBlank() || capacityText.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please fill in name, type and capacity.");
            return;
        }
        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity must be a number.");
            return;
        }

        String assetID = IdGenerator.nextId(Constants.WARDS_FILE, "W");
        assetManager.addWard(new Ward(assetID, name, type, capacity, status.isBlank() ? "AVAILABLE" : status));
        loadAssets();
        nameField.setText("");
        typeField.setText("");
        capacityField.setText("");
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an asset first.");
            return;
        }
        String assetID = (String) tableModel.getValueAt(row, 0);
        String newStatus = statusField.getText().trim().toUpperCase();
        if (newStatus.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a status to apply.");
            return;
        }
        assetManager.updateAssetStatus(assetID, newStatus);
        loadAssets();
    }
}
