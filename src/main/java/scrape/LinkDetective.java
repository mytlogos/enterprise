package scrape;

import org.jsoup.Jsoup;
import scrape.sources.posts.ParseTime;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class LinkDetective {
    public static boolean validChapterLink(String link) {
        Pattern pattern = Pattern.compile("(chapter|ch)-\\d{1,5}|volume-\\d{1,5}|v\\d{1,5}c\\d{1,5}");
        return pattern.matcher(link).find();
    }

    public static boolean sameTheme(String before, String nextLink) {
        Pattern pattern = Pattern.compile("/[a-z-1-9]*[-/](?:chapter|episode|ch)");
        // TODO: 07.10.2017
        return false;
    }

    public String tryNextLink(String currentLink) {
        String result;
        if (ParseTime.patternAvailable(currentLink, "\\d{4}/[0-1]?\\d/([0-3]?\\d)?")) {
            result = changeDatePath();
        } else {
            result = changeNormalPath(currentLink, Operate.NEXT);
        }
        return result;
    }

    private String changeDatePath() {
        return "";
    }

    private String changeNormalPath(String currentLink, Operate operate) {
        String result = null;
        try {
            URI uri = new URI(currentLink);
            String path = uri.getPath();
            String[] pathParts = path.split("/");

            for (int i = 0, pathPartsLength = pathParts.length; i < pathPartsLength; i++) {
                String pathPart = pathParts[i];
                Pattern full = Pattern.compile("(chapter|ch)-\\d{1,5}");
                Pattern abbr = Pattern.compile("v\\d{1,5}c\\d{1,5}");

                if (full.matcher(pathPart).find()) {
                    Pattern volume = Pattern.compile("volume-\\d{1,5}");

                    result = matchPattern(full, volume, operate, uri, pathParts, i, pathPart);
                } else if (abbr.matcher(pathPart).find()) {
                    Pattern volume = Pattern.compile("v\\d{1,5}");
                    Pattern chapter = Pattern.compile("c\\d{1,5}");

                    result = matchPattern(chapter, volume, operate, uri, pathParts, i, pathPart);
                }
            }
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private String matchPattern(Pattern chapter, Pattern volume, Operate operate, URI uri, String[] pathParts, int i, String pathPart) {
        String result = "";
        Matcher matcher = chapter.matcher(pathPart);

        if (matcher.find()) {

            String group = matcher.group();
            String changed = changeNumber(pathParts, i, pathPart, chapter, group, operate);
            result = uri.getScheme() + "://" + uri.getHost() + changed;
            try {
                Jsoup.connect(result).get();
            } catch (IOException e) {
                if (volume.matcher(pathPart).find()) {
                    matcher = volume.matcher(pathPart);

                    if (matcher.find()) {

                        group = matcher.group();
                        changed = changeNumber(pathParts, i, pathPart, volume, group, operate);
                        result = uri.getScheme() + "://" + uri.getHost() + changed;
                        try {
                            Jsoup.connect(result).get();
                        } catch (IOException e1) {
                            result = "";
                        }
                    }
                }
            }
        }
        return result;
    }

    private String changeNumber(String[] pathParts, int i, String pathPart, Pattern pattern, String part, Operate operate) {
        Pattern numberPattern = Pattern.compile("\\d+");
        Matcher numberMatcher = numberPattern.matcher(part);

        if (numberMatcher.find()) {

            String chapterNumber = numberMatcher.group();
            if (!chapterNumber.isEmpty()) {
                part = part.substring(0, part.indexOf(chapterNumber));
                int numberOfChapter = Integer.parseInt(chapterNumber);
                String newChapter = part + operate.operate(numberOfChapter);
                pathParts[i] = pattern.matcher(pathPart).replaceFirst(newChapter);

                StringBuilder builder = new StringBuilder();
                for (String s : pathParts) {
                    builder.append("/").append(s);
                }
                return builder.toString();
            }
        }
        return pathPart;
    }

    public String tryPrevLink(String currentLink) {
        String result;
        if (ParseTime.patternAvailable(currentLink, "\\d{4}/[0-1]?\\d/([0-3]?\\d)?")) {
            result = changeDatePath();
        } else {
            result = changeNormalPath(currentLink, Operate.PREVIOUS);
        }
        return result;
    }

    private enum Operate {
        NEXT {
            @Override
            int operate(int i) {
                return Math.incrementExact(i);
            }
        },
        PREVIOUS {
            @Override
            int operate(int i) {
                return Math.decrementExact(i);
            }
        },;

        abstract int operate(int i);
    }
}
