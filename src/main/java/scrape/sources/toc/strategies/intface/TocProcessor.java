package scrape.sources.toc.strategies.intface;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 */
public interface TocProcessor {
    Element process(Document document);

    void processStructure(Document document);
}
