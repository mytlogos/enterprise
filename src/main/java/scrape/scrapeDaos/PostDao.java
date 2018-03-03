package scrape.scrapeDaos;

import enterprise.data.intface.Creation;
import gorgon.external.*;
import scrape.concurrent.PostCall;
import scrape.sources.Source;
import scrape.sources.posts.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class PostDao extends DataTable<Post> {
    private final Relation<Post, String> title = Relate.build(Ratio.ONE_TO_ONE, "TITLE", Type.TEXT, Post::getTitle, Modifier.NOT_NULL);
    private final Relation<Post, String> published = Relate.build(Ratio.ONE_TO_ONE, "PUBLISHED", Type.TEXT, Post::getTitle, Modifier.NOT_NULL);
    private final Relation<Post, String> post_link = Relate.build(Ratio.ONE_TO_ONE, "POST_LINK", Type.TEXT, Post::getTitle, Modifier.NOT_NULL);
    private final Relation<Post, Boolean> sticky = Relate.build(Ratio.ONE_TO_ONE, "STICKY", Type.BOOLEAN, Post::isSticky, Modifier.NOT_NULL);
    private final Relation<Post, Source> source = Relate.build(Ratio.ONE_TO_ONE, Source.class, Type.ID, Post::getSource, Modifier.NOT_NULL);
    private final Relation<Post, Creation> creation = Relate.build(Ratio.ONE_TO_ONE, Creation.class, Type.ID, Post::getCreation);

    protected PostDao() {
        super("POSTTABLE");
    }

    @Override
    public List<Relation<Post, ?>> getOneToOne() {
        List<Relation<Post, ?>> relations = new ArrayList<>();
        relations.add(title);
        relations.add(published);
        relations.add(source);
        relations.add(creation);
        relations.add(post_link);
        relations.add(sticky);
        return relations;
    }

    @Override
    public List<Relation<Post, ?>> getOneToMany() {
        return Collections.emptyList();
    }

    @Override
    public Post getData(Result<Post> result) throws PersistenceException {
        final String title = result.get(this.title);
        final String link = result.get(post_link);
        final String published = result.get(this.published);
        final Source source = result.get(this.source);
        final Creation creation = result.get(this.creation);
        final Boolean isSticky = result.get(sticky);

        LocalDateTime dateTime = LocalDateTime.parse(published);

        final Post post = new Post(source, title, dateTime, link, creation, isSticky);

        if (creation == null) {
            PostCall.Action.DELETE_ENTRIES.queueEntry(post);
            PostCall.Action.DELETE_ENTRIES.startTimer();
            return null;
        } else {
            return post;
        }
    }
}
