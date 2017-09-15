package scrape.sources.novels.strategies;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.novels.strategies.intface.ElementFilter;
import scrape.sources.novels.strategies.intface.FilterElement;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 */
public class Strategies<E extends FilterElement, R extends ElementFilter<E>> {

    public E tryAll(Elements elements, R filterContainer) {

        if (!elements.isEmpty() && filterContainer != null) {
            Collection<E> filters = filterContainer.getFilter();

            if (filters != null && !filters.isEmpty()) {
                Element first = elements.get(0);
                return tryFilter(elements, first, filters);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private E tryFilter(Elements elements, Element first, Collection<E> filters) {
        E filter = getTitleFilter(first, filters);

        if (filter != null) {
            for (Element element : elements) {
                Element apply = filter.apply(element);

                if (apply == null) {
                    filters.remove(filter);
                    return tryFilter(elements, first, filters);
                }
            }
        }
        return filter;
    }

    private E getTitleFilter(Element element, Collection<E> filters) {
        Iterator<E> iterator = filters.iterator();
        while (iterator.hasNext()) {
            E filter = iterator.next();
            Element apply = filter.apply(element);

            if (apply != null) {
                return filter;
            } else {
                iterator.remove();
            }
        }
        return null;
    }
}
