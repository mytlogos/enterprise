package scrape.sources.novels;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 */
public enum BasicNovelPosts implements ElementsGetter {
    ;

    @Override
    public Elements get(Document document) {
        return new Elements();
    }

    public ElementsGetter bodyStrategy() {
        return document -> null;
    }

    public ElementsGetter postsStrategy() {
        return document -> null;
    }
}
