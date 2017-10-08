package scrape.sources.toc.strategies.impl;

import org.jsoup.nodes.Element;
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
        ;

        @Override
        public Element apply(Element element) {
            return null;
        }
    }

}
