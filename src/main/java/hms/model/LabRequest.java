package hms.model;

public class LabRequest {
    private String requestID;
    private String doctorID;
    private String patientID;
    private String testType;
    private String status;
    private String date;

    public LabRequest(String requestID, String doctorID, String patientID, String testType,
                       String status, String date) {
        this.requestID = requestID;
        this.doctorID = doctorID;
        this.patientID = patientID;
        this.testType = testType;
        this.status = status;
        this.date = date;
    }

    public String[] toFields() {
        return new String[] { requestID, doctorID, patientID, testType, status, date };
    }

    public static LabRequest fromFields(String[] f) {
        return new LabRequest(f[0], f[1], f[2], f[3], f[4], f[5]);
    }

    public String getRequestID() {
        return requestID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getTestType() {
        return testType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }
}
