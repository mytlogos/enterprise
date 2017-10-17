package scrape.sources.posts.strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.posts.strategies.intface.Filter;
import scrape.sources.toc.strategies.intface.DocumentElement;

/**
 *
 */
public enum ContentWrapper implements DocumentElement, Filter {
    CONTENT("#content, div.content") {
    },
    MAIN(".main, #main") {
    },
    CONTAINER(".container, #container"),
    MAIN_CONTENT("[class^=main-content], [id^=main-content]"),
    TABLE("table[class*=update], table[id*=update]"),
    RELEASES(".releases, #releases"),
    COLUMN_GRID(".column.grid_7"),
    CONTENT_WRAP_ID("#content-wrap"),
    CONTENT_WRAP_CLASS(".content-wrap"),
    X_CONTAINER(".site > .x-container.max.width.offset"),
    CONTAINER_FLUID("h2:contains(release) + .container-fluid"),;

    private final String selector;

    ContentWrapper(String s) {
        selector = s;
    }

    public static ContentWrapper tryAll(Document document) {
        Element child = null;
        ContentWrapper result = null;
        for (ContentWrapper postsWrapper : ContentWrapper.values()) {
            Elements wrapper = document.select(postsWrapper.selector);

            if (!wrapper.isEmpty() && wrapper.size() == 1) {
                Element element = wrapper.get(0);

                if (child == null) {
                    result = postsWrapper;
                    child = element;

                } else if (isChildOf(child, element)) {
                    result = postsWrapper;
                    child = element;
                }
            }
        }

        return result;
    }

    private static boolean isChildOf(Element parent, Element child) {
        return parent.getAllElements().contains(child);
    }

    @Override
    public Element apply(Document document) {
        Elements elements = document.select(selector);
        for (Element element : elements) {
            element.select("[id~=.*secondary.*], [class~=.*secondary.*]").remove();
            element.select("[id=widget], [class=widget]").remove();
        }

        //post wrapper should be unique in the document
        if (elements.size() != 1) {
            return null;
        } else {
            return elements.get(0);
        }
    }
}
