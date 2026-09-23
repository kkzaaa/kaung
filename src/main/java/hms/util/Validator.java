package hms.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Validator {

    public static boolean isValidEmail(String email) {
        return email.matches("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");
    }

    public static boolean isValidIC(String ic) {
        return ic.matches("\\d{6}-\\d{2}-\\d{4}");
    }

    public static boolean isValidContact(String contact) {
        return contact.matches("0\\d{8,10}");
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidTime(String time) {
        return time.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    public static boolean isPastDate(String date) {
        return LocalDate.parse(date).isBefore(LocalDate.now());
    }

    private Validator() {
    }
}
