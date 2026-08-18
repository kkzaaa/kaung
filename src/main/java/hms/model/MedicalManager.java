package hms.model;

import hms.util.Constants;

public class MedicalManager extends User {
    private String departmentID;

    public MedicalManager(String userID, String username, String password, String fullName,
                           String icNumber, String contactNo, String email, String status,
                           String departmentID) {
        super(userID, username, password, fullName, icNumber, contactNo, email, status);
        this.departmentID = departmentID;
    }

    @Override
    public void showDashboard() {
        System.out.println("Opening Medical Manager dashboard for " + getFullName());
    }

    @Override
    public String getRole() {
        return Constants.ROLE_MANAGER;
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }
}
