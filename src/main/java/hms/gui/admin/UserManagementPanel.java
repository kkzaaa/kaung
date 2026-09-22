package hms.gui.admin;

import hms.service.UserManager;
import hms.util.Constants;
import hms.util.FileHandler;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final UserManager userManager = new UserManager();
    private static final String[] COLUMNS = {
            "User ID", "Role", "Username", "Full name", "IC number", "Contact no.", "Email", "Status" };

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JComboBox<String> filterBox = new JComboBox<>(
            new String[] { "ALL", Constants.ROLE_ADMIN, Constants.ROLE_MANAGER, Constants.ROLE_DOCTOR, Constants.ROLE_PATIENT });
    private final JTextField statusField = new JTextField(10);

    public UserManagementPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter by role:"));
        topPanel.add(filterBox);
        JButton refreshButton = new JButton("Refresh");
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("New status:"));
        bottomPanel.add(statusField);
        JButton updateStatusButton = new JButton("Update status of selected");
        JButton deleteButton = new JButton("Delete selected");
        bottomPanel.add(updateStatusButton);
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadUsers());
        filterBox.addActionListener(e -> loadUsers());
        updateStatusButton.addActionListener(e -> updateSelectedStatus());
        deleteButton.addActionListener(e -> deleteSelected());

        loadUsers();
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        String filter = (String) filterBox.getSelectedItem();
        List<String[]> rows = FileHandler.readRecords(Constants.USERS_FILE);
        for (String[] row : rows) {
            String userID = row[0];
            String role = row[1];
            String username = row[2];
            String fullName = row[4];
            String icNumber = row[5];
            String contactNo = row[6];
            String email = row[7];
            String status = row[8];
            if (filter.equals("ALL") || filter.equals(role)) {
                tableModel.addRow(new Object[] { userID, role, username, fullName, icNumber, contactNo, email, status });
            }
        }
    }

    private String selectedUserID() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return null;
        }
        return (String) tableModel.getValueAt(row, 0);
    }

    private void updateSelectedStatus() {
        String userID = selectedUserID();
        if (userID == null) {
            return;
        }
        String newStatus = statusField.getText().trim().toUpperCase();
        if (newStatus.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a status (e.g. ACTIVE or INACTIVE).");
            return;
        }
        List<String[]> records = FileHandler.readRecords(Constants.USERS_FILE);
        for (String[] row : records) {
            if (row[0].equals(userID)) {
                row[8] = newStatus;
                break;
            }
        }
        FileHandler.rewriteFile(Constants.USERS_FILE, records);
        loadUsers();
    }

    private void deleteSelected() {
        String userID = selectedUserID();
        if (userID == null) {
            return;
        }
        if (hasActiveAppointments(userID)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "This user has appointment records tied to them. Delete anyway?",
                    "Warning", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        userManager.deleteUser(userID);
        loadUsers();
    }

    private boolean hasActiveAppointments(String userID) {
        String patientID = lookupID(Constants.PATIENTS_FILE, userID);
        String doctorID = lookupID(Constants.DOCTORS_FILE, userID);
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            boolean isThisUser = row[1].equals(patientID) || row[2].equals(doctorID);
            if (isThisUser && !row[6].equals(Constants.STATUS_CANCELLED)) {
                return true;
            }
        }
        return false;
    }

    private String lookupID(String filePath, String userID) {
        for (String[] row : FileHandler.readRecords(filePath)) {
            if (row[1].equals(userID)) {
                return row[0];
            }
        }
        return null;
    }
}
