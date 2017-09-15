package scrape.sources.novels.strategies.intface.impl;

import org.jsoup.nodes.Element;
import scrape.sources.novels.strategies.intface.ElementFilter;
import scrape.sources.novels.strategies.intface.FooterElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class FooterFilter implements ElementFilter<FooterElement> {
    @Override
    public Collection<FooterElement> getFilter() {
        return new ArrayList<>(Arrays.asList(Footer.values()));
    }

    /**
     *
     */
    private enum Footer implements FooterElement {
        ;

        @Override
        public Element apply(Element element) {
            return null;
        }

    }
}
