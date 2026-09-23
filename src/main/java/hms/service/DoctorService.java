package hms.service;

import hms.model.Assessment;
import hms.model.ClinicalFeedback;
import hms.model.LabRequest;
import hms.model.Prescription;
import hms.util.Constants;
import hms.util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class DoctorService {

    public List<String[]> getTodaysAppointments(String doctorID, String date) {
        List<String[]> matches = new ArrayList<>();
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (row[2].equals(doctorID) && row[3].equals(date)) {
                matches.add(row);
            }
        }
        return matches;
    }

    public void recordAssessment(Assessment assessment) {
        FileHandler.appendRecord(Constants.ASSESSMENTS_FILE, assessment.toFields());
    }

    public void issuePrescription(Prescription prescription) {
        FileHandler.appendRecord(Constants.PRESCRIPTIONS_FILE, prescription.toFields());
    }

    public void requestLabTest(LabRequest labRequest) {
        FileHandler.appendRecord(Constants.LAB_REQUESTS_FILE, labRequest.toFields());
    }

    public List<String[]> getLabRequestsByDoctor(String doctorID) {
        List<String[]> matches = new ArrayList<>();
        for (String[] row : FileHandler.readRecords(Constants.LAB_REQUESTS_FILE)) {
            if (row[1].equals(doctorID)) {
                matches.add(row);
            }
        }
        return matches;
    }

    public void completeAppointment(String apptID) {
        List<String[]> records = FileHandler.readRecords(Constants.APPOINTMENTS_FILE);
        for (String[] row : records) {
            if (row[0].equals(apptID)) {
                row[6] = Constants.STATUS_COMPLETED;
                break;
            }
        }
        FileHandler.rewriteFile(Constants.APPOINTMENTS_FILE, records);
    }

    public void giveClinicalFeedback(ClinicalFeedback feedback) {
        FileHandler.appendRecord(Constants.CLINICAL_FEEDBACK_FILE, feedback.toFields());
    }
}
