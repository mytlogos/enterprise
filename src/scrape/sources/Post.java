package scrape.sources;

import Enterprise.data.Default;
import Enterprise.data.intface.DataBase;
import Enterprise.data.intface.Table;
import javafx.beans.property.BooleanProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Post of a blog.
 * It is mutable.
 */
public class Post implements Comparable<Post>, DataBase {


    private String title = Default.STRING;
    private List<String> content = new ArrayList<>();
    private String footer = Default.STRING;
    private LocalDateTime timeStamp;

    /**
     * The constructor of {@code Post}.
     */
    public Post() {
    }

    /**
     * Returns the timestamp.
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
     * Sets the timestamp of this {@code Post}.
     *
     * @param timeStamps valid timestamp to set
     */
    public void setTimeStamp(LocalDateTime timeStamps) {
        if (timeStamps == null) {
            throw new NullPointerException();
        }
        this.timeStamp = timeStamps;
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
     * Sets the title of this {@code Post}.
     *
     * @param title title to set
     * @throws IllegalArgumentException if {@code title} is null or empty
     */
    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            this.title = title;
        }
    }

    /**
     * Gets the content of the {@code Post}.
     *
     * @return the content in a {@code List}, every element represents one paragraph.
     *         Is empty if no value was set
     */
    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        if (content == null || content.isEmpty()) {
            this.content.add(Default.STRING);
        } else {
            this.content = content;
        }
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
     * Sets the {@code footer} of this {@code Post}.
     *
     * @param footer sets {@code footer} to {@link Default#STRING}
     *               parameter is {@code null} or empty
     */
    public void setFooter(String footer) {
        if (footer == null || footer.isEmpty()) {
            this.footer = Default.STRING;
        } else {
            this.footer = footer;
        }
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
    public int getId() {
        return 0;
    }

    @Override
    public void setId(int id, Table table) {

    }

    @Override
    public void setDead() {

    }

    @Override
    public void setAlive() {

    }

    @Override
    public void setEntryNew() {

    }

    @Override
    public void setEntryOld() {

    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public boolean isNewEntry() {
        return false;
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
        return null;
    }
}
