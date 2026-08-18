package hms.service;

import hms.model.Ward;
import hms.util.Constants;
import hms.util.FileHandler;

import java.util.List;

public class AssetManager {

    public void addWard(Ward ward) {
        FileHandler.appendRecord(Constants.WARDS_FILE, ward.toFields());
    }

    public void addRoom(Ward room) {
        FileHandler.appendRecord(Constants.WARDS_FILE, room.toFields());
    }

    public void updateAssetStatus(String wardID, String status) {
        List<String[]> records = FileHandler.readRecords(Constants.WARDS_FILE);
        for (String[] row : records) {
            if (row[0].equals(wardID)) {
                row[4] = status;
                break;
            }
        }
        FileHandler.rewriteFile(Constants.WARDS_FILE, records);
    }
}
