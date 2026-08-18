package hms.model;

public class Department {
    private String deptID;
    private String deptName;
    private String managerID;

    public Department(String deptID, String deptName, String managerID) {
        this.deptID = deptID;
        this.deptName = deptName;
        this.managerID = managerID;
    }

    public String[] toFields() {
        return new String[] { deptID, deptName, managerID };
    }

    public String getDeptID() {
        return deptID;
    }

    public void setDeptID(String deptID) {
        this.deptID = deptID;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }
}
