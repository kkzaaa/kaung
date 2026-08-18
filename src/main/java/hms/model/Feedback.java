package hms.model;

public class Feedback {
    private String feedbackID;
    private String patientID;
    private String doctorID;
    private String apptID;
    private int rating;
    private String comment;
    private String date;

    public Feedback(String feedbackID, String patientID, String doctorID, String apptID,
                     int rating, String comment, String date) {
        this.feedbackID = feedbackID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.apptID = apptID;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public String[] toFields() {
        return new String[] { feedbackID, patientID, doctorID, apptID, String.valueOf(rating), comment, date };
    }

    public static Feedback fromFields(String[] f) {
        return new Feedback(f[0], f[1], f[2], f[3], Integer.parseInt(f[4]), f[5], f[6]);
    }

    public String getFeedbackID() {
        return feedbackID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getApptID() {
        return apptID;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
}
