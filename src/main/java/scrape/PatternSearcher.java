package scrape;

import enterprise.test.testScraper;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class PatternSearcher {
    public static void main(String[] args) {
        searchLinks(getTocLinks());
    }

    public static List<String> searchLinks(List<String> links) {
        Map<String, String> mappedLinks = new HashMap<>();

        for (String s : links) {
            URI uri = URI.create(s);
//            String path = uri.getPath().replaceAll("/\\d{4}/\\d{2}(/\\d{2})", "");
            mappedLinks.put(uri.getPath(), uri.getHost());
        }

        Set<String> patterns = new HashSet<>();

        createPatterns(mappedLinks.keySet(), patterns);

        Map<String, Integer> hits = new HashMap<>();

        List<Map.Entry<String, Integer>> entries = getHits(mappedLinks.keySet(), patterns, hits);

//        entries.forEach(System.out::println);

        printNotHit(mappedLinks.keySet(), entries);

        return getResult(links, entries);
    }

    private static List<String> getTocLinks() {
        String mistakes = "http://imgur.com/DQL0KHd,E2Fmz85,ZXy33mC,Dc8bZa1,vspQ9Fh,0YG978i\n" +
                "https://pirateyoshi.wordpress.com/gcr-toc/gcr-illustrations/\n" +
                "https://pirateyoshi.wordpress.com/gcr-prologue/\n" +
                "https://zirusmusings.com/gcr-ch1/\n" +
                "https://zirusmusings.com/gcr-ch1-pt1/\n" +
                "https://zirusmusings.com/gcr-ch1-pt2/\n" +
                "https://zirusmusings.com/gcr-ch2/\n" +
                "https://zirusmusings.com/gcr-ch2-pt1/\n" +
                "https://zirusmusings.com/gcr-ch2-pt2/\n" +
                "https://zirusmusings.com/gcr-ch2-pt3/\n" +
                "https://zirusmusings.com/gcr-ch2-pt4/\n" +
                "https://zirusmusings.com/gcr-ch2-pt5/\n" +
                "https://zirusmusings.com/gcr-ch3/\n" +
                "https://zirusmusings.com/gcr-ch3-pt1/\n" +
                "https://zirusmusings.com/gcr-ch3-pt2/\n" +
                "https://zirusmusings.com/gcr-ch3-pt3/\n" +
                "https://zirusmusings.com/gcr-ch3-pt4/\n" +
                "https://zirusmusings.com/gcr-ch3-pt5/\n" +
                "https://zirusmusings.com/gcr-ch3-pt6/\n" +
                "https://zirusmusings.com/gcr-ch3-pt7/\n" +
                "https://zirusmusings.com/gcr-ch3-pt8/\n" +
                "https://zirusmusings.com/gcr-ch3-pt9/\n" +
                "https://zirusmusings.com/gcr-ch3-pt10/\n" +
                "https://zirusmusings.com/gcr-ch3-pt11/\n" +
                "https://zirusmusings.com/gcr-ch3-pt12/\n" +
                "https://zirusmusings.com/gcr-epilogue/\n" +
                "https://zirusmusings.com/gcr-ss/\n" +
                "https://zirusmusings.com/gcr-ss-pt1/\n" +
                "https://zirusmusings.com/gcr-ss-pt2/\n" +
                "https://zirusmusings.com/gcr2-illustrations/\n" +
                "https://pirateyoshi.wordpress.com/gcr2-prologue/\n" +
                "https://zirusmusings.com/gcr2-ch1/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt1/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt2/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt3/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt4/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt5/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt6/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt7/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt8/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt9/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt10/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt11/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt12/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt13/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt14/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt15/\n" +
                "https://zirusmusings.com/gcr2-ch1-pt16/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt1/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt2/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt3/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt4/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt5/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt6/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt7/\n" +
                "https://zirusmusings.com/gcr2-ch2-pt8/\n" +
                "https://zirusmusings.com/gcr2-ch3-pt1/\n" +
                "https://zirusmusings.com/gcr2-ch3-pt2/\n" +
                "https://zirusmusings.com/gcr2-ch3-pt3/\n" +
                "https://zirusmusings.com/gcr2-5-illustrations/\n" +
                "https://zirusmusings.com/gcr3-illustrations/\n" +
                "https://zirusmusings.com/gcr4-illustrations/\n" +
                "http://www.novelupdates.com/series/the-guilds-cheat-receptionist/\n" +
                "http://booklive.jp/product/index/title_id/282541/vol_no/001\n" +
                "http://www.amazon.co.jp/%E3%82%AE%E3%83%AB%E3%83%89%E3%81%AE%E3%83%81%E3%83%BC%E3%83%88%E3%81%AA%E5%8F%97%E4%BB%98%E5%AC%A2-1-%E3%83%A2%E3%83%B3%E3%82%B9%E3%82%BF%E3%83%BC%E6%96%87%E5%BA%AB-%E5%A4%8F%E3%81%AB%E3%82%B3%E3%82%BF%E3%83%84/dp/4575750085/ref=sr_1_2?ie=UTF8&qid=1465133683&sr=8-2&keywords=%E3%82%AE%E3%83%AB%E3%83%89%E3%81%AE%E3%83%81%E3%83%BC%E3%83%88%E3%81%AA%E5%8F%97%E4%BB%98%E5%AC%A2";

        return new ArrayList<>(Arrays.asList(mistakes.split("\\n")));
    }

    private static void createPatterns(Collection<String> paths, Collection<String> patterns) {
        for (String path : paths) {
            StringBuilder patternBuilder = new StringBuilder();
            String[] splitPath = path.split("/");

            for (int i = splitPath.length - 1; i > 0; i--) {
                String s = splitPath[i];
                if (!s.isEmpty()) {
                    if (s.chars().allMatch(Character::isDigit)) {
                        patternBuilder.insert(0, "\\d+").insert(0, "/");
                    } else {
                        addLinkPattern(patterns, patternBuilder, s);
                    }
                }
            }
            patterns.add(patternBuilder.toString());
        }
    }

    private static void addLinkPattern(Collection<String> patterns, StringBuilder pattern, String s) {
        Pattern separator = Pattern.compile("[^a-zA-Z]");
        String[] splitResource = s.split(separator.pattern());

        List<StringBuilder> builders = new ArrayList<>();
        builders.add(new StringBuilder(pattern.toString()));

        for (int i = splitResource.length - 1; i > 0; i--) {
            String string = splitResource[i];

            if (!string.isEmpty()) {

                builders.add(new StringBuilder());
                Matcher matcher = separator.matcher(s);

                if (matcher.find()) {
                    for (StringBuilder builder : builders) {
                        builder.insert(0, string);
                        patterns.add(builder.toString());
                        patterns.add(string);

                        String separatorPattern = matcher.group();

                        builder.insert(0, Pattern.quote(separatorPattern));
                        patterns.add(builder.toString());
                    }
                }
            }
        }
        patterns.addAll(builders.stream().map(StringBuilder::toString).collect(Collectors.toList()));
        patterns.addAll(new ArrayList<>(Arrays.asList(splitResource)));
    }

    public static List<String> searchText(List<String> texts) {
        Set<String> patterns = new HashSet<>();

        for (String text : texts) {
            addTextPattern(patterns, new StringBuilder(), text);
        }
//        patterns.forEach(System.out::println);

        Map<String, Integer> hits = new HashMap<>();

        //no patterns smaller than three letters
        patterns = patterns.stream().filter(s -> s.length() > 3).collect(Collectors.toSet());

        Pattern pattern = Pattern.compile("(ch(apter)?).?\\d{1,4}", Pattern.CASE_INSENSITIVE);
        Set<String> filteredPatterns = patterns.stream().filter(s -> pattern.matcher(s).find()).collect(Collectors.toSet());

        List<Map.Entry<String, Integer>> entryList = getHits(texts, filteredPatterns, hits);

        if (entryList.isEmpty()) {
            entryList = getHits(texts, patterns, hits);
        }

        printNotHit(texts, entryList);
        return getResult(texts, entryList);
    }

    /**
     * Generates Patterns from Word-Fragments. A Substring is treated
     * as word, as long as it contains only characters of the Alphabet.
     *
     * @param patterns
     * @param pattern
     * @param s
     */
    private static void addTextPattern(Collection<String> patterns, StringBuilder pattern, String s) {
        Pattern separator = Pattern.compile("[\\W]");
        String[] splitResource = s.split(separator.pattern());

        StringBuilder possiblePattern = new StringBuilder(pattern.toString());

        for (String string : splitResource) {

            if (!string.isEmpty()) {
                Matcher matcher = separator.matcher(s);

                if (matcher.find()) {
                    if (string.chars().allMatch(Character::isDigit)) {
                        possiblePattern.append("\\d+");
                    } else {
                        possiblePattern.append(string);
                    }
                    patterns.add(possiblePattern.toString());

                    String separatorPattern = matcher.group();

                    possiblePattern.append(Pattern.quote(separatorPattern));
                    patterns.add(possiblePattern.toString());
                }
            }
        }
    }

    private static List<Map.Entry<String, Integer>> getHits(Collection<String> paths, Collection<String> patterns, Map<String, Integer> hits) {
        patterns = patterns.stream().filter(PatternSearcher::isValidPattern).collect(Collectors.toList());
//        List<String> filterPattern = filterPattern(patterns);
        for (String s : patterns) {
            hitPattern(paths, hits, s);
        }
        List<Map.Entry<String, Integer>> entries = testScraper.sortByValue(hits);
        /*Map.Entry<String, Integer> entry = getMax(entries);

        if (entry != null && entry.getValue() * 5 < paths.size()) {
            for (String pattern : patterns) {
                hitPattern(paths, hits, pattern);
            }
        }*/
        return entries;
    }

    private static void printNotHit(Collection<String> paths, List<Map.Entry<String, Integer>> entries) {
        if (!entries.isEmpty()) {
            Map.Entry<String, Integer> entry = entries.get(entries.size() - 1);
            String mostHitsPattern = entry.getKey();

            Pattern designed = Pattern.compile(mostHitsPattern);
//            System.out.println("Pattern: " + mostHitsPattern + " Hits: " + entry.getValue());

            for (String path : paths) {
                if (!designed.matcher(path).find()) {
//                    System.out.println("no hit for " + path);
                }
            }
        } else {
            for (String path : paths) {
//                System.out.println("no hit for " + path);
            }
        }
    }

    private static List<String> getResult(List<String> links, List<Map.Entry<String, Integer>> entries) {
        if (!entries.isEmpty()) {
            Map.Entry<String, Integer> entry = entries.get(entries.size() - 1);
            String mostHitsPattern = entry.getKey();

            Pattern designedPattern = Pattern.compile(mostHitsPattern);

            List<String> result = new ArrayList<>();
            for (String link : links) {
                if (designedPattern.matcher(link).find()) {
                    result.add(link);
                }
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    private static void hitPattern(Collection<String> paths, Map<String, Integer> hits, String s) {
        Pattern designed = Pattern.compile(s);
        int patternHits = 0;
        for (String path : paths) {
            if (designed.matcher(path).find()) {
                patternHits++;
            }
        }
        hits.put(s, patternHits);
    }

    private static Map.Entry<String, Integer> getMax(List<Map.Entry<String, Integer>> entries) {
        if (!entries.isEmpty()) {
            return entries.get(entries.size() - 1);
        }
        return null;
    }

    private static List<String> filterPattern(List<String> patterns) {
        List<String> strings = patterns
                .stream()
                .filter(PatternSearcher::isValidPattern)
                .filter(s -> s.contains("chapter"))
                .collect(Collectors.toList());
        patterns.removeAll(strings);
        return strings;
    }

    private static boolean isValidPattern(String s) {
        return !s.isEmpty()
                && (s.length() > 1)
                && !s.matches("\\\\Q.\\\\E")
                && !s.chars().allMatch(Character::isDigit)
                && !s.matches("/\\\\d\\+(/\\\\d\\+)?(/\\\\d\\+)?")
                && !s.matches("(\\\\Q.\\\\E|\\.)?html");
    }
}
