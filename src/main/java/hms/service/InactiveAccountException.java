package hms.service;

public class InactiveAccountException extends RuntimeException {

    public InactiveAccountException(String username) {
        super("The account '" + username + "' is inactive. Please contact the admin.");
    }
}
