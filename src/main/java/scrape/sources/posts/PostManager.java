package scrape.sources.posts;

import enterprise.data.intface.Creation;
import enterprise.data.intface.SourceableEntry;
import enterprise.gui.enterprise.PostView;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.ScheduledService;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import scrape.concurrent.ScheduledPostScraper;
import scrape.sources.Source;
import scrape.sources.SourceList;

import java.util.*;

/**
 * A Singleton to provide global Access and Functions to
 * scrape and provide Posts from the Internet.
 * In the future it will be either expanded or divided
 * to cover other contents too.
 */
public class PostManager {
    private static final PostManager MANAGER = new PostManager();
    private final PostList posts = new PostList();
    /**
     * This [{@code Map} maps a {@code List} of keyWords to an {@link SourceList}.
     */
    private final Set<PostSearchEntry> searchEntries = new HashSet<>();

    private final ScheduledPostScraper scheduledScraper = new ScheduledPostScraper();
    private final List<Post> newPosts = new PostList();

    /**
     * The constructor of this {@code PostManager}.
     * Sets the behaviour of the scraper on a
     * {@link javafx.concurrent.Worker.State#SUCCEEDED} event.
     */
    private PostManager() {
        if (MANAGER != null) {
            throw new IllegalStateException();
        }
        initScheduled();
    }

    private void initScheduled() {
        scheduledScraper.setOnSucceeded(event -> {
            List<Post> postList;
            postList = scheduledScraper.getValue();

            postList.removeAll(posts);
            addPosts(postList);

            newPosts.clear();
            newPosts.addAll(postList);

            if (!newPosts.isEmpty()) {
                showNotification();
            }
        });
    }

    public static PostManager getInstance() {
        return MANAGER;
    }

    private void showNotification() {
        Notifications.
                create().
                title("Neue Posts").
                text("Es gibt " + newPosts.size() + " neue Posts.").
                hideAfter(Duration.minutes(1)).
                onAction(event -> PostView.getInstance().openNew()).
                show();
    }

    public void addPosts(Collection<Post> posts) {
        this.posts.addAll(posts);
        this.posts.sort(PostView.getInstance().SORTED_BY.getComparator());
    }

    public PostList getNewPosts() {
        return (PostList) newPosts;
    }

    public void clearNew() {
        newPosts.clear();
    }

    /**
     * Adds entry to the {@link #searchEntries}.
     *
     * @param sourceableEntry {@code Sourceable} with data to add a new key-value pair
     */
    public void addSearchEntries(SourceableEntry sourceableEntry) {
        System.out.println("converting into searchEntry");
        List<PostSearchEntry> entries = convert(sourceableEntry);
        System.out.println("adding searchEntries");
        searchEntries.addAll(entries);
    }

    private List<PostSearchEntry> convert(SourceableEntry sourceableEntry) {
        List<String> keyWords = sourceableEntry.getUser().getKeyWordList();
        Creation creation = sourceableEntry.getCreation();

        List<PostSearchEntry> entries = new ArrayList<>();

        //todo enable sourceList from hibernate, else it stays null

        SourceList sourceList = sourceableEntry.getSourceable().getSourceList();
        if (sourceList != null) {
            for (Source source : sourceList) {
                PostSearchEntry entry = new PostSearchEntry(creation, source, keyWords);
                entries.add(entry);
            }
        }

        return entries;
    }

    public void removeSearchEntries(SourceableEntry entry) {
        searchEntries.removeAll(convert(entry));
    }

    /**
     * Gets the instance of the {@link ScheduledPostScraper} in this class.
     *
     * @return instance of {@code ScheduledPostScraper}
     */
    public ScheduledPostScraper getScheduledScraper() {
        return scheduledScraper;
    }

    /**
     * Starts this instance of the {@link ScheduledPostScraper}.
     *
     * @see ScheduledService#start()
     */
    public void startScheduledScraper() {
        scheduledScraper.start();
    }

    /**
     * Cancels the {@link ScheduledPostScraper}.
     *
     * @see ScheduledService#cancel()
     */
    public void cancelScheduledScraper() {
        scheduledScraper.cancel();
    }

    /**
     * Returns the {@link ScheduledService#progressProperty()} of this {@code scheduledScraper}.
     *
     * @return progressProperty - a read-Only Property
     */
    public ReadOnlyDoubleProperty progressProperty() {
        return scheduledScraper.progressProperty();
    }

    /**
     * Returns the {@link ScheduledService#messageProperty()} of this {@code scheduledScraper}.
     *
     * @return progressProperty - a read-Only Property
     */
    public ReadOnlyStringProperty messageProperty() {
        return scheduledScraper.messageProperty();
    }

    /**
     * Returns the {@code PostList} of this instance.
     *
     * @return a {@code List} of {@link Post}s
     */
    public PostList getPosts() {
        return posts;
    }

    /**
     * Returns the {@code searchMap} instance.
     *
     * @return map
     */
    public Set<PostSearchEntry> getSearch() {
        return searchEntries;
    }
}
