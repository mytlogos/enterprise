package scrape.sources.posts.strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.SourceAccessor;
import scrape.sources.posts.strategies.intface.ArchiveSearcher;
import scrape.sources.posts.strategies.intface.PostElement;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArchiveGetter {

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
        return !getArchiveElements(doc).isEmpty() || validArchive(checkWithLink(doc.location(), 0, 12));
    }

    static Elements getArchiveElements(Document document) {
        Elements archiveElements = new Elements();
        archiveElements.addAll(document.getElementsByAttributeValueStarting("id", "calendar"));
        archiveElements.addAll(document.getElementsByAttributeValueStarting("id", "archive"));

        return archiveElements;
    }

    static Document checkWithLink(String uri, int startCounter, int maxMonthsDepth) {
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

        return SourceAccessor.getDocument(uri + monthlyArchive);
    }

    /**
     * Tests if an available {@link PostWrapper} is applicable on this {@link Document}.
     * Tests furthermore if it contains Text and if an available {@link PostElement} is available,
     * which is applicable on the Result of the {@code PostWrapper}.
     *
     * @param doc document to test
     * @return true if it is an valid Archive
     */
    static boolean validArchive(Document doc) {
        PostWrapper wrapper = PostWrapper.tryAll(doc);

        if (wrapper == null) {
            return false;
        } else {
            final Element postWrapper = wrapper.apply(doc);

            if (!postWrapper.hasText()) {
                return false;
            } else {
                for (PostElement postElement : Filters.getPostsFilter()) {
                    Elements elements = postElement.apply(postWrapper);
                    if (elements != null && !elements.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<ArchiveSearcher> getFilter() {
        return new ArrayList<>(Arrays.asList(ArchiveStrategy.values()));
    }

    /**
     * @param document
     * @return
     */
    public static ArchiveSearcher getArchiveSearcher(Document document) {
        for (ArchiveStrategy archiveStrategy : ArchiveStrategy.values()) {
            Iterator<Document> iterator = archiveStrategy.iterator(document);

            if (iterator.hasNext()) {
                return archiveStrategy;
            }
        }
        return null;
    }

    /**
     * @param document
     * @param maxMonthsDepth
     * @return
     */
    public static ArchiveSearcher getArchiveSearcher(Document document, int maxMonthsDepth) {
        for (ArchiveStrategy archiveStrategy : ArchiveStrategy.values()) {
            Iterator<Document> iterator = archiveStrategy.iterator(document, maxMonthsDepth);

            if (iterator.hasNext()) {
                return archiveStrategy;
            }
        }
        return null;
    }

    static List<String> checkWithDocument(Document document, Elements archiveElements) {
        List<String> links = new ArrayList<>();
        for (Element archiveElement : archiveElements) {
            links.addAll(archiveElement.getElementsByAttribute("href").eachAttr("abs:href"));
        }
        if (links.isEmpty()) {
            for (Element archiveElement : archiveElements) {
                links.addAll(archiveElement.getElementsByAttribute("value").eachAttr("value"));
            }
        }

        Pattern pattern = Pattern.compile("/2\\d{3}/[0-1]?\\d/?$");
        Matcher matcher;

        List<String> monthlyLinks = new ArrayList<>();

        for (String link : links) {
            matcher = pattern.matcher(link);
            if (matcher.find()) {
                String completeLink = document.baseUri() + matcher.group();
                monthlyLinks.add(completeLink);
            }
        }
        return monthlyLinks;
    }

    static class Archive implements Iterable<Document>, Iterator<Document> {
        private Document mainDoc;
        private Document doc;
        private boolean getNext = false;
        private boolean hasNext = false;

        private List<String> links = new ArrayList<>();
        private String startUri = "";

        private int maxMonthsDepth = 12;
        private int counter = 0;

        private Function<Archive, Boolean> hasNextFunction;

        Archive(Function<Archive, Boolean> hasNext, Consumer<Archive> init) {
            this.setHasNextFunction(hasNext);
            init.accept(this);
        }

        @Override
        public Iterator<Document> iterator() {
            return this;
        }

        @Override
        public boolean hasNext() {
            if (isGetNext()) {
                setGetNext(false);
                return hasNext = getHasNextFunction().apply(this);
            } else {
                return isHasNext();
            }
        }

        @Override
        public Document next() {
            setGetNext(true);
            return getDoc();
        }

        Document getDoc() {
            return doc;
        }

        void setDoc(Document doc) {
            this.doc = doc;
        }

        boolean isGetNext() {
            return getNext;
        }

        void setGetNext(boolean getNext) {
            this.getNext = getNext;
        }

        Function<Archive, Boolean> getHasNextFunction() {
            return hasNextFunction;
        }

        boolean isHasNext() {
            return hasNext;
        }

        void setHasNextFunction(Function<Archive, Boolean> hasNextFunction) {
            this.hasNextFunction = hasNextFunction;
        }

        Document getMainDoc() {
            return mainDoc;
        }

        void setMainDoc(Document mainDoc) {
            this.mainDoc = mainDoc;
        }

        List<String> getLinks() {
            return links;
        }

        void setLinks(List<String> links) {
            this.links = links;
        }

        int getMaxMonthsDepth() {
            return maxMonthsDepth;
        }

        void setMaxMonthsDepth(int maxMonthsDepth) {
            this.maxMonthsDepth = maxMonthsDepth;
        }

        int getCounter() {
            return counter;
        }

        void setCounter(int counter) {
            this.counter = counter;
        }
    }
}

