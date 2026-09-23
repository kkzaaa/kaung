package hms.service;

import hms.util.Constants;
import hms.util.FileHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator {

    public double getRevenueSummary(String fromDate, String toDate) {
        double total = 0;
        for (String[] row : FileHandler.readRecords(Constants.BILLING_FILE)) {
            total += Double.parseDouble(row[3]);
        }
        return total;
    }

    public Map<String, Integer> getAppointmentCountByDept() {
        Map<String, String> doctorToDept = new HashMap<>();
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            doctorToDept.put(row[0], row[3]);
        }

        Map<String, Integer> countByDept = new HashMap<>();
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            String deptID = doctorToDept.getOrDefault(row[2], "UNKNOWN");
            countByDept.merge(deptID, 1, Integer::sum);
        }
        return countByDept;
    }

    public Map<String, Integer> getDoctorWorkload() {
        Map<String, Integer> workload = new HashMap<>();
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            workload.merge(row[2], 1, Integer::sum);
        }
        return workload;
    }

    public Map<String, Double> getAverageRatingByDoctor() {
        Map<String, Integer> totals = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();
        for (String[] row : FileHandler.readRecords(Constants.FEEDBACK_FILE)) {
            String doctorID = row[2];
            totals.merge(doctorID, Integer.parseInt(row[4]), Integer::sum);
            counts.merge(doctorID, 1, Integer::sum);
        }
        Map<String, Double> averages = new HashMap<>();
        for (String doctorID : totals.keySet()) {
            averages.put(doctorID, (double) totals.get(doctorID) / counts.get(doctorID));
        }
        return averages;
    }

    public List<String[]> generateReport(String type) {
        switch (type) {
            case "APPOINTMENTS":
                return FileHandler.readRecords(Constants.APPOINTMENTS_FILE);
            case "BILLING":
                return FileHandler.readRecords(Constants.BILLING_FILE);
            case "FEEDBACK":
                return FileHandler.readRecords(Constants.FEEDBACK_FILE);
            default:
                throw new IllegalArgumentException("Unknown report type: " + type);
        }
    }
}
