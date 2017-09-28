package scrape.sources.novels.strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.PostConfigs;
import scrape.sources.novels.strategies.intface.*;
import scrape.sources.novels.strategies.intface.impl.*;

import java.util.ArrayList;
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
        configs.setWrapper(wrapper);

        Element body = wrapper.apply(document);

        PostElement postFilter = setter.getPostFilter(body);

        System.out.println("Configs:\n" + configs);
        return postFilter != null;
    }

    private boolean validPosts(Elements postElements) {
        TitleElement titleElement = tryAll(postElements, new TitlesFilter().getFilter());
        TimeElement timeElement = tryAll(postElements, new TimeFilter().getFilter());

        if (timeElement == null && titleElement != null) {
            TimeFilter.LINK_PAGE linkPage = new TimeFilter.LINK_PAGE(titleElement);

            List<TimeElement> list = new ArrayList<>();
            list.add(linkPage);

            timeElement = tryAll(postElements, list);
        }

        if (timeElement != null && titleElement != null) {

            configs.setTime(timeElement);
            configs.setTitle(titleElement);

            return true;
        } else {
            return false;
        }
    }


    private PostElement getPostFilter(Element element) {
        if (element != null) {
            List<PostElement> filters = new PostsFilter().getFilter();

            for (PostElement filter : filters) {
                Elements applied = filter.apply(element);

                if (applied != null && !applied.isEmpty() && validPosts(applied)) {
                    setOptionalConfigs(applied);

                    configs.setPosts(filter);
                    return filter;
                }
            }
        }
        return null;
    }

    private void setOptionalConfigs(Elements applied) {
        ContentElement contentElement = tryAll(applied, new ContentFilter().getFilter());
        FooterElement footerElement = tryAll(applied, new FooterFilter().getFilter());

        configs.setFooter(footerElement);
        configs.setPostBody(contentElement);
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
