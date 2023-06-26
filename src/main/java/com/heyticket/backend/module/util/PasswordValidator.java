package com.heyticket.backend.module.util;

import com.heyticket.backend.exception.InternalCode;
import com.heyticket.backend.exception.ValidationFailureException;
import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";

    public static void validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        boolean matches = pattern.matcher(password).matches();
        if (!matches) {
            throw new ValidationFailureException("Invalid password", InternalCode.BAD_REQUEST);
        }
    }
}
