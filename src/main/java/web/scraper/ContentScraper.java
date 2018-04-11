package web.scraper;

import enterprise.data.intface.Creation;
import org.jsoup.nodes.Element;
import scrape.sources.Content;
import scrape.sources.Source;

import java.io.IOException;

/**
 *
 */
public abstract class ContentScraper extends StandardScraper<Content> {
    private final Creation creation;

    public ContentScraper(Element root, Source source, boolean clean, Creation creation) {
        super(root, source, clean);
        this.creation = creation;
    }

    public ContentScraper(Source source, Creation creation) throws IOException {
        super(source);
        this.creation = creation;
    }
}
