package hms.model;

public class Assessment {
    private String assessmentID;
    private String apptID;
    private String typeID;
    private String doctorID;
    private String patientID;
    private String vitals;
    private String labResults;
    private String notes;
    private String date;

    public Assessment(String assessmentID, String apptID, String typeID, String doctorID,
                       String patientID, String vitals, String labResults, String notes, String date) {
        this.assessmentID = assessmentID;
        this.apptID = apptID;
        this.typeID = typeID;
        this.doctorID = doctorID;
        this.patientID = patientID;
        this.vitals = vitals;
        this.labResults = labResults;
        this.notes = notes;
        this.date = date;
    }

    public String[] toFields() {
        return new String[] { assessmentID, apptID, typeID, doctorID, patientID, vitals, labResults, notes, date };
    }

    public static Assessment fromFields(String[] f) {
        return new Assessment(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]);
    }

    public String getAssessmentID() {
        return assessmentID;
    }

    public String getApptID() {
        return apptID;
    }

    public String getTypeID() {
        return typeID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getVitals() {
        return vitals;
    }

    public String getLabResults() {
        return labResults;
    }

    public String getNotes() {
        return notes;
    }

    public String getDate() {
        return date;
    }
}
