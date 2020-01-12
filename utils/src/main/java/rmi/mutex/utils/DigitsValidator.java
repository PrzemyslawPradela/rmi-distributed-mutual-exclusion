package rmi.mutex.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DigitsValidator {
    private final Pattern pattern;

    private static final String DIGITS_PATTERN = "\\d+";

    public DigitsValidator() {
        pattern = Pattern.compile(DIGITS_PATTERN);
    }

    public boolean validate(final String digits) {
        Matcher matcher = pattern.matcher(digits);
        return !matcher.matches();
    }
}