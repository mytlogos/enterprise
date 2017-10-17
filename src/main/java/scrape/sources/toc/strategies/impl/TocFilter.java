package scrape.sources.toc.strategies.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.posts.strategies.intface.ElementFilter;
import scrape.sources.toc.strategies.intface.TocElement;

import java.util.Collection;

/**
 *
 */
public class TocFilter implements ElementFilter<TocElement> {
    @Override
    public Collection<TocElement> getFilter() {
        return null;
    }

    public enum Tocs implements TocElement {
        SIMPLE_CONTAINS("a:contains(?)", TYPE.SIMPLE_STEP),
        DOUBLE_CONTAINS_CATALOGUE("a:contains(?):contains(catalogue)", TYPE.SIMPLE_STEP),
        DOUBLE_CONTAINS_INDEX("a:contains(?):contains(index)", TYPE.SIMPLE_STEP),
        DOUBLE_CONTAINS_TOC("a:contains(?):contains(toc)", TYPE.SIMPLE_STEP),
        MATCHES_OWN("a:matchesOwn(Table of Content(s)?)", TYPE.SIMPLE_STEP)
        ;

        private final String selector;
        private final TYPE type;
        private String keyWord = "";

        Tocs(String selector, TYPE type) {
            this.selector = selector;
            this.type = type;
        }



        @Override
        public Element apply(Element element) {
            return this.type.get(this, element);
        }

        @Override
        public void setKeyWord(String keyWord) {
            this.keyWord = keyWord;
        }
    }

    private enum TYPE {
        SIMPLE_STEP {
            @Override
            Element get(Tocs tocs, Element element) {
                String selector = tocs.selector.replace("?", tocs.keyWord);
                final Elements select = element.select(selector);

                if (select.size() == 1) {
                    return select.get(0);
                }
                return null;
            }
        },;

        abstract Element get(Tocs tocs, Element element);

    }


}
