package hms.model;

public class Billing {
    private String billID;
    private String patientID;
    private String apptID;
    private double amount;
    private String grade;
    private String insuranceStatus;
    private String paymentStatus;

    public Billing(String billID, String patientID, String apptID, double amount,
                    String grade, String insuranceStatus, String paymentStatus) {
        this.billID = billID;
        this.patientID = patientID;
        this.apptID = apptID;
        this.amount = amount;
        this.grade = grade;
        this.insuranceStatus = insuranceStatus;
        this.paymentStatus = paymentStatus;
    }

    public String[] toFields() {
        return new String[] { billID, patientID, apptID, String.valueOf(amount), grade, insuranceStatus, paymentStatus };
    }

    public static Billing fromFields(String[] f) {
        return new Billing(f[0], f[1], f[2], Double.parseDouble(f[3]), f[4], f[5], f[6]);
    }

    public String getBillID() {
        return billID;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getApptID() {
        return apptID;
    }

    public double getAmount() {
        return amount;
    }

    public String getGrade() {
        return grade;
    }

    public String getInsuranceStatus() {
        return insuranceStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}
