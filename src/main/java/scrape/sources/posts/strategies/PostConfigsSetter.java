package scrape.sources.posts.strategies;

import Enterprise.misc.Log;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.ConfigSetter;
import scrape.sources.posts.PostConfigs;
import scrape.sources.posts.strategies.intface.*;
import scrape.sources.posts.strategies.intface.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 */
public class PostConfigsSetter extends ConfigSetter {

    private final Document document;
    private PostConfigs configs;

    public PostConfigsSetter(PostConfigs configs, Document document) {
        this.document = document;
        this.configs = configs;
    }

    @Override
    public boolean setConfigs() {
        ArchiveSearcher searcher = ArchiveGetter.getArchiveSearcher(document);
        configs.setArchive(searcher);

        ContentWrapper wrapper = ContentWrapper.tryAll(document);
        configs.setWrapper(wrapper);

        if (wrapper != null) {
            Element body = wrapper.apply(document);
            PostElement postFilter = getPostFilter(body);

            System.out.println("Configs:\n" + configs);
            if (postFilter != null) {
                configs.setInit();
                return true;
            }
        }
        Log.classLogger(this).log(Level.WARNING, document.baseUri() + " is not supported");
        return false;
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
}
