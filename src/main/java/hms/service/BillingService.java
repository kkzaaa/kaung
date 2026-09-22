package hms.service;

import hms.model.Billing;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class BillingService {

    public double getRateForDept(String deptID) {
        for (String[] row : FileHandler.readRecords(Constants.RATES_FILE)) {
            if (row[0].equals(deptID)) {
                return Double.parseDouble(row[1]);
            }
        }
        return 0.0;
    }

    public String gradeForAmount(double amount) {
        if (amount >= 200) {
            return "A";
        } else if (amount >= 100) {
            return "B";
        }
        return "C";
    }

    public Billing createBill(String patientID, String apptID, double amount, String insuranceStatus) {
        String billID = IdGenerator.nextId(Constants.BILLING_FILE, "B");
        Billing bill = new Billing(billID, patientID, apptID, amount, gradeForAmount(amount),
                insuranceStatus, "UNPAID");
        FileHandler.appendRecord(Constants.BILLING_FILE, bill.toFields());
        return bill;
    }

    public List<String[]> getBillsByPatient(String patientID) {
        List<String[]> matches = new ArrayList<>();
        for (String[] row : FileHandler.readRecords(Constants.BILLING_FILE)) {
            if (row[1].equals(patientID)) {
                matches.add(row);
            }
        }
        return matches;
    }

    public void updatePaymentStatus(String billID, String paymentStatus) {
        List<String[]> records = FileHandler.readRecords(Constants.BILLING_FILE);
        for (String[] row : records) {
            if (row[0].equals(billID)) {
                row[6] = paymentStatus;
                break;
            }
        }
        FileHandler.rewriteFile(Constants.BILLING_FILE, records);
    }
}
