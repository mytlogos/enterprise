package scrape.sources.posts;

import Enterprise.data.Default;
import Enterprise.data.impl.AbstractDataEntry;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.DataEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import scrape.sources.Source;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Post of a blog.
 * It is immutable.
 */
public class Post extends AbstractDataEntry implements Comparable<Post>, DataEntry {
    private boolean sticky;
    private Source source;
    private String followLink;
    private String title;
    private List<String> content = new ArrayList<>();
    private String footer = Default.STRING;
    private LocalDateTime timeStamp;
    private Creation creation;


    /**
     * The constructor of {@code Post}.
     */
    public Post(Source source, String title, LocalDateTime dateTime, String followLink, Creation creation, boolean isSticky) {
        this(Default.VALUE, source, title, dateTime, followLink, creation, isSticky);
    }

    public Post(int id, Source source, String title, LocalDateTime dateTime, String followLink, Creation creation, boolean isSticky) {
        super(id);
        this.title = title;
        this.timeStamp = dateTime;
        this.followLink = followLink;
        this.creation = creation;
        this.sticky = isSticky;
        if (source != null) {
            this.source = source;
        } else {
            this.source = Default.SOURCE;
        }
        validateState();
    }

    public Post(Source source, String title, List<String> content, String footer, LocalDateTime timeStamp, String followLink, boolean isSticky) {
        this(source, title, timeStamp, followLink, null, isSticky);
        this.content = content;
        this.footer = footer;
        validateState();
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
        if (content == null) {
            message = message + "content is null, ";
        }
        if (footer == null) {
            message = message + "footer is null";
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
     * Returns the timestamp.
     *
     * @return time - may be null
     */
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    /**
     * Returns a String representation of
     * the timestamp without timezone.
     *
     * @return string
     */
    public String getTime() {
        String timeString = String.valueOf(timeStamp.getDayOfMonth()) + "." +
                timeStamp.getMonthValue() + "." +
                timeStamp.getYear() + " " +
                timeStamp.getHour() + ":";
        String minute;
        if (timeStamp.getMinute() < 10) {
            minute = "0" + timeStamp.getMinute();
        } else {
            minute = "" + timeStamp.getMinute();
        }
        String second;
        if (timeStamp.getSecond() < 10) {
            second = "0" + timeStamp.getSecond();
        } else {
            second = "" + timeStamp.getSecond();
        }
        timeString = timeString.concat(minute).concat(":").concat(second);
        return timeString;
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
     * Returns the title of this {@code Post}.
     *
     * @return title - returns {@link Default#STRING} if not value was set
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the content of the {@code Post}.
     *
     * @return the content in a {@code List}, every element represents one paragraph.
     * Is empty if no value was set
     */
    public List<String> getContent() {
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
     * Returns the {@code content} of this {@code Post}
     * as an String, with every element in one line each.
     *
     * @return content - as an {@code String}
     */
    public String getContentString() {
        StringBuilder builder = new StringBuilder();
        for (String s : content) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }

    /**
     * @return
     */
    public boolean isSticky() {
        return sticky;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(Post o) {
        int compare;
        compare = o.getTimeStamp().compareTo(timeStamp);
        if (compare == 0) {
            compare = title.compareTo(o.getTitle());
        }
        return compare;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof Post) {
            if (equal = title.equalsIgnoreCase(((Post) obj).getTitle())) {
                equal = timeStamp.isEqual(((Post) obj).getTimeStamp());
            }
        }
        return equal;
    }

    @Override
    public int hashCode() {
        int result = 31 * timeStamp.hashCode();
        result = 31 * result * title.hashCode();
        return result;
    }

    @Override
    public void setUpdated() {
    }

    @Override
    public boolean isUpdated() {
        return false;
    }

    @Override
    public BooleanProperty updatedProperty() {
        return new SimpleBooleanProperty();
    }

    @Override
    protected void bindUpdated() {
        throw new IllegalAccessError();
    }
}
