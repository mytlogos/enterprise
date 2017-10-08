package scrape.sources.posts.strategies;

import org.jsoup.nodes.Document;
import scrape.sources.posts.strategies.intface.Filter;

/**
 *
 */
public interface PageGetter extends Filter {
    Document getPage(Document document);
}
