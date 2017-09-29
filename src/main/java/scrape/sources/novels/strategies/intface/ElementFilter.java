package scrape.sources.novels.strategies.intface;

import org.jsoup.nodes.Element;

import java.util.Collection;
import java.util.function.Function;

/**
 *
 */
public interface ElementFilter<E extends Function<Element, Element>> {
    Collection<E> getFilter();
}
