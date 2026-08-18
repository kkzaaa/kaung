package hms.model;

import java.util.Arrays;
import java.util.List;

public class AssessmentType {
    private String typeID;
    private String typeName;
    private List<String> requiredFields;

    public AssessmentType(String typeID, String typeName, List<String> requiredFields) {
        this.typeID = typeID;
        this.typeName = typeName;
        this.requiredFields = requiredFields;
    }

    public String[] toFields() {
        return new String[] { typeID, typeName, String.join(",", requiredFields) };
    }

    public static AssessmentType fromFields(String[] fields) {
        return new AssessmentType(fields[0], fields[1], Arrays.asList(fields[2].split(",")));
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<String> getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(List<String> requiredFields) {
        this.requiredFields = requiredFields;
    }
}
