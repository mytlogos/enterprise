package scrape.sources.posts.strategies.intface;

import java.util.Collection;

/**
 *
 */
public interface ElementFilter<E extends FilterElement> {
    Collection<E> getFilter();
}
