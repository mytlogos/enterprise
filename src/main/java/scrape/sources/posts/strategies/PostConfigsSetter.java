package scrape.sources.posts.strategies;

import enterprise.data.Default;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.ConfigSetter;
import scrape.sources.posts.PostConfig;
import scrape.sources.posts.strategies.intface.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 *
 */
public class PostConfigsSetter extends ConfigSetter {

    private final Document document;
    private final PostConfig configs;

    public PostConfigsSetter(PostConfig configs, Document document) {
        this.document = document;
        this.configs = configs;
    }

    @Override
    public void setConfigs() {
        ArchiveSearcher searcher = ArchiveGetter.getArchiveSearcher(document);
        configs.setArchive(searcher);

        PostWrapper wrapper = PostWrapper.tryAll(document);
        configs.setWrapper(wrapper);

        if (wrapper != null) {
            Element body = wrapper.apply(document);
            PostElement postFilter = getPostFilter(body);

            System.out.println("Configs:\n" + configs);

            if (postFilter != null) {
                configs.setInit();
                return;
            }
        }
        Default.LOGGER.log(Level.WARNING, document.baseUri() + " is not supported");
    }

    private PostElement getPostFilter(Element element) {
        if (element != null) {
            Collection<Posts> filters = Filters.getPostsFilter();

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

    private boolean validPosts(Elements postElements) {
        TitleElement titleElement = tryAll(postElements, Filters.getTitleFilter());
        TimeElement timeElement = tryAll(postElements, Filters.getTimeFilter());

        if (timeElement == null && titleElement != null) {

            List<TimeElement> list = new ArrayList<>();
            PostTime linkPage = PostTime.LINK_PAGE;
            linkPage.setTitleElement(titleElement);
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

    private void setOptionalConfigs(Elements applied) {
        ContentElement contentElement = tryAll(applied, Filters.getContentFilter());
        FooterElement footerElement = tryAll(applied, Filters.getFooterFilter());

        configs.setFooter(footerElement);
        configs.setPostBody(contentElement);
    }
}
