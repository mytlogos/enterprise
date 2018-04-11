package web.scraper;

import org.jdom2.JDOMException;
import org.jsoup.nodes.Element;
import scrape.pages.FeedManager;
import scrape.sources.Source;
import scrape.sources.posts.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public abstract class NewsScraper extends StandardScraper<Collection<Post>> {
    public NewsScraper(Element root, Source source, boolean clean) {
        super(root, source, clean);
    }

    public NewsScraper(Source source) throws IOException {
        super(source);
    }


    private String getFeed() throws JDOMException, IOException {
        return new FeedManager(source).checkFeed(root);
    }

    private List<String> getArchives() {
        return new ArrayList<>();
    }
}
