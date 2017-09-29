package scrape.sources.novels.strategies;

import org.jsoup.nodes.Document;
import scrape.sources.novels.strategies.intface.Filter;

/**
 *
 */
public interface PageGetter extends Filter {
    Document getPage(Document document);
}
