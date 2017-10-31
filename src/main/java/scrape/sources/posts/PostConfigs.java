package scrape.sources.posts;

import Enterprise.data.impl.AbstractDataEntry;
import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import scrape.ScrapeConfigs;
import scrape.sources.feed.Feed;
import scrape.sources.posts.strategies.ContentWrapper;
import scrape.sources.posts.strategies.intface.*;

/**
 *
 */
@DataAccess(daoClass = "SourceTable")
public class PostConfigs extends AbstractDataEntry implements ScrapeConfigs {
    private boolean init = false;
    private boolean isArchive = false;

    @SQLUpdate(columnField = "archiveSearcher")
    private ObjectProperty<ArchiveSearcher> archive = new SimpleObjectProperty<>();

    @SQLUpdate(columnField = "postWrapper")
    private ObjectProperty<ContentWrapper> wrapper = new SimpleObjectProperty<>();

    @SQLUpdate(columnField = "feed")
    private ObjectProperty<Feed> feed = new SimpleObjectProperty<>();

    @SQLUpdate(columnField = "postElement")
    private ObjectProperty<PostElement> posts = new SimpleObjectProperty<>();

    @SQLUpdate(columnField = "timeElement")
    private ObjectProperty<TimeElement> time = new SimpleObjectProperty<>();

    @SQLUpdate(columnField = "titleElement")
    private ObjectProperty<TitleElement> title = new SimpleObjectProperty<>();

    @SQLUpdate(columnField = "contentElement")
    private ObjectProperty<ContentElement> postBody = new SimpleObjectProperty<>();

    @SQLUpdate(columnField = "footerElement")
    private ObjectProperty<FooterElement> footer = new SimpleObjectProperty<>();

    private BooleanProperty updated = new SimpleBooleanProperty();

    public boolean isInit() {
        return init;
    }

    @Override
    public void setInit() {
        init = true;
    }

    public boolean isArchive() {
        return isArchive;
    }

    public ArchiveSearcher getArchive() {
        return archive.get();
    }

    public void setArchive(ArchiveSearcher archive) {
        isArchive = archive != null;
        this.archive.set(archive);
    }

    public ContentWrapper getWrapper() {
        return wrapper.get();
    }

    public void setWrapper(ContentWrapper body) {
        this.wrapper.set(body);
    }

    public PostElement getPosts() {
        return posts.get();
    }

    public void setPosts(PostElement posts) {
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
        this.time.set(time);
    }

    public TitleElement getTitle() {
        return title.get();
    }

    public void setTitle(TitleElement title) {
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

    public boolean isUpdated() {
        return updated.get();
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

}
