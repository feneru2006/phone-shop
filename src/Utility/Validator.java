package Utility;

import java.util.regex.Pattern;
import java.time.LocalDateTime;

public class Validator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9}$");
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{2,20}$");
    private static final Pattern IMEI_PATTERN = Pattern.compile("^[0-9]{15}$"); // Chuẩn IMEI 15 số
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,15}$");

    // ===== Kiểm tra String cơ bản =====
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    // Kiểm tra các loại mã (MASP, MANV, MAHD...)
    public static boolean isValidCode(String code) {
        if (isNullOrEmpty(code)) return false;
        return CODE_PATTERN.matcher(code.trim()).matches();
    }
    // Kiểm tra IMEI cho bảng ctsp
    public static boolean isValidIMEI(String imei) {
        if (isNullOrEmpty(imei)) return false;
        return IMEI_PATTERN.matcher(imei.trim()).matches();
    }

    // Kiểm tra Số điện thoại
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    // Kiểm tra Tài khoản đăng nhập
    public static boolean isValidUsername(String username) {
        if (isNullOrEmpty(username)) return false;
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    // ===== Kiểm tra số (Sử dụng double cho tiền tệ như Database) =====
    public static boolean isPositive(double value) {
        return value > 0;
    }
    public static boolean isNonNegative(double value) {
        return value >= 0;
    }

    public static boolean isPositiveInteger(Integer value) {
        return value != null && value > 0;
    }

    // Kiểm tra phần trăm giảm giá (0-100)
    public static boolean isValidPercent(int percent) {
        return percent >= 0 && percent <= 100;
    }

    // ===== Kiểm tra Logic Thời gian =====
    
    public static boolean isValidDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return false;
        return end.isAfter(start);
    }

    // ===== Thông báo lỗi =====
    
    public static String requiredMessage(String fieldName) {
        return fieldName + " không được để trống";
    }
    public static String invalidFormatMessage(String fieldName) {
        return fieldName + " không đúng định dạng hoặc chứa ký tự đặc biệt";
    }
    public static String minMaxLengthMessage(String fieldName, int min, int max) {
        return fieldName + " phải từ " + min + " đến " + max + " ký tự";
    }
    public static String positiveNumberMessage(String fieldName) {
        return fieldName + " phải là số dương lớn hơn 0";
    }
    public static String invalidDateRangeMessage() {
        return "Ngày kết thúc phải sau ngày bắt đầu";
    }
    public static String invalidPercentMessage() {
        return "Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100";
    }
}