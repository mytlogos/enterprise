package scrape.sources;

import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import scrape.sources.novels.strategies.PostsWrapper;
import scrape.sources.novels.strategies.intface.*;

/**
 *
 */
@DataAccess(daoClass = "SourceTable")
public class PostConfigs {
    private boolean init = false;
    private boolean isArchive = false;

    @SQLUpdate(stateGet = "isArchiveUpdated", valueGet = "getArchive", columnField = "archiveSearcher")
    private ObjectProperty<ArchiveSearcher> archive = new SimpleObjectProperty<>();

    @SQLUpdate(stateGet = "isBodyUpdated", valueGet = "getWrapper", columnField = "postWrapper")
    private ObjectProperty<PostsWrapper> wrapper = new SimpleObjectProperty<>();

    @SQLUpdate(stateGet = "isFeedUpdated", valueGet = "getFeed", columnField = "feed")
    private ObjectProperty<Feed> feed = new SimpleObjectProperty<>();

    @SQLUpdate(stateGet = "isPostsUpdated", valueGet = "getPosts", columnField = "postElement")
    private ObjectProperty<PostElement> posts = new SimpleObjectProperty<>();

    @SQLUpdate(stateGet = "isTimeUpdated", valueGet = "getTime", columnField = "timeElement")
    private ObjectProperty<TimeElement> time = new SimpleObjectProperty<>();

    @SQLUpdate(stateGet = "isTitleUpdated", valueGet = "getTitle", columnField = "titleElement")
    private ObjectProperty<TitleElement> title = new SimpleObjectProperty<>();

    @SQLUpdate(stateGet = "isPostBodyUpdated", valueGet = "getPostContent", columnField = "contentElement")
    private ObjectProperty<ContentElement> postBody = new SimpleObjectProperty<>();

    @SQLUpdate(stateGet = "isFooterUpdated", valueGet = "getFooter", columnField = "footerElement")
    private ObjectProperty<FooterElement> footer = new SimpleObjectProperty<>();

    private BooleanProperty archiveUpdated = new SimpleBooleanProperty();
    private BooleanProperty bodyUpdated = new SimpleBooleanProperty();
    private BooleanProperty feedUpdated = new SimpleBooleanProperty();
    private BooleanProperty postsUpdated = new SimpleBooleanProperty();
    private BooleanProperty timeUpdated = new SimpleBooleanProperty();
    private BooleanProperty titleUpdated = new SimpleBooleanProperty();
    private BooleanProperty postBodyUpdated = new SimpleBooleanProperty();
    private BooleanProperty footerUpdated = new SimpleBooleanProperty();

    private BooleanProperty updated = new SimpleBooleanProperty();

    PostConfigs() {
        bindUpdated();
    }

    public boolean isInit() {
        return init && wrapper.get() != null && posts.get() != null && time.get() != null && title.get() != null;
    }

    public boolean isArchive() {
        return isArchive;
    }

    public ArchiveSearcher getArchive() {
        return archive.get();
    }

    public void setArchive(ArchiveSearcher archive) {
        init();
        isArchive = archive != null;
        this.archive.set(archive);
    }

    public PostsWrapper getWrapper() {
        return wrapper.get();
    }

    public void setWrapper(PostsWrapper body) {
        init();
        this.wrapper.set(body);
    }

    public PostElement getPosts() {
        return posts.get();
    }

    public void setPosts(PostElement posts) {
        init();
        this.posts.set(posts);
    }

    public Feed getFeed() {
        return feed.get();
    }

    public void setFeed(Feed feed) {
        this.feed.set(feed);
    }

    public TimeElement getTime() {
        return time.get();
    }

    public void setTime(TimeElement time) {
        init();
        this.time.set(time);
    }

    public TitleElement getTitle() {
        return title.get();
    }

    public void setTitle(TitleElement title) {
        init();
        this.title.set(title);
    }

    public ContentElement getPostContent() {
        return postBody.get();
    }

    public void setPostBody(ContentElement postBody) {
        this.postBody.set(postBody);
    }

    public FooterElement getFooter() {
        return footer.get();
    }

    public void setFooter(FooterElement footer) {
        this.footer.set(footer);
    }

    public boolean isArchiveUpdated() {
        return archiveUpdated.get();
    }

    public boolean isBodyUpdated() {
        return bodyUpdated.get();
    }

    public boolean isFeedUpdated() {
        return feedUpdated.get();
    }

    public boolean isPostsUpdated() {
        return postsUpdated.get();
    }

    public boolean isTimeUpdated() {
        return timeUpdated.get();
    }

    public boolean isTitleUpdated() {
        return titleUpdated.get();
    }

    public boolean isPostBodyUpdated() {
        return postBodyUpdated.get();
    }

    public boolean isFooterUpdated() {
        return footerUpdated.get();
    }

    public boolean isUpdated() {
        return updated.get();
    }

    public BooleanProperty updatedProperty() {
        return updated;
    }

    @Override
    public String toString() {
        return "Archive: " + archive.get() + System.lineSeparator() +
                "Wrapper: " + wrapper.get() + System.lineSeparator() +
                "Post: " + posts.get() + System.lineSeparator() +
                "Title: " + title.get() + System.lineSeparator() +
                "Time: " + time.get() + System.lineSeparator() +
                "Content: " + postBody.get() + System.lineSeparator() +
                "Footer: " + footer.get();
    }

    private void init() {
        init = true;
    }

    private void bindUpdated() {
        feed.addListener(observable -> feedUpdated.set(true));
        archive.addListener(observable -> archiveUpdated.set(true));
        wrapper.addListener(observable -> bodyUpdated.set(true));
        posts.addListener(observable -> postsUpdated.set(true));
        time.addListener(observable -> timeUpdated.set(true));
        title.addListener(observable -> titleUpdated.set(true));
        postBody.addListener(observable -> postBodyUpdated.set(true));
        footer.addListener(observable -> footerUpdated.set(true));

        updated.bind(feedUpdated
                .or(archiveUpdated)
                .or(bodyUpdated)
                .or(postsUpdated)
                .or(timeUpdated)
                .or(titleUpdated)
                .or(postBodyUpdated)
                .or(footerUpdated));
    }

    public void setUpdated() {
        feedUpdated.set(false);
        archiveUpdated.set(false);
        bodyUpdated.set(false);
        postsUpdated.set(false);
        timeUpdated.set(false);
        titleUpdated.set(false);
        postBodyUpdated.set(false);
        footerUpdated.set(false);
    }
}
