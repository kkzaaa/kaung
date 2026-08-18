package hms.model;

public class Prescription {
    private String rxID;
    private String assessmentID;
    private String patientID;
    private String doctorID;
    private String medication;
    private String dosage;
    private String instructions;
    private String date;

    public Prescription(String rxID, String assessmentID, String patientID, String doctorID,
                         String medication, String dosage, String instructions, String date) {
        this.rxID = rxID;
        this.assessmentID = assessmentID;
        this.patientID = patientID;
        this.doctorID = doctorID;
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
        this.date = date;
    }

    public String[] toFields() {
        return new String[] { rxID, assessmentID, patientID, doctorID, medication, dosage, instructions, date };
    }

    public static Prescription fromFields(String[] f) {
        return new Prescription(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]);
    }

    public String getRxID() {
        return rxID;
    }

    public String getAssessmentID() {
        return assessmentID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public String getMedication() {
        return medication;
    }

    public String getDosage() {
        return dosage;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getDate() {
        return date;
    }
}
