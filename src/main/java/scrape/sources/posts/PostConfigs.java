package scrape.sources.posts;

import enterprise.data.impl.AbstractDataEntry;
import gorgon.external.DataAccess;
import gorgon.external.GorgonEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import scrape.ScrapeConfigs;
import scrape.scrapeDaos.PostConfigDao;
import scrape.sources.feed.Feed;
import scrape.sources.posts.strategies.ContentWrapper;
import scrape.sources.posts.strategies.intface.*;

/**
 *
 */
@DataAccess(PostConfigDao.class)
public class PostConfigs extends AbstractDataEntry implements ScrapeConfigs, GorgonEntry {
    private final ObjectProperty<ArchiveSearcher> archive = new SimpleObjectProperty<>();
    private final ObjectProperty<Feed> feed = new SimpleObjectProperty<>();
    private final ObjectProperty<ContentWrapper> wrapper = new SimpleObjectProperty<>();
    private final ObjectProperty<PostElement> posts = new SimpleObjectProperty<>();
    private final ObjectProperty<TimeElement> time = new SimpleObjectProperty<>();
    private final ObjectProperty<TitleElement> title = new SimpleObjectProperty<>();
    private final ObjectProperty<ContentElement> postBody = new SimpleObjectProperty<>();
    private final ObjectProperty<FooterElement> footer = new SimpleObjectProperty<>();
    private final BooleanProperty updated = new SimpleBooleanProperty();
    private boolean init = false;
    private boolean isArchive = false;

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

    public void setPostBody(ContentElement postBody) {
        this.postBody.set(postBody);
    }

    public boolean isUpdated() {
        return updated.get();
    }

    @Override
    public int hashCode() {
        int result = getArchive().hashCode();
        result = 31 * result + getFeed().hashCode();
        result = 31 * result + getWrapper().hashCode();
        result = 31 * result + getPosts().hashCode();
        result = 31 * result + getTime().hashCode();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getPostContent().hashCode();
        result = 31 * result + getFooter().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostConfigs configs = (PostConfigs) o;

        if (!getArchive().equals(configs.getArchive())) return false;
        if (!getFeed().equals(configs.getFeed())) return false;
        if (!getWrapper().equals(configs.getWrapper())) return false;
        if (!getPosts().equals(configs.getPosts())) return false;
        if (!getTime().equals(configs.getTime())) return false;
        if (!getTitle().equals(configs.getTitle())) return false;
        if (!getPostContent().equals(configs.getPostContent())) return false;
        return getFooter().equals(configs.getFooter());
    }

    public ArchiveSearcher getArchive() {
        return archive.get();
    }

    public void setArchive(ArchiveSearcher archive) {
        isArchive = archive != null;
        this.archive.set(archive);
    }

    public Feed getFeed() {
        return feed.get();
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

    public FooterElement getFooter() {
        return footer.get();
    }

    public void setFooter(FooterElement footer) {
        this.footer.set(footer);
    }

    public void setFeed(Feed feed) {
        this.feed.set(feed);
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

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof PostConfigs)) return -1;

        PostConfigs o = (PostConfigs) gorgonEntry;

        int compared = String.valueOf(getArchive()).compareTo(String.valueOf(o.getArchive()));

        if (compared == 0) {
            compared = String.valueOf(getWrapper()).compareTo(String.valueOf(o.getWrapper()));
        }
        if (compared == 0) {
            compared = String.valueOf(getPosts()).compareTo(String.valueOf(o.getPosts()));
        }
        if (compared == 0) {
            compared = String.valueOf(getTitle()).compareTo(String.valueOf(o.getTitle()));
        }
        if (compared == 0) {
            compared = String.valueOf(getTime()).compareTo(String.valueOf(o.getTime()));
        }
        if (compared == 0) {
            compared = String.valueOf(getPostContent()).compareTo(String.valueOf(o.getPostContent()));
        }
        if (compared == 0) {
            compared = String.valueOf(getFooter()).compareTo(String.valueOf(o.getFooter()));
        }
        if (compared == 0) {
            compared = String.valueOf(getFeed()).compareTo(String.valueOf(o.getFeed()));
        }
        return compared;
    }
}
