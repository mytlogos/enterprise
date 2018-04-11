package scrape.scrapeDaos;

import gorgon.external.*;
import scrape.sources.posts.PostConfig;
import scrape.sources.posts.strategies.*;
import scrape.sources.posts.strategies.intface.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class PostConfigDao extends DataTable<PostConfig> {
    private final Relation<PostConfig, String> postElement = Relate.build(Ratio.ONE_TO_ONE, "POSTELEMENT", Type.TEXT, configs -> String.valueOf(configs.getPosts()));
    private final Relation<PostConfig, String> postWrapper = Relate.build(Ratio.ONE_TO_ONE, "POSTWRAPPER", Type.TEXT, configs -> String.valueOf(configs.getWrapper()));
    private final Relation<PostConfig, String> timeElement = Relate.build(Ratio.ONE_TO_ONE, "TIMEELEMENT", Type.TEXT, configs -> String.valueOf(configs.getTime()));
    private final Relation<PostConfig, String> titleElement = Relate.build(Ratio.ONE_TO_ONE, "TITLEELEMENT", Type.TEXT, configs -> String.valueOf(configs.getTitle()));
    private final Relation<PostConfig, String> contentElement = Relate.build(Ratio.ONE_TO_ONE, "CONTENTELEMENT", Type.TEXT, configs -> String.valueOf(configs.getPostBody()));
    private final Relation<PostConfig, String> footerElement = Relate.build(Ratio.ONE_TO_ONE, "FOOTERELEMENT", Type.TEXT, configs -> String.valueOf(configs.getFooter()));
    private final Relation<PostConfig, String> feed = Relate.build(Ratio.ONE_TO_ONE, "FEED", Type.TEXT, configs -> String.valueOf(configs.getFeed()));
    private final Relation<PostConfig, String> archiveSearcher = Relate.build(Ratio.ONE_TO_ONE, "ARCHIVESEARCHER", Type.TEXT, configs -> String.valueOf(configs.getArchive()));


    protected PostConfigDao() {
        super("POST_CONFIG_TABLE");
    }

    @Override
    public List<Relation<PostConfig, ?>> getOneToOne() {
        List<Relation<PostConfig, ?>> relations = new ArrayList<>();
        relations.add(postElement);
        relations.add(postWrapper);
        relations.add(timeElement);
        relations.add(titleElement);
        relations.add(contentElement);
        relations.add(footerElement);
        relations.add(feed);
        relations.add(archiveSearcher);
        return relations;
    }

    @Override
    public List<Relation<PostConfig, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public PostConfig getData(Result<PostConfig> result) throws PersistenceException {
        final String postElement = result.get(this.postElement);
        final String wrapper = result.get(this.postWrapper);
        final String time = result.get(this.timeElement);
        final String title = result.get(this.titleElement);
        final String content = result.get(this.contentElement);
        final String footer = result.get(this.footerElement);
        final String feed = result.get(this.feed);
        final String archive = result.get(this.archiveSearcher);

        PostConfig configs = new PostConfig();

        //todo better by enum or collection?

        /*configs.setArchive(getMatch(Filters.getArchiveFilter(), archive));
        configs.setFeed(getMatch(Filters.getFeedFilter(), feed));
        configs.setWrapper(getMatch(Filters.getWrapperFilter(), wrapper));

        configs.setPosts(getMatch(Filters.getPostsFilter(), postElement));
        configs.setTitle(getMatch(Filters.getTitleFilter(), title));
        configs.setTime(getMatch(Filters.getTimeFilter(), time));

        configs.setPostBody(getMatch(Filters.getContentFilter(), content));
        configs.setFooter(getMatch(Filters.getFooterFilter(), footer));*/

        configs.setArchive(getMatch(ArchiveStrategy.class, archive));
        configs.setFeed(feed);
        configs.setWrapper(getMatch(PostWrapper.class, wrapper));

        configs.setPosts(getMatch(Posts.class, postElement));
        configs.setTitle(getMatch(PostTitle.class, title));
        configs.setTime(getMatch(PostTime.class, time));

        configs.setPostBody(getMatch(PostContent.class, content));
        configs.setFooter(getMatch(PostFooter.class, footer));
        return configs;
    }

    private <E extends Enum<E> & Filter> E getMatch(Class<E> enumClass, String toMatch) {
        try {
            return Enum.valueOf(enumClass, toMatch);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private <E extends Filter> E getMatch(Collection<E> collection, String match) {
        if (match != null) {
            for (E e : collection) {
                if (e.toString().matches(match)) {
                    return e;
                }
            }
        }
        return null;
    }
}
