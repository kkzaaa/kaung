package hms.model;

import hms.util.Constants;

public class Admin extends User {

    public Admin(String userID, String username, String password, String fullName,
                 String icNumber, String contactNo, String email, String status) {
        super(userID, username, password, fullName, icNumber, contactNo, email, status);
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
