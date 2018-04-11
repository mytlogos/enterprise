package web;

import org.jsoup.nodes.Document;

/**
 *
 */
public abstract class AbstractScraper implements Scraper {
    final Document document;

    AbstractScraper(Document document) {
        this(new PreProcessor(), document);
    }

    AbstractScraper(PreProcessor preProcessor, Document document) {
        this.document = preProcessor.clean(document);
    }
}
