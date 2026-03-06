package Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * -----------------------------------------------------------------------
 * PROJECT: PHONE SHOP MANAGEMENT SYSTEM
 * MODULE: UTILITY - DATE & TIME HANDLER
 * -----------------------------------------------------------------------
 */
public class DateUtils {

    /* ===================================================================
       [SECTION 1] CONSTANTS & FORMATTERS
       =================================================================== */
    public static final String DATE_FORMAT         = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT     = "dd/MM/yyyy HH:mm:ss";
    public static final String TIME_FORMAT         = "HH:mm:ss";
    public static final String DATE_FORMAT_ISO     = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT_ISO = "yyyy-MM-dd HH:mm:ss";

    // Formatters for modern java.time API
    private static final DateTimeFormatter DATE_FORMATTER     = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    /* ===================================================================
       [SECTION 2] STRING FORMATTING (Date -> String)
       =================================================================== */
    public static String formatDate(Date date, String pattern) {
        if (date == null) return "";
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String formatDateToString(Date date) {
        return formatDate(date, DATE_FORMAT);
    }

    public static String formatDateTimeToString(Date date) {
        return formatDate(date, DATETIME_FORMAT);
    }

    public static String formatDateToISO(Date date) {
        return formatDate(date, DATE_FORMAT_ISO);
    }

    /* ===================================================================
       [SECTION 3] STRING PARSING (String -> Date)
       =================================================================== */
    public static Date parseStringToDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseStringToDate(String dateStr) {
        return parseStringToDate(dateStr, DATE_FORMAT);
    }

    public static Date parseStringToDateTime(String dateStr) {
        return parseStringToDate(dateStr, DATETIME_FORMAT);
    }

    public static Date parseISOToDate(String dateStr) {
        return parseStringToDate(dateStr, DATE_FORMAT_ISO);
    }

    /* ===================================================================
       [SECTION 4] DATE ARITHMETIC & CALCULATION
       =================================================================== */
    public static Date getCurrentDate()     { return new Date(); }
    public static Date getCurrentDateTime() { return new Date(); }

    public static Date addDays(Date date, int days) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    public static Date addMonths(Date date, int months) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    public static Date addYears(Date date, int years) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }

    public static Date addHours(Date date, int hours) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }

    /* ===================================================================
       [SECTION 5] LOGIC COMPARISON & BOUNDARIES
       =================================================================== */
    public static boolean isBetween(Date date, Date start, Date end) {
        if (date == null || start == null || end == null) return false;
        return !date.before(start) && !date.after(end);
    }

    public static boolean isExpired(Date expiryDate) {
        return expiryDate != null && expiryDate.before(getCurrentDate());
    }

    public static long daysBetween(Date start, Date end) {
        if (start == null || end == null) return 0;
        return (end.getTime() - start.getTime()) / (1000L * 60 * 60 * 24);
    }

    public static Date getStartOfDay(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getEndOfDay(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    // --- Month & Year Boundaries ---
    public static Date getStartOfMonth(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(cal.getTime());
    }

    public static Date getEndOfMonth(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getEndOfDay(cal.getTime());
    }

    public static Date getStartOfYear(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(cal.getTime());
    }

    public static Date getEndOfYear(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        return getEndOfDay(cal.getTime());
    }

    /* ===================================================================
       [SECTION 6] VALIDATION & STATUS
       =================================================================== */
    public static boolean isValidDate(String dateStr) {
        return parseStringToDate(dateStr) != null;
    }

    public static boolean isToday(Date date) {
        if (date == null) return false;
        return compareDatesOnly(date, getCurrentDate()) == 0;
    }

    public static int compareDatesOnly(Date date1, Date date2) {
        if (date1 == null || date2 == null) return (date1 == date2) ? 0 : (date1 == null ? -1 : 1);
        return getStartOfDay(date1).compareTo(getStartOfDay(date2));
    }

    public enum ExpiryStatus { EXPIRED, EXPIRING_SOON, FRESH, UNKNOWN }

    public static ExpiryStatus getExpiryStatus(Date expiryDate) {
        if (expiryDate == null) return ExpiryStatus.UNKNOWN;
        long daysLeft = (expiryDate.getTime() - new Date().getTime()) / (1000L * 60 * 60 * 24);
        if (daysLeft < 0) return ExpiryStatus.EXPIRED;
        return (daysLeft <= 7) ? ExpiryStatus.EXPIRING_SOON : ExpiryStatus.FRESH;
    }

    /* ===================================================================
       [SECTION 7] DATABASE SQL CONVERSION
       =================================================================== */
    public static java.sql.Date toSqlDate(Date date) {
        return (date == null) ? null : new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp toSqlTimestamp(Date date) {
        return (date == null) ? null : new java.sql.Timestamp(date.getTime());
    }

    /* ===================================================================
       [SECTION 8] MODERN JAVA.TIME INTEROPERABILITY
       =================================================================== */
    public static String formatDate(LocalDate date) {
        return (date == null) ? "" : date.format(DATE_FORMATTER);
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date fromLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}