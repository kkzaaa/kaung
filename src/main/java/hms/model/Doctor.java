package hms.model;

import hms.util.Constants;

public class Doctor extends User {
    private String specialty;
    private String departmentID;
    private String managerID;
    private String shiftSchedule;

    public Doctor(String userID, String username, String password, String fullName,
                  String icNumber, String contactNo, String email, String status,
                  String specialty, String departmentID, String managerID, String shiftSchedule) {
        super(userID, username, password, fullName, icNumber, contactNo, email, status);
        this.specialty = specialty;
        this.departmentID = departmentID;
        this.managerID = managerID;
        this.shiftSchedule = shiftSchedule;
    }

    @Override
    public void showDashboard() {
        System.out.println("Opening Doctor dashboard for " + getFullName());
    }

    @Override
    public String getRole() {
        return Constants.ROLE_DOCTOR;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }

    public String getShiftSchedule() {
        return shiftSchedule;
    }

    public void setShiftSchedule(String shiftSchedule) {
        this.shiftSchedule = shiftSchedule;
    }
}
