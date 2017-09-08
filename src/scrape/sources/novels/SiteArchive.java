package scrape.sources.novels;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class SiteArchive implements Iterable<Document> {
    private Document archive;
    private boolean isNewest = true;

    private SiteArchive(Document document) {
        archive = document;
    }

    public static boolean hasArchive(Document doc) {
        if (doc.location().equals("https://shintranslations.com/")) {
            System.out.println("is shin");
        }
        Elements archiveElements = getArchiveElements(doc);
        return !archiveElements.isEmpty() || checkWithLink(doc) != null;
    }

    public static SiteArchive archiveSearcher(Document document) {
        return new SiteArchive(document);
    }

    private static Elements getArchiveElements(Document document) {
        Elements archiveElements = new Elements();
        archiveElements.addAll(document.getElementsByAttributeValueStarting("id", "calendar"));
        archiveElements.addAll(document.getElementsByAttributeValueStarting("id", "archive"));

        return archiveElements;
    }

    private static Document checkWithLink(Document document) {
        LocalDate now = LocalDate.now();
        int monthValue = now.getMonthValue();
        int year = now.getYear();

        Document doc = new Document("");

        //goes back to 12 months if monthly archive does not exist (in cases of inactivity of the blogger)
        for (int i = 0; i < 12; i++) {
            try {
                doc = getMonthArchive(document, year, monthValue);
                break;
            } catch (IOException e) {
                if (monthValue == 1) {
                    year--;
                    monthValue = 12;
                } else {
                    monthValue--;
                }
            }
        }
        return doc;
    }

    private static Document getMonthArchive(Document document, int year, int monthValue) throws IOException {
        String month = monthValue < 10 ? "0" + monthValue : "" + monthValue;
        String monthlyArchive = "/" + year + "/" + month;
        return getDocument(document.baseUri() + monthlyArchive);
    }

    private static Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }

    @Override
    public Iterator<Document> iterator() {
        return new ArchiveIterator();
    }

    private int getMonth(String s) {
        Matcher matcher = Pattern.compile("/[0-1]?\\d/").matcher(s);
        int montInt = 0;

        if (matcher.find()) {
            String month = matcher.group().replaceAll("/", "");
            montInt = Integer.parseInt(month);
        }

        return montInt;
    }

    private Document getNextArchive(String location) throws IOException {
        Pattern pattern = Pattern.compile("/2\\d{3}/[0-1]?\\d/");
        Matcher matcher = pattern.matcher(location);

        // TODO: 07.09.2017 shortcut search: search for document archive with links, instead of trying all links

        String newArchive = "";
        String lastArchive = "";
        if (matcher.find()) {
            lastArchive = matcher.group();

            int montInt = getMonth(lastArchive);
            int year = getYear(lastArchive);

            if (montInt == 1) {
                --year;
                montInt = 12;
            } else {
                --montInt;
            }

            newArchive = "/" + year + "/" + montInt + "/";
        }

        if (newArchive.isEmpty()) {
            return new Document("");
        } else {
            return getDocument(location.replace(lastArchive, newArchive));
        }
    }

    private Document getNewestArchive(Document document) throws IOException {
        Elements archiveElements = getArchiveElements(document);

        if (archiveElements.isEmpty()) {
            return checkWithLink(document);
        }

        Set<String> monthlyLinks = checkWithDocument(document, archiveElements);

        return getDocument(getNewest(monthlyLinks));
    }

    private Set<String> checkWithDocument(Document document, Elements archiveElements) {
        List<String> links = new ArrayList<>();
        for (Element archiveElement : archiveElements) {
            links.addAll(archiveElement.getElementsByAttribute("href").eachAttr("abs:href"));
        }
        if (links.isEmpty()) {
            for (Element archiveElement : archiveElements) {
                links.addAll(archiveElement.getElementsByAttribute("value").eachAttr("value"));
            }
        }

        Pattern pattern = Pattern.compile("/2\\d{3}/[0-1]?\\d/?");
        Matcher matcher;

        Set<String> monthlyLinks = new HashSet<>();

        for (String link : links) {
            matcher = pattern.matcher(link);
            if (matcher.find()) {
                String completeLink = document.baseUri() + matcher.group().replaceFirst("/", "");
                monthlyLinks.add(completeLink);
            }
        }
        return monthlyLinks;
    }

    private String getNewest(Collection<String> collection) {
        String result = "";
        int max = 0;

        Pattern monthPattern = Pattern.compile("/[0-1]?\\d/?$");

        for (String s : collection) {

            if (sameYears(result, s)) {
                Matcher matcher = monthPattern.matcher(s);
                if (matcher.find()) {

                    String temp = matcher.group();
                    temp = temp.replaceAll("/", "");

                    int tempInt = Integer.parseInt(temp);

                    if (tempInt > max) {
                        result = s;
                        max = tempInt;
                    }
                }
            } else {
                if (getYear(result) < getYear(s)) {
                    Matcher matcher = monthPattern.matcher(s);
                    if (matcher.find()) {

                        String temp = matcher.group();
                        temp = temp.replaceAll("/", "");

                        int tempInt = Integer.parseInt(temp);

                        if (tempInt > max) {
                            result = s;
                            max = tempInt;
                        }
                    }
                }
            }

        }

        return result;
    }

    private int getYear(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        Pattern yearPattern = Pattern.compile("/2\\d{3}/");
        Matcher yearMatcher = yearPattern.matcher(s);

        int result = 0;
        if (yearMatcher.find()) {
            String year = yearMatcher.group();
            year = year.replaceAll("/", "");
            result = Integer.parseInt(year);
        }
        return result;
    }

    private boolean sameYears(String result, String s) {
        boolean sameYear = false;
        if (result == null) {
            return false;
        }

        Pattern yearPattern = Pattern.compile("/2\\d{3}/");
        Matcher yearMatcher = yearPattern.matcher(s);

        if (yearMatcher.find()) {
            String year = yearMatcher.group();
            sameYear = result.contains(year);
        }
        return sameYear;
    }

    private class ArchiveIterator implements Iterator<Document> {

        @Override
        public boolean hasNext() {
            boolean hasNext;
            try {
                if (isNewest) {
                    archive = getNewestArchive(archive);
                    hasNext = archive.hasText();
                    isNewest = false;
                } else {
                    archive = getNextArchive(archive.location());
                    hasNext = archive.hasText();
                }
            } catch (IOException e) {
                return false;
            }
            return hasNext;
        }

        @Override
        public Document next() {
            return archive;
        }


    }
}
