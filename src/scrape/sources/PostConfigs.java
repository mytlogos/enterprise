package scrape.sources;

import scrape.sources.novels.Feed;
import scrape.sources.novels.strategies.PostsWrapper;
import scrape.sources.novels.strategies.intface.*;

/**
 *
 */
public class PostConfigs {
    private boolean init = false;
    private boolean isArchive = false;

    private ArchiveSearcher archive = null;
    private PostsWrapper body = null;
    private Feed feed = null;

    private PostElement posts = null;
    private TimeElement time = null;
    private TitleElement title = null;
    private ContentElement postBody = null;
    private FooterElement footer = null;

    public boolean isInit() {
        return init && body != null && posts != null && time != null && title != null;
    }

    private void init() {
        init = true;
    }

    public boolean isArchive() {
        return isArchive;
    }

    public ArchiveSearcher getArchive() {
        return archive;
    }

    public void setArchive(ArchiveSearcher archive) {
        init();
        isArchive = archive != null;
        this.archive = archive;
    }

    public PostsWrapper getWrapper() {
        return body;
    }

    public void setWrapper(PostsWrapper body) {
        init();
        this.body = body;
    }

    public PostElement getPosts() {
        return posts;
    }

    public void setPosts(PostElement posts) {
        init();
        this.posts = posts;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public TimeElement getTime() {
        return time;
    }

    public void setTime(TimeElement time) {
        init();
        this.time = time;
    }

    public TitleElement getTitle() {
        return title;
    }

    public void setTitle(TitleElement title) {
        init();
        this.title = title;
    }

    public ContentElement getPostBody() {
        return postBody;
    }

    public void setPostBody(ContentElement postBody) {
        this.postBody = postBody;
    }

    public FooterElement getFooter() {
        return footer;
    }

    public void setFooter(FooterElement footer) {
        this.footer = footer;
    }

    @Override
    public String toString() {
        return "Archive: " + archive + System.lineSeparator() +
                "Wrapper: " + body + System.lineSeparator() +
                "Post: " + posts + System.lineSeparator() +
                "Title: " + title + System.lineSeparator() +
                "Time: " + time + System.lineSeparator() +
                "Content: " + postBody + System.lineSeparator() +
                "Footer: " + footer;
    }
}
