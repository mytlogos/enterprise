package scrape.sources.novels;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * Utility class for parsing time.
 */
public class ParseTime {


    private final static String IS_RELATIVE = "[0-5]?\\d\\s(minute(s)?|second(s)?|hour(s)?|day(s)?|week(s)?|month(s)?|year(s)?)\\sago";
    private final static String HAS_DATE = "(?=^\\d{4})((\\d{4}\\W[0-1]?\\d\\W[0-3]?\\d))|(?=[a-zA-z]{3,10})([a-zA-z]{3,10}\\s[0-3]?\\d,\\s\\d{4})|(?=[0-3]?\\d\\s[a-zA-z]{3,10})([0-3]?\\d\\s[a-zA-z]{3,10}\\s\\d{4})|([0-3]?\\d\\W[0-1]?\\d\\W\\d{4})";
    private final static String HAS_TIME = "[0-2]?\\d:[0-5]\\d(:[0-5]\\d)?((.?((am)|(pm))).?)?((.?(\\+\\d{2}:00).?)|(.?((GMT)|(UTC)).?-\\d{1,2}).?)?";
    private final static String MMMM_DD_YYYY = "[a-zA-z]{3,10}\\s[0-3]?\\d,\\s\\d{4}";
    private final static String DD_MMMM_YYYY = "[0-3]?\\d\\s[a-zA-z]{3,10}\\s\\d{4}";
    private final static String DD_MM_YYYY = "[0-3]?\\d\\W[0-1]?\\d\\W\\d{4}";
    private final static String YYYY_MM_DD = "(\\d{4}[^a-zA-Z0-9:][0-1]?\\d[^a-zA-Z0-9:][0-3]?\\d)";
    private final static String TIME12 = "[0-2]?\\d:[0-5]\\d(:[0-5]\\d)?(.?(am)|.?(pm))";
    private final static String TIME24 = "[0-2]?\\d:[0-5]\\d(:[0-5]\\d)?";
    private final static String OFFSETNUMB = "((\\+\\d{2}:00))";
    private final static String OFFSETCHAR = "(((GMT)|(UTC)).?-\\d{1,2})";
    private static final String HAS_OFFSET = "((\\+\\d{2}:00))|(((GMT)|(UTC)).?-\\d{1,2})";
    private static final int maxNumbers = String.valueOf(Instant.now().toEpochMilli()).length() + 1;

    public static boolean isParseAble(String s) {
        // TODO: 14.09.2017 do sth better
        try {
            return parseTime(s) != null;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDateTime parseTime(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        if (hasTime(s)) {
            return getDateTime(s);

        } else if (hasDate(s)) {
            return getLocalDate(s).atTime(LocalTime.MIDNIGHT);

        } else if (hasRelative(s)) {
            return getRelative(s);

        } else if (isEpoch(s)) {
            return fromEpoch(s);

        } else {
            throw new DateTimeParseException("could not parse", s, 0);
        }
    }

    private static boolean hasTime(String s) {
        return patternAvailable(s, HAS_TIME);
    }

    public static boolean hasDate(String s) {
        return patternAvailable(s, HAS_DATE);
    }

    private static boolean hasRelative(String s) {
        return patternAvailable(s, IS_RELATIVE);
    }

    private static boolean hasDate(String s, String hasDate) {
        return patternAvailable(s, hasDate);
    }

    private static LocalDateTime getRelative(String s) {
        LocalDateTime result;
        LocalDateTime now = LocalDateTime.now();
        String relative = patternFind(s, IS_RELATIVE);

        int number = Integer.parseInt(patternFind(relative, "[0-5]?\\d"));
        if (relative.contains("second")) {
            result = now.minusSeconds(number);

        } else if (relative.contains("minute")) {
            result = now.minusMinutes(number);

        } else if (relative.contains("hour")) {
            result = now.minusHours(number);

        } else if (relative.contains("day")) {
            result = now.minusDays(number);

        } else if (relative.contains("week")) {
            result = now.minusWeeks(number);

        } else if (relative.contains("month")) {
            result = now.minusMonths(number);

        } else if (relative.contains("year")) {
            result = now.minusYears(number);

        } else {
            result = null;
        }
        return result;
    }

    private static LocalDateTime getDateTime(String s) {
        LocalDateTime dateTime;

        ZoneOffset offset = getOffset(s);

        LocalTime time = getLocalTime(s);

        if (offset != null) {
            OffsetTime offsetTime = time.atOffset(offset);
            time = offsetTime.toLocalTime();
        }

        LocalDate date = getLocalDate(s);

        if (time != null) {
            dateTime = LocalDateTime.of(date, time);
        } else {
            dateTime = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        }

        return dateTime;
    }

    private static LocalDate getLocalDate(String s) {
        LocalDate localDate;
        String date = patternFind(s, YYYY_MM_DD);
        if (!date.isEmpty()) {

            localDate = LocalDate.parse(date);
        } else {
            date = patternFind(s, DD_MM_YYYY);
            if (!date.isEmpty()) {
                localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d.M.uuuu"));
            } else {
                date = patternFind(s, MMMM_DD_YYYY);
                if (!date.isEmpty()) {
                    localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US));
                } else {
                    date = patternFind(s, DD_MMMM_YYYY);
                    localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("d MMMM uuuu"));
                }
            }
        }

        return localDate;
    }

    private static LocalTime getLocalTime(String s) {
        LocalTime localTime;
        String time = patternFind(s, TIME12);
        if (!time.isEmpty()) {
            try {
                DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("K:mm[:ss] a").toFormatter();
                localTime = LocalTime.parse(time, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("h:mm[:ss] a").toFormatter();
                localTime = LocalTime.parse(time, dateTimeFormatter);
            }
        } else {
            time = patternFind(s, TIME24);
            if (!time.isEmpty()) {
                localTime = LocalTime.parse(time);
            } else {
                localTime = null;
            }
        }
        return localTime;
    }

    private static ZoneOffset getOffset(String s) {
        ZoneOffset zoneOffset;

        if (patternAvailable(s, HAS_OFFSET)) {
            String offset = patternFind(s, OFFSETNUMB);

            if (!offset.isEmpty()) {
                zoneOffset = ZoneOffset.of(offset);
            } else {

                offset = patternFind(s, OFFSETCHAR);
                String remove = patternFind(offset, "[a-zA-Z]*");
                offset = offset.replace(remove, "").trim();

                zoneOffset = ZoneOffset.of(offset);
            }
        } else {
            zoneOffset = null;
        }
        return zoneOffset;
    }

    private static boolean isEpoch(String s) {
        Pattern pattern = compile("\\d{1," + maxNumbers + "}");
        return pattern.matcher(s).matches();
    }

    private static LocalDateTime fromEpoch(String s) {
        return LocalDateTime.ofEpochSecond(Integer.parseInt(s), 0, ZoneOffset.UTC);
    }

    private static String patternFind(String s, String regex) {
        Pattern pattern = compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.find() ? matcher.group() : "";
    }

    private static boolean patternAvailable(String s, String regex) {
        Pattern pattern = compile(regex);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }
}
