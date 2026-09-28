package hms.service;

import hms.model.Appointment;
import hms.model.Feedback;
import hms.util.Constants;
import hms.util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class PatientService {

    public boolean isSlotAvailable(String doctorID, String date, String time) {
        return isSlotAvailable(doctorID, date, time, null);
    }

    private boolean isSlotAvailable(String doctorID, String date, String time, String ignoreApptID) {
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (row[0].equals(ignoreApptID)) {
                continue;
            }
            if (row[2].equals(doctorID) && row[3].equals(date) && row[4].equals(time)
                    && !row[6].equals(Constants.STATUS_CANCELLED)) {
                return false;
            }
        }
        return true;
    }

    public void bookAppointment(Appointment appointment) {
        if (!isSlotAvailable(appointment.getDoctorID(), appointment.getDate(), appointment.getTime())) {
            throw new IllegalStateException("Slot is already booked: " + appointment.getDoctorID()
                    + " " + appointment.getDate() + " " + appointment.getTime());
        }
        FileHandler.appendRecord(Constants.APPOINTMENTS_FILE, appointment.toFields());
    }

    public void rescheduleAppointment(String apptID, String newDate, String newTime) {
        List<String[]> records = FileHandler.readRecords(Constants.APPOINTMENTS_FILE);
        String[] target = null;
        for (String[] row : records) {
            if (row[0].equals(apptID)) {
                target = row;
                break;
            }
        }
        if (target == null) {
            throw new IllegalArgumentException("No such appointment: " + apptID);
        }
        if (!isSlotAvailable(target[2], newDate, newTime, apptID)) {
            throw new IllegalStateException("Slot is already booked: " + target[2] + " " + newDate + " " + newTime);
        }
        target[3] = newDate;
        target[4] = newTime;
        target[6] = Constants.STATUS_RESCHEDULED;
        FileHandler.rewriteFile(Constants.APPOINTMENTS_FILE, records);
    }

    public void cancelAppointment(String apptID) {
        List<String[]> records = FileHandler.readRecords(Constants.APPOINTMENTS_FILE);
        for (String[] row : records) {
            if (row[0].equals(apptID)) {
                row[6] = Constants.STATUS_CANCELLED;
                break;
            }
        }
        FileHandler.rewriteFile(Constants.APPOINTMENTS_FILE, records);
    }

    public List<String[]> getMedicalHistory(String patientID) {
        List<String[]> history = new ArrayList<>();
        for (String[] row : FileHandler.readRecords(Constants.ASSESSMENTS_FILE)) {
            if (row[4].equals(patientID)) {
                history.add(row);
            }
        }
        return history;
    }

    public void submitFeedback(Feedback feedback) {
        FileHandler.appendRecord(Constants.FEEDBACK_FILE, feedback.toFields());
    }
}
