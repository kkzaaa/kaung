package hms.model;

import hms.util.Constants;

public class MedicalManager extends User {
    private String managerID;
    private String departmentID;

    public MedicalManager(String userID, String username, String password, String fullName,
                           String icNumber, String contactNo, String email, String status,
                           String managerID, String departmentID) {
        super(userID, username, password, fullName, icNumber, contactNo, email, status);
        this.managerID = managerID;
        this.departmentID = departmentID;
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
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
