package Enterprise.gui.general;

import Enterprise.data.intface.Sourceable;
import Enterprise.gui.enterprise.controller.PostView;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Worker;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import scrape.concurrent.ScheduledScraper;
import scrape.concurrent.ScrapeService;
import scrape.sources.Post;
import scrape.sources.SourceList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Singleton to provide global Access and Functions to
 * scrape and provide Posts from the Internet.
 * In the future it will be either expanded or divided
 * to cover other contents too.
 */
public class PostManager {
    private final PostList posts = new PostList();
    private static final PostManager MANAGER = new PostManager();

    /**
     * This [{@code Map} maps a {@code List} of keyWords to an {@link SourceList}.
     */
    private final Map<List<String>, SourceList> searchMap = new ConcurrentHashMap<>();

    LocalDateTime localDateTime;

    private final ScrapeService service = new ScrapeService();

    private final ScheduledScraper scheduledScraper = new ScheduledScraper();
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
        service.setOnSucceeded(event -> {
            System.out.println("Succeeded");
            List<Post> postList;
            postList = service.getValue();

            posts.removeAll(postList);
            posts.addAll(postList);
            System.out.println(posts);
            System.out.println(posts.size() + " Anzahl Posts");
        });

        initScheduled();
    }

    public static PostManager getInstance() {
        return MANAGER;
    }

    private void initScheduled() {
        scheduledScraper.progressProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
        scheduledScraper.messageProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));

        scheduledScraper.setOnSucceeded(event -> {
            List<Post> postList;
            postList = scheduledScraper.getValue();

            postList.removeAll(posts);

            posts.addAll(postList);

            newPosts.clear();
            newPosts.addAll(postList);

            if (!newPosts.isEmpty()) {
                Notifications.
                        create().
                        title("Neue Posts").
                        text("Es gibt " + newPosts.size() + " neue Posts.").
                        hideAfter(Duration.minutes(1)).
                        onAction(event1 -> PostView.getInstance().openNew()).
                        show();
            }
        });
        scheduledScraper.setOnReady(event -> System.out.println("Message ready: " + scheduledScraper.getMessage()));
        scheduledScraper.setOnRunning(event -> System.out.println("Message running: " + scheduledScraper.getMessage()));
    }

    public PostList getNewPosts() {
        return (PostList) newPosts;
    }

    public void clearNew() {
        newPosts.clear();
    }

    /**
     * Adds key-Value pairs of the {@code Sourceable} to the {@link #searchMap}.
     *
     * @param entry {@code Sourceable} with data to add a new key-value pair
     */
    public void addSearchEntries(Sourceable entry) {
        List<String> stringList = entry.getKeyWordList();
        SourceList sources = entry.getSourceList();

        searchMap.put(stringList, sources);
    }

    public void removeSearchEntries(List<String> stringList) {
        searchMap.remove(stringList);
    }

    /**
     * Starts the {@link ScrapeService}.
     */
    public void startSearching() {
        if (!(service.getState() == Worker.State.SCHEDULED) && !(service.getState() == Worker.State.RUNNING)) {
            service.reset();
            service.setSearchMap(searchMap);
            service.start();
        }else
            System.out.println("Service läuft noch.");
    }

    /**
     * Gets the instance of the {@link ScheduledScraper} in this class.
     *
     * @return instance of {@code ScheduledScraper}
     */
    public ScheduledScraper getScheduledScraper() {
        return scheduledScraper;
    }

    /**
     * Starts this instance of the {@link ScheduledScraper}.
     *
     * @see ScheduledService#start()
     */
    public void startScheduledScraper() {
        scheduledScraper.start();
    }

    /**
     * Cancels the {@link ScheduledScraper}.
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
     * Returns the one-time executing {@code ScrapeService}.
     *
     * @return instance of {@code ScrapeService}
     */
    public ScrapeService getService() {
        return service;
    }

    /**
     * Returns the {@code searchMap} instance.
     *
     * @return map
     */
    public Map<List<String>, SourceList> getSearchMap() {
        return searchMap;
    }
}
