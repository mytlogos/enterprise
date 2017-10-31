package scrape.sources.toc.strategies.intface;

import org.jsoup.nodes.Element;

/**
 *
 */
public interface TocProcessor {
    Element process(String link);
}
