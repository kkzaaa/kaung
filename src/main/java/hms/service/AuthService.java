package hms.service;

import hms.model.Admin;
import hms.model.Doctor;
import hms.model.MedicalManager;
import hms.model.Patient;
import hms.model.User;
import hms.util.Constants;
import hms.util.FileHandler;

import java.util.List;

public class AuthService {

    public User login(String username, String password) {
        List<String[]> users = FileHandler.readRecords(Constants.USERS_FILE);
        for (String[] row : users) {
            String userID = row[0];
            String role = row[1];
            String rowUsername = row[2];
            String rowPassword = row[3];
            String fullName = row[4];
            String icNumber = row[5];
            String contactNo = row[6];
            String email = row[7];
            String status = row[8];

            if (!rowUsername.equals(username) || !rowPassword.equals(password)) {
                continue;
            }

            switch (role) {
                case Constants.ROLE_ADMIN:
                    return new Admin(userID, rowUsername, rowPassword, fullName, icNumber, contactNo, email, status);
                case Constants.ROLE_MANAGER:
                    return buildManager(userID, rowUsername, rowPassword, fullName, icNumber, contactNo, email, status);
                case Constants.ROLE_DOCTOR:
                    return buildDoctor(userID, rowUsername, rowPassword, fullName, icNumber, contactNo, email, status);
                case Constants.ROLE_PATIENT:
                    return buildPatient(userID, rowUsername, rowPassword, fullName, icNumber, contactNo, email, status);
                default:
                    return null;
            }
        }
        return null;
    }

    private MedicalManager buildManager(String userID, String username, String password, String fullName,
                                         String icNumber, String contactNo, String email, String status) {
        for (String[] row : FileHandler.readRecords(Constants.MANAGERS_FILE)) {
            if (row[1].equals(userID)) {
                return new MedicalManager(userID, username, password, fullName, icNumber, contactNo, email, status, row[2]);
            }
        }
        return new MedicalManager(userID, username, password, fullName, icNumber, contactNo, email, status, "");
    }

    private Doctor buildDoctor(String userID, String username, String password, String fullName,
                                String icNumber, String contactNo, String email, String status) {
        for (String[] row : FileHandler.readRecords(Constants.DOCTORS_FILE)) {
            if (row[1].equals(userID)) {
                return new Doctor(userID, username, password, fullName, icNumber, contactNo, email, status,
                        row[2], row[3], row[4], row[5]);
            }
        }
        return new Doctor(userID, username, password, fullName, icNumber, contactNo, email, status, "", "", "", "");
    }

    private Patient buildPatient(String userID, String username, String password, String fullName,
                                  String icNumber, String contactNo, String email, String status) {
        for (String[] row : FileHandler.readRecords(Constants.PATIENTS_FILE)) {
            if (row[1].equals(userID)) {
                return new Patient(userID, username, password, fullName, icNumber, contactNo, email, status,
                        row[2], row[3], row[4], row[5]);
            }
        }
        return new Patient(userID, username, password, fullName, icNumber, contactNo, email, status, "", "", "", "");
    }
}
