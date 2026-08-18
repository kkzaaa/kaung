package hms.model;

import hms.util.Constants;

public class Patient extends User {
    private String dob;
    private String gender;
    private String bloodType;
    private String address;

    public Patient(String userID, String username, String password, String fullName,
                   String icNumber, String contactNo, String email, String status,
                   String dob, String gender, String bloodType, String address) {
        super(userID, username, password, fullName, icNumber, contactNo, email, status);
        this.dob = dob;
        this.gender = gender;
        this.bloodType = bloodType;
        this.address = address;
    }

    @Override
    public void showDashboard() {
        System.out.println("Opening Patient dashboard for " + getFullName());
    }

    @Override
    public String getRole() {
        return Constants.ROLE_PATIENT;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
