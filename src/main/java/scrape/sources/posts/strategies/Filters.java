package scrape.sources.posts.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public abstract class Filters {

    public static Collection<PostTitle> getTitleFilter() {
        return new ArrayList<>(Arrays.asList(PostTitle.values()));
    }

    public static Collection<PostContent> getContentFilter() {
        return new ArrayList<>(Arrays.asList(PostContent.values()));
    }

    public static Collection<PostFooter> getFooterFilter() {
        return new ArrayList<>(Arrays.asList(PostFooter.values()));
    }

    public static Collection<PostTime> getTimeFilter() {
        List<PostTime> times = new ArrayList<>(Arrays.asList(PostTime.values()));
        times.remove(PostTime.LINK_PAGE);
        return times;
    }

    public static Collection<Posts> getPostsFilter() {
        return new ArrayList<>(Arrays.asList(Posts.values()));
    }

    public static Collection<PostWrapper> getWrapperFilter() {
        return new ArrayList<>(Arrays.asList(PostWrapper.values()));
    }


    public static Collection<ArchiveStrategy> getArchiveFilter() {
        return new ArrayList<>(Arrays.asList(ArchiveStrategy.values()));
    }
}
