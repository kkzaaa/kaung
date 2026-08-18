package hms.service;

import hms.model.User;
import hms.util.Constants;
import hms.util.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    public boolean isUsernameTaken(String username) {
        for (String[] row : FileHandler.readRecords(Constants.USERS_FILE)) {
            if (row[2].equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void createUser(User user) {
        FileHandler.appendRecord(Constants.USERS_FILE, user.toFields());
    }

    public void updateUser(User user) {
        List<String[]> records = FileHandler.readRecords(Constants.USERS_FILE);
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i)[0].equals(user.getUserID())) {
                records.set(i, user.toFields());
                break;
            }
        }
        FileHandler.rewriteFile(Constants.USERS_FILE, records);
    }

    public void deleteUser(String userID) {
        List<String[]> records = FileHandler.readRecords(Constants.USERS_FILE);
        records.removeIf(row -> row[0].equals(userID));
        FileHandler.rewriteFile(Constants.USERS_FILE, records);
    }

    public List<String[]> listUsersByRole(String role) {
        List<String[]> matches = new ArrayList<>();
        for (String[] row : FileHandler.readRecords(Constants.USERS_FILE)) {
            if (row[1].equals(role)) {
                matches.add(row);
            }
        }
        return matches;
    }
}
