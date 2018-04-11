package scrape.sources.posts;

import gorgon.external.DataAccess;
import gorgon.external.GorgonEntry;
import scrape.Config;
import scrape.scrapeDaos.PostConfigDao;
import scrape.sources.posts.strategies.PostWrapper;
import scrape.sources.posts.strategies.intface.*;

/**
 *
 */
@DataAccess(PostConfigDao.class)
public class PostConfig extends Config {
    private ArchiveSearcher archive = null;
    private String feed = null;
    private PostWrapper wrapper = null;
    private PostElement posts = null;
    private TimeElement time = null;
    private TitleElement title = null;
    private ContentElement postBody = null;
    private FooterElement footer = null;

    @Override
    public int hashCode() {
        int result = getArchive() != null ? getArchive().hashCode() : 0;
        result = 31 * result + (getFeed() != null ? getFeed().hashCode() : 0);
        result = 31 * result + (getWrapper() != null ? getWrapper().hashCode() : 0);
        result = 31 * result + (getPosts() != null ? getPosts().hashCode() : 0);
        result = 31 * result + (getTime() != null ? getTime().hashCode() : 0);
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getPostBody() != null ? getPostBody().hashCode() : 0);
        result = 31 * result + (getFooter() != null ? getFooter().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostConfig that = (PostConfig) o;

        if (getArchive() != null ? !getArchive().equals(that.getArchive()) : that.getArchive() != null) return false;
        if (getFeed() != null ? !getFeed().equals(that.getFeed()) : that.getFeed() != null) return false;
        if (getWrapper() != that.getWrapper()) return false;
        if (getPosts() != null ? !getPosts().equals(that.getPosts()) : that.getPosts() != null) return false;
        if (getTime() != null ? !getTime().equals(that.getTime()) : that.getTime() != null) return false;
        if (getTitle() != null ? !getTitle().equals(that.getTitle()) : that.getTitle() != null) return false;
        if (getPostBody() != null ? !getPostBody().equals(that.getPostBody()) : that.getPostBody() != null)
            return false;
        return getFooter() != null ? getFooter().equals(that.getFooter()) : that.getFooter() == null;
    }

    @Override
    public String toString() {
        String separator = System.lineSeparator();
        return "Archive: " + archive + separator +
                "Wrapper: " + wrapper + separator +
                "Post: " + posts + separator +
                "Title: " + title + separator +
                "PostTime: " + time + separator +
                "Content: " + postBody + separator +
                "PostFooter: " + footer;
    }

    public ArchiveSearcher getArchive() {
        return archive;
    }

    public void setArchive(ArchiveSearcher archive) {
        this.archive = archive;
    }

    public String getFeed() {
        return feed;
    }

    public PostWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(PostWrapper body) {
        this.wrapper = body;
    }

    public PostElement getPosts() {
        return posts;
    }

    public void setPosts(PostElement posts) {
        this.posts = posts;
    }

    public TimeElement getTime() {
        return time;
    }

    public void setTime(TimeElement time) {
        this.time = time;
    }

    public TitleElement getTitle() {
        return title;
    }

    public void setTitle(TitleElement title) {
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

    public void setFeed(String feed) {
        this.feed = feed;
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof PostConfig)) return -1;

        PostConfig o = (PostConfig) gorgonEntry;

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
            compared = String.valueOf(getPostBody()).compareTo(String.valueOf(o.getPostBody()));
        }
        if (compared == 0) {
            compared = String.valueOf(getFooter()).compareTo(String.valueOf(o.getFooter()));
        }
        if (compared == 0) {
            compared = String.valueOf(getFeed()).compareTo(String.valueOf(o.getFeed()));
        }
        return compared;
    }

    @Override
    public String getKey() {
        return "novel-post";
    }
}
