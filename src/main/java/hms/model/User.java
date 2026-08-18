package hms.model;

public abstract class User {
    private String userID;
    private String username;
    private String password;
    private String fullName;
    private String icNumber;
    private String contactNo;
    private String email;
    private String status;

    public User(String userID, String username, String password, String fullName,
                String icNumber, String contactNo, String email, String status) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.icNumber = icNumber;
        this.contactNo = contactNo;
        this.email = email;
        this.status = status;
    }

    public abstract void showDashboard();

    public abstract String getRole();

    public String[] toFields() {
        return new String[] { userID, getRole(), username, password, fullName, icNumber, contactNo, email, status };
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
