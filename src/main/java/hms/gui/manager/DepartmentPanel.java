package hms.gui.manager;

import hms.model.Department;
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
import java.util.List;

public class DepartmentPanel extends JPanel {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Dept ID", "Name", "Manager ID" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField nameField = new JTextField(15);
    private final JTextField managerField = new JTextField(10);

    public DepartmentPanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Manager ID"));
        formPanel.add(managerField);
        JButton addButton = new JButton("Add department");
        JButton deleteButton = new JButton("Delete selected");
        JButton refreshButton = new JButton("Refresh");
        formPanel.add(addButton);
        formPanel.add(deleteButton);
        formPanel.add(refreshButton);
        add(formPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addDepartment());
        deleteButton.addActionListener(e -> deleteSelected());
        refreshButton.addActionListener(e -> loadDepartments());

        loadDepartments();
    }

    private void loadDepartments() {
        tableModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.DEPARTMENTS_FILE)) {
            tableModel.addRow(row);
        }
    }

    private void addDepartment() {
        String name = nameField.getText().trim();
        String managerID = managerField.getText().trim();
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a department name.");
            return;
        }
        for (String[] row : FileHandler.readRecords(Constants.DEPARTMENTS_FILE)) {
            if (row[1].equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "A department with this name already exists.");
                return;
            }
        }
        String deptID = IdGenerator.nextId(Constants.DEPARTMENTS_FILE, "DEPT");
        FileHandler.appendRecord(Constants.DEPARTMENTS_FILE, new Department(deptID, name, managerID).toFields());
        loadDepartments();
        nameField.setText("");
        managerField.setText("");
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a department first.");
            return;
        }
        String deptID = (String) tableModel.getValueAt(row, 0);
        List<String[]> records = FileHandler.readRecords(Constants.DEPARTMENTS_FILE);
        records.removeIf(r -> r[0].equals(deptID));
        FileHandler.rewriteFile(Constants.DEPARTMENTS_FILE, records);
        loadDepartments();
    }
}
