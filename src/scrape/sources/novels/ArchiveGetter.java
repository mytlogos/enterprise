package scrape.sources.novels;

import org.jsoup.nodes.Document;

/**
 *
 */
public interface ArchiveGetter {
    Document getArchive(Document document);
}
