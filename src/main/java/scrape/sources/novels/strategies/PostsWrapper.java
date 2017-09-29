package scrape.sources.novels.strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.novels.strategies.intface.Filter;

import java.util.function.Function;

/**
 *
 */
public enum PostsWrapper implements Function<Document, Element>, Filter {
    CONTENT("div#content, div.content") {
    },
    MAIN(".main, #main") {
    },
    CONTAINER(".container, #container"),
    MAIN_CONTENT("[class^=main-content], [id^=main-content]"),
    TABLE("table[class*=update], table[id*=update]"),
    RELEASES(".releases, #releases"),
    COLUMN_GRID(".column.grid_7"),
    CONTENT_WRAP("#content-wrap, .content-wrap"),
    X_CONTAINER(".site > .x-container.max.width.offset");

    private final String selector;

    PostsWrapper(String s) {
        selector = s;
    }

    public static PostsWrapper tryAll(Document document) {
        Element child = null;
        PostsWrapper result = null;
        for (PostsWrapper postsWrapper : PostsWrapper.values()) {
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
