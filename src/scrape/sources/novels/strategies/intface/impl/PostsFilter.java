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
        ROW(".row") {
        },
        ARTICLE("article") {
        },
        HENTRY(".hentry") {
        },
        POST(".post") {
        };

        private final String selector;

        Posts(String s) {
            selector = s;
        }


        @Override
        public Elements apply(Element body) {
            Elements postElements = body.select(selector);
            /*for (Element postElement : postElements) {

                //if no title link is available, try to get it from the parent (too specific)
                if (postElement.parent().tagName().equals("a") && !postElement.children().hasAttr("href")) {

                    Element parent = new Element(Tag.valueOf("a"), postElement.baseUri());
                    parent.attr("href", postElement.parent().attr("href"));

                    for (Element element : postElement.getElementsByTag("h3")) {
                        parent.text(element.text());
                        element.replaceWith(parent);
                    }
                }
            }*/
            return postElements;
        }
    }
}
