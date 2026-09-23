package hms.model;

public class ClinicalFeedback {
    private String feedbackID;
    private String apptID;
    private String doctorID;
    private String patientID;
    private String comment;
    private String date;

    public ClinicalFeedback(String feedbackID, String apptID, String doctorID, String patientID,
                             String comment, String date) {
        this.feedbackID = feedbackID;
        this.apptID = apptID;
        this.doctorID = doctorID;
        this.patientID = patientID;
        this.comment = comment;
        this.date = date;
    }

    public String[] toFields() {
        return new String[] { feedbackID, apptID, doctorID, patientID, comment, date };
    }

    public String getFeedbackID() {
        return feedbackID;
    }

    public String getApptID() {
        return apptID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
}
