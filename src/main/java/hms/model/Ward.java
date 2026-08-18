package hms.model;

public class Ward {
    private String wardID;
    private String wardName;
    private String type;
    private int capacity;
    private String status;

    public Ward(String wardID, String wardName, String type, int capacity, String status) {
        this.wardID = wardID;
        this.wardName = wardName;
        this.type = type;
        this.capacity = capacity;
        this.status = status;
    }

    public String[] toFields() {
        return new String[] { wardID, wardName, type, String.valueOf(capacity), status };
    }

    public String getWardID() {
        return wardID;
    }

    public void setWardID(String wardID) {
        this.wardID = wardID;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
