package hms.model;

import hms.util.Constants;

public class Admin extends User {
    private String adminID;

    public Admin(String userID, String username, String password, String fullName,
                 String icNumber, String contactNo, String email, String status, String adminID) {
        super(userID, username, password, fullName, icNumber, contactNo, email, status);
        this.adminID = adminID;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    @Override
    public void showDashboard() {
        System.out.println("Opening Admin dashboard for " + getFullName());
    }

    @Override
    public String getRole() {
        return Constants.ROLE_ADMIN;
    }
}
