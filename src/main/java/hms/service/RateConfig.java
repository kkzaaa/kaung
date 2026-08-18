package hms.service;

import hms.util.Constants;
import hms.util.FileHandler;

import java.util.List;

public class RateConfig {

    public void setConsultationRate(String deptID, double amount) {
        List<String[]> records = FileHandler.readRecords(Constants.RATES_FILE);
        for (String[] row : records) {
            if (row[0].equals(deptID)) {
                row[1] = String.valueOf(amount);
                FileHandler.rewriteFile(Constants.RATES_FILE, records);
                return;
            }
        }
        FileHandler.appendRecord(Constants.RATES_FILE, new String[] { deptID, String.valueOf(amount) });
    }

    public void toggleInsurance(String providerName) {
        List<String[]> records = FileHandler.readRecords(Constants.INSURANCE_FILE);
        for (String[] row : records) {
            if (row[0].equals(providerName)) {
                row[1] = row[1].equals("ACCEPTED") ? "NOT_ACCEPTED" : "ACCEPTED";
                FileHandler.rewriteFile(Constants.INSURANCE_FILE, records);
                return;
            }
        }
        FileHandler.appendRecord(Constants.INSURANCE_FILE, new String[] { providerName, "ACCEPTED" });
    }
}
