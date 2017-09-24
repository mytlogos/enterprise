package scrape.sources.novels.strategies;

import Enterprise.misc.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.novels.strategies.intface.ArchiveSearcher;
import scrape.sources.novels.strategies.intface.PostElement;
import scrape.sources.novels.strategies.intface.impl.PostsFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
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

    /**
     * Tests if an available {@link PostsWrapper} is applicable on this {@link Document}.
     * Tests furthermore if it contains Text and if an available {@link PostElement} is available,
     * which is applicable on the Result of the {@code PostWrapper}.
     *
     * @param doc document to test
     * @return true if it is an valid Archive
     */
    private static boolean validArchive(Document doc) {
        PostsWrapper wrapper = PostsWrapper.tryAll(doc);

        if (wrapper == null) {
            return false;
        } else {
            final Element postWrapper = wrapper.apply(doc);

            if (!postWrapper.hasText()) {
                return false;
            } else {
                for (PostElement postElement : new PostsFilter().getFilter()) {
                    Elements elements = postElement.apply(postWrapper);
                    if (elements != null && !elements.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Elements getArchiveElements(Document document) {
        Elements archiveElements = new Elements();
        archiveElements.addAll(document.getElementsByAttributeValueStarting("id", "calendar"));
        archiveElements.addAll(document.getElementsByAttributeValueStarting("id", "archive"));

        return archiveElements;
    }

    private static Document checkWithLink(String uri, int startCounter, int maxMonthsDepth) {
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

    private static Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }

    /**
     * Strategy class for searching and validating an Archive from a site
     * on the internet.
     * These strategies are iterable, returning the page for the next archive,
     * if available.
     * // TODO: 09.09.2017 archives can have a 'NEXT' page (available either through button or scrolldown)
     * // TODO: 09.09.2017 either in here or the scraper itself checks for a 'NEXT'
     */
    private enum ArchiveStrategy implements ArchiveSearcher {
        /**
         * Strategy for searching the given document for an available archive widget.
         */
        WIDGET_ARCHIVE {
            Function<Archive, Boolean> hasNext = archive -> {
                archive.doc = null;
                //terminate if search depth (maxMonth) is smaller than the counter
                if (archive.links.size() <= archive.counter || archive.maxMonthsDepth <= archive.counter) {
                    archive.mainDoc = null;
                    return false;
                } else {
                    String link = archive.links.get(archive.counter);
                    try {
                        archive.doc = getDocument(link);
                    } catch (IOException e) {
                        Log.packageLogger(this.getClass()).log(Level.SEVERE, "error in getting newest archive from widget for " + link, e);
                    }
                }
                archive.counter++;
                if (validArchive(archive.doc)) {
                    return true;
                } else {
                    archive.doc = null;
                    archive.counter = 0;
                    return false;
                }
            };

            @Override
            public Iterator<Document> iterator(Document document) {
                Archive archive = new Archive(
                        hasNext,
                        archiveConsumer -> {
                            Objects.requireNonNull(document);
                            archiveConsumer.mainDoc = document;
                            Elements elements = getArchiveElements(archiveConsumer.mainDoc);
                            archiveConsumer.links = checkWithDocument(archiveConsumer.mainDoc, elements);
                        });

                return archive.iterator();
            }

            @Override
            public Iterator<Document> iterator(Document document, int maxMonthsDepth) {
                Archive archive = new Archive(
                        hasNext,
                        archiveConsumer -> {
                            Objects.requireNonNull(document);

                            archiveConsumer.maxMonthsDepth = maxMonthsDepth;
                            archiveConsumer.mainDoc = document;
                            Elements elements = getArchiveElements(archiveConsumer.mainDoc);
                            archiveConsumer.links = checkWithDocument(archiveConsumer.mainDoc, elements);
                        });

                return archive.iterator();
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
            Function<Archive, Boolean> hasNext = archive -> {
                archive.doc = checkWithLink(archive.mainDoc.baseUri(), archive.counter, archive.maxMonthsDepth);
                if (validArchive(archive.doc)) {
                    archive.counter++;
                    return true;
                } else {
                    archive.doc = null;
                    archive.counter = 0;
                    return false;
                }
            };

            @Override
            public Iterator<Document> iterator(Document document) {
                Archive archive = new Archive(
                        hasNext,
                        archiveConsumer -> {
                            Objects.requireNonNull(document);
                            archiveConsumer.mainDoc = document;
                            Elements elements = getArchiveElements(archiveConsumer.mainDoc);
                            archiveConsumer.links = checkWithDocument(archiveConsumer.mainDoc, elements);
                        });

                return archive.iterator();
            }

            @Override
            public Iterator<Document> iterator(Document document, int maxMonthsDepth) {
                Archive archive = new Archive(
                        hasNext,
                        archiveConsumer -> {
                            Objects.requireNonNull(document);

                            archiveConsumer.maxMonthsDepth = maxMonthsDepth;
                            archiveConsumer.mainDoc = document;
                            Elements elements = getArchiveElements(archiveConsumer.mainDoc);
                            archiveConsumer.links = checkWithDocument(archiveConsumer.mainDoc, elements);
                        });

                return archive.iterator();
            }
        }
    }

    private static class Archive implements Iterable<Document> {
        private Document mainDoc;
        private Document doc;

        private List<String> links = new ArrayList<>();
        private String startUri = "";

        private int maxMonthsDepth = 12;
        private int counter = 0;

        private Function<Archive, Boolean> hasNext = null;

        private Archive(Function<Archive, Boolean> hasNext, Consumer<Archive> init) {
            this.hasNext = hasNext;
            init.accept(this);
        }

        @Override
        public Iterator<Document> iterator() {
            final Archive instance = this;
            return new Iterator<Document>() {
                @Override
                public boolean hasNext() {
                    return hasNext.apply(instance);
                }

                @Override
                public Document next() {
                    return doc;
                }
            };
        }
    }
}

