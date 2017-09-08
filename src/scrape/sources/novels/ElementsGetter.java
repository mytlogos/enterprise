package scrape.sources.novels;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


/**
 *
 */
public interface ElementsGetter {
    Elements get(Document document);
}
