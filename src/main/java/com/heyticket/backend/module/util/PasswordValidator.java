package com.heyticket.backend.module.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%*])(?=.*\\d).{10,}$";

    public static void validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        boolean matches = pattern.matcher(password).matches();
        if (!matches) {
            throw new IllegalStateException("Invalid password");
        }
    }

}
