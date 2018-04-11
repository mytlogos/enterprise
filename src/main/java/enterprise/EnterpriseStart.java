package enterprise;

import enterprise.data.Default;
import enterprise.data.concurrent.OnCloseRun;
import enterprise.data.dataAccess.DataAccessManager;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.SourceableEntry;
import enterprise.gui.enterprise.Tasks;
import enterprise.modules.BasicModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scrape.concurrent.ScheduledPostScraper;
import scrape.sources.Source;
import scrape.sources.posts.Post;
import scrape.sources.posts.PostManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EnterpriseStart extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler((t, throwable) -> {
            Default.LOGGER.log(Level.SEVERE, "exception occurred on Application-Thread", throwable);
            Thread thread = new Thread(new OnCloseRun());
            thread.setDaemon(false);
            thread.start();
        });


        new Thread(this::startUp).start();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/enterprise.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Enterprise");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.setOnCloseRequest(event -> startOnCloseOperations());
        primaryStage.show();
    }

    /**
     * Starts the {@link OnCloseRun}.
     */
    private void startOnCloseOperations() {
        Thread thread = new Thread(new OnCloseRun());
        thread.setDaemon(false);
        thread.start();
    }

    private void startUp() {
        final ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(new EntryLoader());
        executor.submit(new PostLoader());
//        executor.submit(DataAccessManager.manager::startUpdater);
        executor.shutdown();
    }

    /**
     * Loads all {@code CreationEntry} from the data source with the help
     * of the {@link DataAccessManager}.
     * Separates the Entries according to their {@link BasicModule}.
     * Makes the {@link enterprise.data.intface.Sourceable}s available for the {@link ScheduledPostScraper}.
     */
    private static class EntryLoader implements Runnable {

        @Override
        public void run() {
            Tasks.get().accept(this, "Lade Einträge aus der Datenbank...");
            Thread.currentThread().setName("Data-Loader");
            Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> Default.LOGGER.log(Level.SEVERE, "something occurred while loading data", throwable));

            Collection<CreationEntry> entries = DataAccessManager.manager.getEntries();

            for (CreationEntry creationEntry : entries) {
                creationEntry.getModule().addEntry(creationEntry);
            }

            PostManager instance = PostManager.getInstance();
            entries.stream()
                    .map(entry -> entry instanceof SourceableEntry ? (SourceableEntry) entry : null)
                    .filter(Objects::nonNull)
                    .forEach(instance::addSearchEntries);

            //starts the ScheduledPostScraper
            PostManager.getInstance().startScheduledScraper();

            Tasks.get().finish(this);
        }
    }

    /**
     * Loads Posts from the data source with the help of
     * {@link DataAccessManager}.
     */
    private static class PostLoader implements Runnable {
        private List<Post> filterPosts(List<Post> posts) {
            return posts.stream().filter(this::postFilter).collect(Collectors.toList());
        }

        private boolean postFilter(Post post) {
            if (post.isSticky()) {
                LocalDateTime limit = LocalDateTime.now().minusYears(1);
                return limit.isBefore(post.getTimeStamp());
            } else {
                LocalDateTime limit = LocalDateTime.now().minusWeeks(1);
                return limit.isBefore(post.getTimeStamp());
            }
        }

        @Override
        public void run() {
            Tasks.get().accept(this, "Lade Posts aus der Datenbank...");

            Thread.currentThread().setName("Post-Loader");
            final Collection<Post> posts = DataAccessManager.manager.get(Post.class);

            PostManager.getInstance().addPosts(posts);
            Map<Source, List<Post>> postMap = posts.stream().collect(Collectors.groupingBy(Post::getSource));

            for (Source source : postMap.keySet()) {
                postMap.get(source).
                        stream().
                        max(Comparator.comparing(Post::getTimeStamp)).ifPresent(post -> PostManager.getInstance().putPost(source, post));

            }
            //introduces new sources into the cache and replaces the post sources with
            //the cached ones or the post source
            posts.forEach(post -> post.setSource(Source.cache(post.getSource())));

            Tasks.get().finish(this);
        }


    }
}
