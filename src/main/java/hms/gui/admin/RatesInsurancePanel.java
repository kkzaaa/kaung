package hms.gui.admin;

import hms.service.RateConfig;
import hms.util.Constants;
import hms.util.FileHandler;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class RatesInsurancePanel extends JPanel {
    private final RateConfig rateConfig = new RateConfig();

    private final DefaultTableModel ratesModel = new DefaultTableModel(new String[] { "Department ID", "Base rate" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel insuranceModel = new DefaultTableModel(new String[] { "Provider", "Status" }, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable ratesTable = new JTable(ratesModel);
    private final JTable insuranceTable = new JTable(insuranceModel);

    private final JTextField deptField = new JTextField(10);
    private final JTextField amountField = new JTextField(10);
    private final JTextField providerField = new JTextField(15);

    public RatesInsurancePanel() {
        setLayout(new GridLayout(1, 2, 10, 10));

        JPanel ratesPanel = new JPanel(new BorderLayout());
        ratesPanel.add(new JLabel("Consultation rates by department"), BorderLayout.NORTH);
        ratesPanel.add(new JScrollPane(ratesTable), BorderLayout.CENTER);

        JPanel rateForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        rateForm.add(new JLabel("Dept ID"), gbc);
        gbc.gridx = 1;
        rateForm.add(deptField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        rateForm.add(new JLabel("Amount"), gbc);
        gbc.gridx = 1;
        rateForm.add(amountField, gbc);
        JButton setRateButton = new JButton("Set rate");
        gbc.gridx = 1;
        gbc.gridy = 2;
        rateForm.add(setRateButton, gbc);
        ratesPanel.add(rateForm, BorderLayout.SOUTH);

        JPanel insurancePanel = new JPanel(new BorderLayout());
        insurancePanel.add(new JLabel("Accepted insurance providers"), BorderLayout.NORTH);
        insurancePanel.add(new JScrollPane(insuranceTable), BorderLayout.CENTER);

        JPanel insuranceForm = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        insuranceForm.add(new JLabel("Provider"), gbc);
        gbc.gridx = 1;
        insuranceForm.add(providerField, gbc);
        JButton toggleButton = new JButton("Toggle accepted");
        gbc.gridx = 1;
        gbc.gridy = 1;
        insuranceForm.add(toggleButton, gbc);
        insurancePanel.add(insuranceForm, BorderLayout.SOUTH);

        add(ratesPanel);
        add(insurancePanel);

        setRateButton.addActionListener(e -> setRate());
        toggleButton.addActionListener(e -> toggleInsurance());

        loadData();
    }

    private void loadData() {
        ratesModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.RATES_FILE)) {
            ratesModel.addRow(row);
        }
        insuranceModel.setRowCount(0);
        for (String[] row : FileHandler.readRecords(Constants.INSURANCE_FILE)) {
            insuranceModel.addRow(row);
        }
    }

    private void setRate() {
        String deptID = deptField.getText().trim();
        String amountText = amountField.getText().trim();
        if (deptID.isBlank() || amountText.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter department ID and amount.");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a number.");
            return;
        }
        rateConfig.setConsultationRate(deptID, amount);
        loadData();
        deptField.setText("");
        amountField.setText("");
    }

    private void toggleInsurance() {
        String provider = providerField.getText().trim();
        if (provider.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a provider name.");
            return;
        }
        rateConfig.toggleInsurance(provider);
        loadData();
        providerField.setText("");
    }
}
