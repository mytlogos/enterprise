package scrape.sources.posts.strategies;

import enterprise.data.Default;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import scrape.sources.SourceAccessor;
import scrape.sources.posts.strategies.intface.ArchiveSearcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Strategy class for searching and validating an Archive from a site
 * on the internet.
 * These strategies are iterable, returning the page for the next archive,
 * if available.
 * // TODO: 09.09.2017 archives can have a 'NEXT' page (available either through button or scrolldown)
 * // TODO: 09.09.2017 either in here or the web itself checks for a 'NEXT'
 */
public enum ArchiveStrategy implements ArchiveSearcher {
    /**
     * Strategy for searching the given document for an available archive widget.
     */
    WIDGET_ARCHIVE {
        final Function<ArchiveGetter.Archive, Boolean> hasNext = archive -> {
            archive.setDoc(null);
            //terminate if search depth (maxMonth) is smaller than the counter
            if (archive.getLinks().size() <= archive.getCounter() || archive.getMaxMonthsDepth() <= archive.getCounter()) {
                archive.setMainDoc(null);
                return false;
            } else {
                String link = archive.getLinks().get(archive.getCounter());
                try {
                    archive.setDoc(SourceAccessor.getDocument(link));
                } catch (IOException e) {
                    Default.LOGGER.log(Level.SEVERE, "exception occurred in getting newest archive from widget for " + link, e);
                }
            }
            archive.setCounter(archive.getCounter() + 1);
            if (ArchiveGetter.validArchive(archive.getDoc())) {
                return true;
            } else {
                archive.setDoc(null);
                archive.setCounter(0);
                return false;
            }
        };

        @Override
        public Iterator<Document> iterator(Document document) {
            ArchiveGetter.Archive archive = new ArchiveGetter.Archive(
                    hasNext,
                    archiveConsumer -> {
                        Objects.requireNonNull(document);
                        archiveConsumer.setMainDoc(document);
                        Elements elements = ArchiveGetter.getArchiveElements(archiveConsumer.getMainDoc());
                        archiveConsumer.setLinks(ArchiveGetter.checkWithDocument(archiveConsumer.getMainDoc(), elements));
                    });

            return archive.iterator();
        }

        @Override
        public Iterator<Document> iterator(Document document, int maxMonthsDepth) {
            ArchiveGetter.Archive archive = new ArchiveGetter.Archive(
                    hasNext,
                    archiveConsumer -> {
                        Objects.requireNonNull(document);

                        archiveConsumer.setMaxMonthsDepth(maxMonthsDepth);
                        archiveConsumer.setMainDoc(document);
                        Elements elements = ArchiveGetter.getArchiveElements(archiveConsumer.getMainDoc());
                        archiveConsumer.setLinks(ArchiveGetter.checkWithDocument(archiveConsumer.getMainDoc(), elements));
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
        final Function<ArchiveGetter.Archive, Boolean> hasNext = archive -> {
            archive.setDoc(ArchiveGetter.checkWithLink(archive.getMainDoc().baseUri(), archive.getCounter(), archive.getMaxMonthsDepth()));
            if (ArchiveGetter.validArchive(archive.getDoc())) {
                archive.setCounter(archive.getCounter() + 1);
                return true;
            } else {
                archive.setDoc(null);
                archive.setCounter(0);
                return false;
            }
        };

        @Override
        public Iterator<Document> iterator(Document document) {
            ArchiveGetter.Archive archive = new ArchiveGetter.Archive(
                    hasNext,
                    archiveConsumer -> {
                        Objects.requireNonNull(document);
                        archiveConsumer.setMainDoc(document);
                        Elements elements = ArchiveGetter.getArchiveElements(archiveConsumer.getMainDoc());
                        archiveConsumer.setLinks(ArchiveGetter.checkWithDocument(archiveConsumer.getMainDoc(), elements));
                    });

            return archive.iterator();
        }

        @Override
        public Iterator<Document> iterator(Document document, int maxMonthsDepth) {
            ArchiveGetter.Archive archive = new ArchiveGetter.Archive(
                    hasNext,
                    archiveConsumer -> {
                        Objects.requireNonNull(document);

                        archiveConsumer.setMaxMonthsDepth(maxMonthsDepth);
                        archiveConsumer.setMainDoc(document);
                        Elements elements = ArchiveGetter.getArchiveElements(archiveConsumer.getMainDoc());
                        archiveConsumer.setLinks(ArchiveGetter.checkWithDocument(archiveConsumer.getMainDoc(), elements));
                    });

            return archive.iterator();
        }
    }
}
