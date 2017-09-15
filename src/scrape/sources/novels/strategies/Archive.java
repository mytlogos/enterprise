package scrape.sources.novels.strategies;

import Enterprise.misc.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.novels.strategies.intface.impl.PostsFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategy class for searching and validating an Archive from a site
 * on the internet.
 * These strategies are iterable, returning the page for the next archive,
 * if available.
 * // TODO: 09.09.2017 archives can have a 'NEXT' page (available either through button or scrolldown)
 * // TODO: 09.09.2017 either in here or the scraper itself checks for a 'NEXT'
 */
public enum Archive implements Iterable<Document>, Iterator<Document> {
    /**
     * Strategy for searching the given document for an available archive.
     */
    WIDGET_ARCHIVE {
        List<String> links = new ArrayList<>();
        Document mainDoc;
        Document doc;
        int counter = 0;

        @Override
        public Iterator<Document> iterator() {
            return this;
        }

        @Override
        public void init(Document document) {
            Objects.requireNonNull(document);
            mainDoc = document;
            Elements elements = getArchiveElements(mainDoc);
            links = checkWithDocument(mainDoc, elements);
        }

        @Override
        public boolean hasNext() {
            doc = null;
            //terminate if search depth (maxMonth) is smaller than the counter
            if (links.size() <= counter || maxMonthsDepth <= counter) {
                mainDoc = null;
                return false;
            } else {
                String link = links.get(counter);
                try {
                    doc = Archive.getDocument(link);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "error in getting newest archive from widget", e);
                }
            }
            counter++;
            if (validArchive(doc)) {
                return true;
            } else {
                doc = null;
                counter = 0;
                return false;
            }
        }

        @Override
        public Document next() {
            return doc;
        }
    },
    /**
     * Strategy for searching for pages of the given Site, which match a
     * defined scheme for archive website:
     * <p>
     * http://www.host.top-level-domain/YYYY/MM
     * </p>
     * with YYYY being the year in number and MM being the month in numbers.
     * The search starts from the current month backwards till the maximum
     * SearchDepth.
     */
    SITE_ARCHIVE {
        String startUri = "";
        Document doc;
        int counter = 0;

        @Override
        public void init(Document document) {
            startUri = document.location();
        }

        @Override
        public boolean hasNext() {
            doc = Archive.checkWithLink(startUri, counter);
            if (Archive.validArchive(doc)) {
                counter++;
                return true;
            } else {
                doc = null;
                counter = 0;
                return false;
            }
        }

        @Override
        public Document next() {
            return doc;
        }

        @Override
        public Iterator<Document> iterator() {
            return this;
        }
    };
    private static int maxMonthsDepth = 12;
    Logger logger = Log.packageLogger(this.getClass());

    /**
     * Checks if an archive element is available in the {@link Document} or there
     * are sites, based on the location of the {@code Document}, which are valid
     * archives.
     *
     * @param doc document to check for
     * @return true, if an valid archive elements or an valid archive site was found, based
     * on the maximum depth search
     */
    public static boolean hasArchive(Document doc) {
        return !getArchiveElements(doc).isEmpty() || validArchive(checkWithLink(doc.location(), 0));
    }

    private static boolean validArchive(Document doc) {
        Elements wrapperElements = new Elements();
        for (PostsWrapper postsWrapper : PostsWrapper.values()) {
            wrapperElements.add(postsWrapper.apply(doc));
        }

        if (wrapperElements.isEmpty()) {
            return false;
        } else {
            Elements postElements = new Elements();
            for (PostsFilter.Posts posts : PostsFilter.Posts.values()) {
//                postElements.addAll(posts.apply(wrapperElements));
            }

            // TODO: 10.09.2017 maybe check text for further validation
            return !postElements.isEmpty();
        }
    }

    private static Elements getArchiveElements(Document document) {
        Elements archiveElements = new Elements();
        archiveElements.addAll(document.getElementsByAttributeValueStarting("id", "calendar"));
        archiveElements.addAll(document.getElementsByAttributeValueStarting("id", "page"));

        return archiveElements;
    }

    private static Document checkWithLink(String uri, int startCounter) {
        LocalDate now = LocalDate.now();
        int monthValue = now.getMonthValue();
        int year = now.getYear();

        Document doc = new Document("");

        //goes back to 12 months if monthly page does not exist (in cases of inactivity of the blogger)
        for (int i = startCounter; i < maxMonthsDepth; i++) {
            try {
                doc = getMonthArchive(uri, year, monthValue);
                if (validArchive(doc)) {
                    return doc;
                }
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

    private static Document getMonthArchive(String uri, int year, int monthValue) throws IOException {
        String month = monthValue < 10 ? "0" + monthValue : "" + monthValue;
        String monthlyArchive = "/" + year + "/" + month;

        return getDocument(uri + monthlyArchive);
    }

    private static List<String> checkWithDocument(Document document, Elements archiveElements) {
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

        List<String> monthlyLinks = new ArrayList<>();

        for (String link : links) {
            matcher = pattern.matcher(link);
            if (matcher.find()) {
                String completeLink = document.baseUri() + matcher.group().replaceFirst("/", "");
                monthlyLinks.add(completeLink);
            }
        }
        // TODO: 09.09.2017 return a sorted list of links, sorted after the date
        return monthlyLinks;
    }

    private static Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }

    /**
     * Initializes the data of the this {@code Archive}.
     *
     * @param document the start of this strategy, not null
     */
    public abstract void init(Document document);

    /**
     * Sets the maximum search depth for this strategy.
     *
     * @param maxMonthsDepth maximum search depth in number of months
     */
    public void searchDepth(int maxMonthsDepth) {
        Archive.maxMonthsDepth = maxMonthsDepth;
    }
}
