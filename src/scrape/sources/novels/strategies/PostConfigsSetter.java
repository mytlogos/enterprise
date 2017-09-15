package scrape.sources.novels.strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.PostConfigs;
import scrape.sources.novels.strategies.intface.*;
import scrape.sources.novels.strategies.intface.impl.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class PostConfigsSetter {

    private PostConfigs configs;

    private PostConfigsSetter(PostConfigs configs) {
        this.configs = configs;
    }

    public static boolean setPostConfigs(PostConfigs configs, Document document) {
        PostConfigsSetter setter = new PostConfigsSetter(configs);

        PostsWrapper wrapper = PostsWrapper.tryAll(document);
        Element body = wrapper.apply(document);

        PostElement postFilter = setter.getPostFilter(body);

        return postFilter != null;
    }

    private boolean validPosts(Elements postElements) {
        if (postElements == null || postElements.isEmpty()) {
            return false;
        }

        TitleElement titleElement = tryAll(postElements, new TitlesFilter().getFilter());
        TimeElement timeElement = tryAll(postElements, new TimeFilter().getFilter());

        System.out.println("TIME: " + timeElement);
        System.out.println("TITLE:" + titleElement);
        if (timeElement != null && titleElement != null) {
            return false;
        } else {
            return false;
        }
    }


    private PostElement getPostFilter(Element element) {
        if (element != null) {
            List<PostElement> filters = new PostsFilter().getFilter();

            for (PostElement filter : filters) {
                Elements applied = filter.apply(element);

                System.out.println("FILTER: " + filter);
                if (validPosts(applied)) {
                    setOptionalConfigs(applied);
                    return filter;
                }
            }
        }
        return null;
    }

    private void setOptionalConfigs(Elements applied) {
        ContentElement contentElement = tryAll(applied, new ContentFilter().getFilter());
        FooterElement footerElement = tryAll(applied, new FooterFilter().getFilter());
    }

    private <E extends FilterElement> E tryAll(Elements elements, Collection<E> filters) {
        if (!elements.isEmpty() && filters != null && !filters.isEmpty()) {
            Element first = elements.get(0);
            return tryFilter(elements, first, filters);
        } else {
            return null;
        }
    }

    private <E extends FilterElement> E tryFilter(Elements elements, Element first, Collection<E> filters) {
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

    private <E extends FilterElement> E getTitleFilter(Element element, Collection<E> filters) {
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
