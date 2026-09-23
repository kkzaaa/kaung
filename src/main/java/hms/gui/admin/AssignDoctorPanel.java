package hms.gui.admin;

import hms.gui.UITheme;
import hms.util.Constants;
import hms.util.FileHandler;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Vector;

public class AssignDoctorPanel extends JPanel {
    private final JComboBox<String> doctorBox = new JComboBox<>();
    private final JComboBox<String> managerBox = new JComboBox<>();

    public AssignDoctorPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Doctor"), gbc);
        gbc.gridx = 1;
        add(doctorBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Assign to Manager"), gbc);
        gbc.gridx = 1;
        add(managerBox, gbc);

        JButton assignButton = UITheme.primaryButton("Assign");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(assignButton, gbc);

        JButton refreshButton = new JButton("Refresh lists");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(refreshButton, gbc);

        refreshButton.addActionListener(e -> loadLists());
        assignButton.addActionListener(e -> assign());

        loadLists();
    }

    private void loadLists() {
        Vector<String> doctorIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            doctorIDs.add(row[0]);
        }
        doctorBox.setModel(new javax.swing.DefaultComboBoxModel<>(doctorIDs));

        Vector<String> managerIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.MANAGERS_FILE)) {
            managerIDs.add(row[0]);
        }
        managerBox.setModel(new javax.swing.DefaultComboBoxModel<>(managerIDs));
    }

    private void assign() {
        String doctorID = (String) doctorBox.getSelectedItem();
        String managerID = (String) managerBox.getSelectedItem();
        if (doctorID == null || managerID == null) {
            JOptionPane.showMessageDialog(this, "No doctors or managers available to assign.");
            return;
        }
        List<String[]> records = FileHandler.readRecords(Constants.DOCTORS_FILE);
        for (String[] row : records) {
            if (row[0].equals(doctorID)) {
                row[4] = managerID;
                break;
            }
        }
        FileHandler.rewriteFile(Constants.DOCTORS_FILE, records);
        JOptionPane.showMessageDialog(this, "Doctor " + doctorID + " assigned to Manager " + managerID + ".");
    }
}
