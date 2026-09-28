package hms.util;

import java.util.Map;

public class Constants {
    public static final String DELIMITER = "|";
    public static final String DATA_DIR = "data/";

    public static final String USERS_FILE = DATA_DIR + "users.txt";
    public static final String ADMINS_FILE = DATA_DIR + "admins.txt";
    public static final String MANAGERS_FILE = DATA_DIR + "managers.txt";
    public static final String DOCTORS_FILE = DATA_DIR + "doctors.txt";
    public static final String PATIENTS_FILE = DATA_DIR + "patients.txt";
    public static final String DEPARTMENTS_FILE = DATA_DIR + "departments.txt";
    public static final String WARDS_FILE = DATA_DIR + "wards.txt";
    public static final String ASSESSMENT_TYPES_FILE = DATA_DIR + "assessmentTypes.txt";
    public static final String APPOINTMENTS_FILE = DATA_DIR + "appointments.txt";
    public static final String ASSESSMENTS_FILE = DATA_DIR + "assessments.txt";
    public static final String PRESCRIPTIONS_FILE = DATA_DIR + "prescriptions.txt";
    public static final String LAB_REQUESTS_FILE = DATA_DIR + "labRequests.txt";
    public static final String BILLING_FILE = DATA_DIR + "billing.txt";
    public static final String FEEDBACK_FILE = DATA_DIR + "feedback.txt";
    public static final String RATES_FILE = DATA_DIR + "rates.txt";
    public static final String INSURANCE_FILE = DATA_DIR + "insurance.txt";
    public static final String CLINICAL_FEEDBACK_FILE = DATA_DIR + "clinicalFeedback.txt";

    // Number of fields on every line of each file (see docs/TEAM_SPLIT.md section 3.2).
    public static final Map<String, Integer> FIELD_COUNTS = Map.ofEntries(
            Map.entry(USERS_FILE, 9),
            Map.entry(ADMINS_FILE, 2),
            Map.entry(MANAGERS_FILE, 3),
            Map.entry(DOCTORS_FILE, 6),
            Map.entry(PATIENTS_FILE, 6),
            Map.entry(DEPARTMENTS_FILE, 3),
            Map.entry(WARDS_FILE, 5),
            Map.entry(ASSESSMENT_TYPES_FILE, 3),
            Map.entry(APPOINTMENTS_FILE, 7),
            Map.entry(ASSESSMENTS_FILE, 9),
            Map.entry(PRESCRIPTIONS_FILE, 8),
            Map.entry(LAB_REQUESTS_FILE, 6),
            Map.entry(BILLING_FILE, 7),
            Map.entry(FEEDBACK_FILE, 7),
            Map.entry(RATES_FILE, 2),
            Map.entry(INSURANCE_FILE, 2),
            Map.entry(CLINICAL_FEEDBACK_FILE, 6));

    public static final String LAB_PENDING = "PENDING";
    public static final String LAB_IN_PROGRESS = "IN_PROGRESS";
    public static final String LAB_COMPLETED = "COMPLETED";

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_PATIENT = "PATIENT";

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    public static final String STATUS_BOOKED = "BOOKED";
    public static final String STATUS_RESCHEDULED = "RESCHEDULED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private Constants() {
    }
}
