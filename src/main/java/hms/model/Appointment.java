package hms.model;

public class Appointment {
    private String apptID;
    private String patientID;
    private String doctorID;
    private String date;
    private String time;
    private String roomID;
    private String status;

    public Appointment(String apptID, String patientID, String doctorID, String date,
                        String time, String roomID, String status) {
        this.apptID = apptID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.date = date;
        this.time = time;
        this.roomID = roomID;
        this.status = status;
    }

    public String[] toFields() {
        return new String[] { apptID, patientID, doctorID, date, time, roomID, status };
    }

    public static Appointment fromFields(String[] fields) {
        return new Appointment(fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
    }

    public String getApptID() {
        return apptID;
    }

    public void setApptID(String apptID) {
        this.apptID = apptID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
