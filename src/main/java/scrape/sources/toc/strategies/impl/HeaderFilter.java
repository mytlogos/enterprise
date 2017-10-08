package scrape.sources.toc.strategies.impl;

import org.jsoup.nodes.Element;
import scrape.sources.posts.strategies.intface.ElementFilter;
import scrape.sources.toc.strategies.intface.HeaderElement;

import java.util.Collection;

/**
 *
 */
public class HeaderFilter implements ElementFilter<HeaderElement> {

    @Override
    public Collection<HeaderElement> getFilter() {
        return null;
    }

    public enum Headers implements HeaderElement {
        ;

        @Override
        public Element apply(Element element) {
            return null;
        }
    }

}
