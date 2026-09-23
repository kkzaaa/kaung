package hms.gui.manager;

import hms.gui.WrapLayout;
import hms.gui.UITheme;
import hms.model.AssessmentType;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.IdGenerator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class AssessmentTypePanel extends JPanel {
    private static final String[] FIELD_OPTIONS = { "BP", "Temp", "Weight", "Height", "Pulse", "SpO2" };

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[] { "Type ID", "Name", "Required fields" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JTextField nameField = new JTextField(15);
    private final List<JCheckBox> fieldChecks = new ArrayList<>();

    public AssessmentTypePanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        for (String option : FIELD_OPTIONS) {
            JCheckBox box = new JCheckBox(option);
            fieldChecks.add(box);
            formPanel.add(box);
        }
        JButton addButton = UITheme.primaryButton("Add assessment type");
        JButton refreshButton = new JButton("Refresh");
        formPanel.add(addButton);
        formPanel.add(refreshButton);
        add(formPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addType());
        refreshButton.addActionListener(e -> loadTypes());

        loadTypes();
    }

    private void loadTypes() {
        tableModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.ASSESSMENT_TYPES_FILE)) {
            tableModel.addRow(row);
        }
    }

    private void addType() {
        String name = nameField.getText().trim();
        if (name.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a name for this assessment type.");
            return;
        }
        List<String> required = new ArrayList<>();
        for (JCheckBox box : fieldChecks) {
            if (box.isSelected()) {
                required.add(box.getText());
            }
        }
        if (required.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one required field.");
            return;
        }
        String typeID = IdGenerator.nextId(Constants.ASSESSMENT_TYPES_FILE, "T");
        FileHandler.appendRecord(Constants.ASSESSMENT_TYPES_FILE, new AssessmentType(typeID, name, required).toFields());
        loadTypes();
        nameField.setText("");
        for (JCheckBox box : fieldChecks) {
            box.setSelected(false);
        }
    }
}
