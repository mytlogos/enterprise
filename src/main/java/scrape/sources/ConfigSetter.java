package scrape.sources;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.posts.strategies.intface.FilterElement;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 */
public abstract class ConfigSetter {
    public abstract boolean setConfigs();

    protected <E extends FilterElement> E tryAll(Elements elements, Collection<E> filters) {
        if (!elements.isEmpty() && filters != null && !filters.isEmpty()) {
            Element first = elements.get(0);
            return tryFilter(elements, first, filters);
        } else {
            return null;
        }
    }

    protected <E extends FilterElement> E getFirstFilter(Element element, Collection<E> filters) {
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

    private <E extends FilterElement> E tryFilter(Elements elements, Element first, Collection<E> filters) {
        E filter = getFirstFilter(first, filters);

        if (filter != null) {
            for (Element element : elements) {
                if (applyFilter(filters, filter, element)) {
                    return tryFilter(elements, first, filters);
                }
            }
        }
        return filter;
    }

    private <E extends FilterElement> boolean applyFilter(Collection<E> filters, E filter, Element element) {
        Element apply = filter.apply(element);

        if (apply == null) {
            filters.remove(filter);
            return true;
        }
        return false;
    }
}
