package scrape.sources.novels.strategies.intface.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.novels.strategies.intface.ElementFilter;
import scrape.sources.novels.strategies.intface.PostElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class PostsFilter implements ElementFilter {

    @Override
    public List<PostElement> getFilter() {
        return new ArrayList<>(Arrays.asList(Posts.values()));
    }

    /**
     *
     */
    public enum Posts implements PostElement {
        LIST("ul.new-list > li > a") {
        },
        TABLE("table[class*=update] > tbody > tr") {
        },
        SHADOW("[class=shadow]") {
        },
        ROW(".chapter, .announcements") {
        },
        ARTICLE("article") {
        },
        HENTRY(".hentry") {
        },
        POST(".post") {
        },
        POST_EX_ASIDE(".post:not(.format-aside)") {
        };

        private final String selector;

        Posts(String s) {
            selector = s;
        }


        @Override
        public Elements apply(Element body) {
            return body.select(selector);
        }
    }
}
