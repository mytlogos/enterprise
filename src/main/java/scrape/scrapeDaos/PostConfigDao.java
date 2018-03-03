package scrape.scrapeDaos;

import gorgon.external.*;
import scrape.sources.posts.FeedGetter;
import scrape.sources.posts.PostConfigs;
import scrape.sources.posts.strategies.ArchiveGetter;
import scrape.sources.posts.strategies.ContentWrapper;
import scrape.sources.posts.strategies.intface.impl.*;

import java.util.*;

/**
 *
 */
public class PostConfigDao extends DataTable<PostConfigs> {
    private final Relation<PostConfigs, String> postElement = Relate.build(Ratio.ONE_TO_ONE, "POSTELEMENT", Type.TEXT, configs -> String.valueOf(configs.getPosts()));
    private final Relation<PostConfigs, String> postWrapper = Relate.build(Ratio.ONE_TO_ONE, "POSTWRAPPER", Type.TEXT, configs -> String.valueOf(configs.getWrapper()));
    private final Relation<PostConfigs, String> timeElement = Relate.build(Ratio.ONE_TO_ONE, "TIMEELEMENT", Type.TEXT, configs -> String.valueOf(configs.getTime()));
    private final Relation<PostConfigs, String> titleElement = Relate.build(Ratio.ONE_TO_ONE, "TITLEELEMENT", Type.TEXT, configs -> String.valueOf(configs.getTitle()));
    private final Relation<PostConfigs, String> contentElement = Relate.build(Ratio.ONE_TO_ONE, "CONTENTELEMENT", Type.TEXT, configs -> String.valueOf(configs.getPostContent()));
    private final Relation<PostConfigs, String> footerElement = Relate.build(Ratio.ONE_TO_ONE, "FOOTERELEMENT", Type.TEXT, configs -> String.valueOf(configs.getFooter()));
    private final Relation<PostConfigs, String> feed = Relate.build(Ratio.ONE_TO_ONE, "FEED", Type.TEXT, configs -> String.valueOf(configs.getFeed()));
    private final Relation<PostConfigs, String> archiveSearcher = Relate.build(Ratio.ONE_TO_ONE, "ARCHIVESEARCHER", Type.TEXT, configs -> String.valueOf(configs.getArchive()));


    protected PostConfigDao() {
        super("POST_CONFIG_TABLE");
    }

    @Override
    public List<Relation<PostConfigs, ?>> getOneToOne() {
        List<Relation<PostConfigs, ?>> relations = new ArrayList<>();
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
    public List<Relation<PostConfigs, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public PostConfigs getData(Result<PostConfigs> result) throws PersistenceException {
        final String postElement = result.get(this.postElement);
        final String wrapper = result.get(this.postWrapper);
        final String time = result.get(this.timeElement);
        final String title = result.get(this.titleElement);
        final String content = result.get(this.contentElement);
        final String footer = result.get(this.footerElement);
        final String feed = result.get(this.feed);
        final String archive = result.get(this.archiveSearcher);

        PostConfigs configs = new PostConfigs();

        configs.setArchive(getMatch(ArchiveGetter.getFilter(), archive));
        configs.setFeed(getMatch(FeedGetter.getFilter(), feed));
        configs.setWrapper(getMatch(Arrays.asList(ContentWrapper.values()), wrapper));

        configs.setPosts(getMatch(new PostsFilter().getFilter(), postElement));
        configs.setTitle(getMatch(new TitlesFilter().getFilter(), title));
        configs.setTime(getMatch(new TimeFilter().getFilter(), time));

        configs.setPostBody(getMatch(new ContentFilter().getFilter(), content));
        configs.setFooter(getMatch(new FooterFilter().getFilter(), footer));
        return configs;
    }

    private <E> E getMatch(Collection<E> collection, String match) {
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
