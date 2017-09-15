package scrape.sources.novels.strategies.intface.impl;

import org.jsoup.nodes.Element;
import scrape.sources.novels.strategies.intface.ContentElement;
import scrape.sources.novels.strategies.intface.ElementFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class ContentFilter implements ElementFilter<ContentElement> {
    @Override
    public Collection<ContentElement> getFilter() {
        return new ArrayList<>(Arrays.asList(ContentImpl.values()));
    }

    /**
     *
     */
    private enum ContentImpl implements ContentElement {

        ;

        @Override
        public Element apply(Element element) {
            return null;
        }

    }
}
