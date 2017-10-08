package scrape.sources.posts.strategies;

import org.jsoup.nodes.Document;
import scrape.sources.posts.strategies.intface.ElementFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

/**
 *
 */
public enum Pages implements Function<Document, Document> {
    ;

    @Override
    public Document apply(Document document) {
        return null;
    }

    public Collection<ElementFilter> getFilter() {
        return new ArrayList<>();
    }
}
