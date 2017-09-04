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


    private final static String IS_RELATIVE = "[0-5]?\\d\\s(minutes|seconds|hours|days|weeks|months|years)\\sago";
    private final static String HAS_DATE = "(?=^\\d{4})((\\d{4}\\W[0-1]?\\d\\W[0-3]?\\d))|(?=[a-zA-z]{3,10})([a-zA-z]{3,10}\\s[0-3]?\\d,\\s\\d{4})|([0-3]?\\d\\W[0-1]?\\d\\W\\d{4})";
    private final static String HAS_TIME = "[0-2]?\\d:[0-5]\\d(:[0-5]\\d)?((.?((am)|(pm))).?)?((.?(\\+\\d{2}:00).?)|(.?((GMT)|(UTC)).?-\\d{1,2}).?)?";
    private final static String MMMM_DD_YYYY = "[a-zA-z]{3,10}\\s[0-3]?\\d,\\s\\d{4}";
    private final static String DD_MM_YYYY = "[0-3]?\\d\\W[0-1]?\\d\\W\\d{4}";
    private final static String YYYY_MM_DD = "(\\d{4}\\W[0-1]?\\d\\W[0-3]?\\d)";
    private final static String TIME12 = "[0-2]?\\d:[0-5]\\d(:[0-5]\\d)?(.?(am)|(pm))";
    private final static String TIME24 = "[0-2]?\\d:[0-5]\\d(:[0-5]\\d)?";
    private final static String OFFSETNUMB = "((\\+\\d{2}:00))";
    private final static String OFFSETCHAR = "(((GMT)|(UTC)).?-\\d{1,2})";
    private static final String HAS_OFFSET = "((\\+\\d{2}:00))|(((GMT)|(UTC)).?-\\d{1,2})";
    private static final int maxNumbers = String.valueOf(Instant.now().toEpochMilli()).length() + 1;

    public static LocalDateTime parseTime(String s) {
        LocalDateTime result;
        if (s == null || s.isEmpty()) {
            return null;
        }
        if (patternAvailable(s, HAS_TIME)) {
            result = getDateTime(s);

        } else if (patternAvailable(s, HAS_DATE)) {
            result = getLocalDate(s).atTime(LocalTime.MIDNIGHT);

        } else if (patternAvailable(s, IS_RELATIVE)) {
            result = getRelative(s);

        } else if (isEpoch(s)) {
            result = fromEpoch(s);

        } else {
            throw new DateTimeParseException("could not parse", s, 0);
        }
        return result;
    }

    private static LocalDateTime getRelative(String s) {
        LocalDateTime result;
        LocalDateTime now = LocalDateTime.now();
        String relative = patternFind(s, IS_RELATIVE);

        int number = Integer.parseInt(patternFind(relative, "[0-5]?\\d"));
        if (relative.contains("seconds")) {
            result = now.minusSeconds(number);

        } else if (relative.contains("minutes")) {
            result = now.minusMinutes(number);

        } else if (relative.contains("hours")) {
            result = now.minusHours(number);

        } else if (relative.contains("days")) {
            result = now.minusDays(number);

        } else if (relative.contains("weeks")) {
            result = now.minusWeeks(number);

        } else if (relative.contains("months")) {
            result = now.minusMonths(number);

        } else if (relative.contains("years")) {
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
        } else {
            System.out.println("no offset");
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

                localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US));
            }
        }

        return localDate;
    }

    private static LocalTime getLocalTime(String s) {
        LocalTime localTime;
        String time = patternFind(s, TIME12);
        if (!time.isEmpty()) {
            DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("K:mm[:ss] a").toFormatter();
            localTime = LocalTime.parse(time, dateTimeFormatter);
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
