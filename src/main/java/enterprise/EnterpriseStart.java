package enterprise;

import enterprise.data.Default;
import enterprise.data.concurrent.OnCloseRun;
import enterprise.data.dataAccess.DataAccessManager;
import enterprise.data.intface.CreationEntry;
import enterprise.gui.enterprise.Tasks;
import enterprise.modules.BasicModule;
import gorgon.external.Gorgon;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class EnterpriseStart extends Application {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        launch(args);
    }

    private static List<Post> filterPosts(List<Post> posts) {
        return posts.stream().filter(EnterpriseStart::postFilter).collect(Collectors.toList());
    }

    private static boolean postFilter(Post post) {
        if (post.isSticky()) {
            LocalDateTime limit = LocalDateTime.now().minusYears(1);
            return limit.isBefore(post.getTimeStamp());
        } else {
            LocalDateTime limit = LocalDateTime.now().minusWeeks(1);
            return limit.isBefore(post.getTimeStamp());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startUp();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/enterprise.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Enterprise");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.setOnCloseRequest(event -> onCloseOperations());
        primaryStage.show();
    }

    private void startUp() {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            Default.LOGGER.log(Level.SEVERE, "exception occurred on Application-Thread", throwable);
            new Thread(new OnCloseRun()).start();
        });

        Runnable getEntryData = new Runnable() {
            @Override
            public void run() {
                loadEntryData(this);
            }
        };
        Runnable loadPosts = new Runnable() {
            @Override
            public void run() {
                loadPosts(this);
            }
        };

        executor.submit(getEntryData);
        executor.submit(loadPosts);
        executor.shutdown();

        Tasks.get().accept(getEntryData, "Lade Einträge aus der Datenbank...");
        Tasks.get().accept(loadPosts, "Lade Posts aus der Datenbank...");
        DataAccessManager.manager.startUpdater();
    }

    /**
     * Executes tasks on a Window Close Request.
     * Cancels the {@link ScheduledPostScraper} and
     * starts the {@link OnCloseRun}.
     */
    private void onCloseOperations() {
        Thread thread = new Thread(new OnCloseRun());
        thread.setDaemon(false);
        thread.start();
        PostManager.getInstance().cancelScheduledScraper();
        System.out.println("Fenster wird geschlossen!");
    }

    /**
     * Starts the {@link Gorgon#startUpdater(long, TimeUnit)} to getAll all {@link CreationEntry}s from the source of data.
     * Separates the Entries according to their {@link BasicModule}s.
     * Makes the {@link enterprise.data.intface.Sourceable}s available for the {@link ScheduledPostScraper}.
     *
     * @param runnable runnable which is executing this method
     */
    private void loadEntryData(Runnable runnable) {
        Thread.currentThread().setName("Data-Loader");
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> throwable.printStackTrace());

        Collection<CreationEntry> entries = DataAccessManager.manager.getEntries();

        for (CreationEntry creationEntry : entries) {
            creationEntry.getModule().addEntry(creationEntry);
        }

        /*PostManager instance = PostManager.getInstance();
        entries.stream()
                .map(entry -> entry instanceof SourceableEntry ? (SourceableEntry) entry : null)
                .filter(Objects::nonNull)
                .forEach(instance::addSearchEntries);*/

        Tasks.get().finish(runnable);
    }

    private void loadPosts(Runnable runnable) {
        Thread.currentThread().setName("Post-Loader");
        final Collection<Post> posts = DataAccessManager.manager.get(Post.class);

        PostManager.getInstance().addPosts(posts);
        Map<Source, List<Post>> postMap = mapWithSource(posts);

        for (Source source : postMap.keySet()) {
            Post post = postMap.get(source).
                    stream().
                    max(Comparator.comparing(Post::getTimeStamp)).
                    orElse(null);
            if (post != null) {
                source.putPost(post.getCreation(), post);
            } else {
                source.putPost(null, null);
            }
        }
        Tasks.get().finish(runnable);
    }

    private Map<Source, List<Post>> mapWithSource(Collection<Post> posts) {
        return posts.stream().collect(Collectors.groupingBy(Post::getSource));
    }
}
