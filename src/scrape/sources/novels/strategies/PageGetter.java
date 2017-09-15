package scrape.sources.novels.strategies;

import org.jsoup.nodes.Document;

/**
 *
 */
public interface PageGetter {
    Document getPage(Document document);
}
