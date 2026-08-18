package hms.service;

import hms.util.Constants;
import hms.util.FileHandler;

import java.util.List;

public class RosterManager {

    public void assignShift(String doctorID, String shiftSchedule) {
        List<String[]> records = FileHandler.readRecords(Constants.DOCTORS_FILE);
        for (String[] row : records) {
            if (row[0].equals(doctorID)) {
                row[5] = shiftSchedule;
                break;
            }
        }
        FileHandler.rewriteFile(Constants.DOCTORS_FILE, records);
    }

    public boolean checkConflict(String doctorID, String date, String time) {
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            String apptDoctorID = row[2];
            String apptDate = row[3];
            String apptTime = row[4];
            String status = row[6];
            if (apptDoctorID.equals(doctorID) && apptDate.equals(date) && apptTime.equals(time)
                    && !status.equals(Constants.STATUS_CANCELLED)) {
                return true;
            }
        }
        return false;
    }

    public String getRosterByDoctor(String doctorID) {
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            if (row[0].equals(doctorID)) {
                return row[5];
            }
        }
        return "";
    }
}
