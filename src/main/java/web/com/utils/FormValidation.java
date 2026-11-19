package web.com.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class FormValidation {
    private static final Pattern EMAIL = Pattern.compile(
            "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?"
            + "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*");
    private static final Pattern OTP = Pattern.compile("[0-9]{6}");
    private static final Pattern PHONE = Pattern.compile("\\+?[0-9][0-9 ]{6,13}[0-9]");

    private FormValidation() {
    }

    public static String text(String value) {
        return value == null ? "" : value.strip();
    }

    public static Integer nonNegativeInteger(String value) {
        try {
            int number = Integer.parseInt(text(value));
            return number >= 0 ? number : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static int positiveInteger(String value, int fallback) {
        Integer number = nonNegativeInteger(value);
        return number != null && number > 0 ? number : fallback;
    }

    public static BigDecimal price(String value, BigDecimal maximum) {
        try {
            BigDecimal price = new BigDecimal(text(value));
            if (price.signum() < 0 || price.compareTo(maximum) > 0 || price.stripTrailingZeros().scale() > 2) {
                return null;
            }
            return price.setScale(2);
        } catch (NumberFormatException | ArithmeticException e) {
            return null;
        }
    }

    public static boolean isEmail(String email) {
        return email != null && email.length() <= 150 && EMAIL.matcher(email).matches();
    }

    public static boolean isOtp(String otp) {
        return otp != null && OTP.matcher(otp).matches();
    }

    public static String passwordError(String password) {
        if (password == null || password.isBlank()) {
            return "Vui lòng nhập mật khẩu.";
        }
        return password.length() > 255 ? "Mật khẩu không được quá 255 ký tự." : null;
    }

    public static String profileError(String fullName, String phone) {
        String name = text(fullName);
        if (name.isEmpty() || name.length() > 255) {
            return "Họ tên là bắt buộc và không được quá 255 ký tự.";
        }
        String number = text(phone);
        if (!number.isEmpty() && (number.length() > 15 || !PHONE.matcher(number).matches())) {
            return "Số điện thoại phải có từ 8 đến 15 ký tự và đúng định dạng.";
        }
        return null;
    }
}
