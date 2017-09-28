package scrape.sources.novels.strategies.intface;

import org.jsoup.nodes.Document;

import java.util.Iterator;

/**
 *
 */
public interface ArchiveSearcher extends Filter {
    Iterator<Document> iterator(Document document);

    Iterator<Document> iterator(Document document, int maxMonthsDepth);
}
