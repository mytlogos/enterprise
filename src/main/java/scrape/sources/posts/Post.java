package scrape.sources.posts;

import enterprise.data.Default;
import enterprise.data.impl.AbstractDataEntry;
import enterprise.data.intface.Creation;
import enterprise.data.intface.DataEntry;
import gorgon.external.DataAccess;
import gorgon.external.GorgonEntry;
import scrape.scrapeDaos.PostDao;
import scrape.sources.Source;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class represents a Post of a blog.
 * It is immutable.
 */
@DataAccess(PostDao.class)
public final class Post extends AbstractDataEntry implements DataEntry, GorgonEntry {
    private boolean sticky;
    private Source source;
    private String followLink;
    private String title;
    private LocalDateTime timeStamp;
    private Creation creation;
    private String content;
    private String footer = Default.STRING;

    Post() {

    }

    public Post(Source source, String title, LocalDateTime timeStamp, String followLink, boolean isSticky) {
        this(source, title, timeStamp, followLink, null, isSticky);
        validateState();
    }

    public Post(Source source, String title, String content, String footer, LocalDateTime timeStamp, String followLink, boolean isSticky) {
        this(source, title, timeStamp, followLink, null, isSticky);
        this.content = content;
        this.footer = footer;
        validateState();
    }

    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * The constructor of {@code Post}.
     */
    public Post(Source source, String title, LocalDateTime timeStamp, String followLink, Creation creation, boolean isSticky) {
        this.title = title;
        this.timeStamp = timeStamp;
        this.followLink = followLink;
        this.creation = creation;
        this.sticky = isSticky;
        if (source != null) {
            this.source = source;
        } else {
            this.source = Default.SOURCE;
        }
    }

    public boolean isAfter(Post post) {
        return !isBefore(post);
    }

    public boolean isBefore(Post post) {
        return getTimeStamp().isBefore(post.getTimeStamp());
    }

    private void validateState() {
        String message = "";
        if (source == null) {
            message = message + "source is null, ";
        }
        if (timeStamp == null) {
            message = message + "timeStamp is null, ";
        }
        if (title == null) {
            message = message + "title is null, ";
        }
        if (followLink == null) {
            message = message + "followLink is null";
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public Creation getCreation() {
        return creation;
    }

    /**
     * Gets the String representation of the link of the Post.
     *
     * @return link to the Post
     */
    public String getFollowLink() {
        return followLink;
    }

    /**
     * Returns a String representation of
     * the timestamp without timezone.
     *
     * @return string
     */
    public String getTime() {
        return timeStamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    /**
     * Gets the source of this Post.
     *
     * @return source
     */
    public Source getSource() {
        return source;
    }

    /**
     * Gets the content of the {@code Post}.
     *
     * @return the content in a {@code List}, every element represents one paragraph.
     * Is empty if no value was set
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets the footer of this {@code Post}.
     *
     * @return footer - returns {@link Default#STRING} if no value was set
     */
    public String getFooter() {
        return footer;
    }

    /**
     * @return
     */
    public boolean isSticky() {
        return sticky;
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof Post)) return -1;

        Post o = (Post) gorgonEntry;

        int compare = o.getTimeStamp().compareTo(getTimeStamp());

        if (compare == 0) {
            compare = getTitle().compareTo(o.getTitle());
        }
        return compare;
    }

    /**
     * Returns the timestamp.
     *
     * @return time - may be null
     */
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    /**
     * Returns the title of this {@code Post}.
     *
     * @return title - returns {@link Default#STRING} if not value was set
     */
    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        int result = getTitle() != null ? getTitle().hashCode() : 0;
        result = 31 * result + (getTimeStamp() != null ? getTimeStamp().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        if (getTitle() != null ? !getTitle().equals(post.getTitle()) : post.getTitle() != null) return false;
        return getTimeStamp() != null ? getTimeStamp().equals(post.getTimeStamp()) : post.getTimeStamp() == null;
    }

    @Override
    public String toString() {
        return "Post{" +
                "sticky=" + sticky +
                ", source=" + source.getUrl() +
                ", followLink='" + followLink + '\'' +
                ", title='" + title + '\'' +
                ", timeStamp=" + timeStamp +
                ", creation=" + creation +
                '}';
    }
}
