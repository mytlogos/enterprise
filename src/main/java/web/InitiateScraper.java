package web;

import org.jsoup.nodes.Element;
import scrape.sources.Source;
import web.scorer.Scorer;
import web.scraper.StandardScraper;

import java.io.IOException;

/**
 *
 */
public class InitiateScraper extends StandardScraper<Void> {

    public InitiateScraper(Element root, Source source, boolean clean) {
        super(root, source, clean);
    }

    public InitiateScraper(Source source) throws IOException {
        super(source);
    }

    @Override
    public Scorer getScorer() {
        return null;
    }
}
