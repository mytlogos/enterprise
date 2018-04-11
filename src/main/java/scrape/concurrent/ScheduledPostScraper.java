package scrape.concurrent;

import enterprise.data.Default;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import scrape.sources.Source;
import scrape.sources.posts.*;
import tools.TimeMeasure;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Scheduled Service, which searches for content in the internet specified in the {@code searchEntries}.
 * <p>
 * The searchEntries is a {@code HashMap}, where a list of {@code String}s maps to a list of Sources.
 * </p>
 * The list of Strings are the keyWords, which the {@link PostScraper} will search for in the {@link Source} of the mapped
 * {@code SourceList}.
 * If the StringList is empty, it will search for every default content in the mapped {@code sources}
 * of the {@code SourceList}.
 * <p>
 * At the moment this Service is specified to search for Posts.
 * It will maybe further expanded to search for more, or delegate this task to another class.
 * </p>
 */
public class ScheduledPostScraper extends ScheduledService<List<Post>> {

    private final Logger logger = Default.LOGGER;

    private Map<Source, List<PostSearchEntry>> searchMap = new HashMap<>();
    private TimeMeasure measure;

    /**
     * The constructor of this {@code ScheduledPostScraper}
     * <p>Sets the scheduling Period and maximum FailureCount </p>
     */
    public ScheduledPostScraper() {
        setPeriod(Duration.minutes(0.2));
        setMaximumFailureCount(5);
    }

    @Override
    protected Task<List<Post>> createTask() {
        return new Task<List<Post>>() {

            int maxWork = 0;
            int progress = 0;

            @Override
            protected List<Post> call() {
                Thread.currentThread().setName("ScheduledPostScraper");

                measure = TimeMeasure.start();

                //the searchEntries supplied by the PostManager
                searchMap = PostManager.getInstance().getSearch().stream().collect(Collectors.groupingBy(PostSearchEntry::getSource));
                calcMaxWork();

                updateMessage("LadeService wird durchgeführt...");
                updateTitle("Scraping Posts...");

                //aborts execution if there is no Work to do
                if (maxWork == 0) {
                    return new ArrayList<>();
                }
                //start the Progressbar
                updateProgress(progress, maxWork);

                //list which will hold the Posts
                List<Post> postList = new PostList();

                updateMessage("Searching for Posts");
                //uses the noKeySources list
                searchForPosts(postList);

                updateMessage("Fertig");

                measure.finish();
                System.out.println(measure.getMessage(s -> "PostTime needed for scraping in seconds: " + s));

                return postList;
            }

            /**
             * Calculates the maximal Work with the sizes of the given Lists:
             * <p>every {@code keyWord}list and and list of {@link Source}.</p>
             *
             * @return the calculated size of the work to do, returns zero by default, if the lists are empty
             */
            private void calcMaxWork() {
                maxWork = searchMap.size();
                for (Source source : searchMap.keySet()) {
                    maxWork += searchMap.get(source).size();
                }
            }

            /**
             * Searches for every available post with the sources given in {@code noKeySources}.
             * Fails for specific sources if the SourceLinks are corrupt or invalid.
             *
             * @param postList list of {@link Post}s, to be operated on
             */
            private void searchForPosts(final List<Post> postList) {
                ExecutorService service = Executors.newFixedThreadPool(20);

                for (final Source source : searchMap.keySet()) {
                    updateMessage("Lade Posts von " + source.getSourceName());

                    service.submit(() -> {
                        try {
                            PostScraper scraper = PostScraper.scraper(source);

                            for (PostSearchEntry searchEntry : searchMap.get(source)) {
                                postList.addAll(scraper.getPosts(searchEntry));
                            }
                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Failed in getting the html Document", e);
                        }
                    });
                    progress++;
                    updateProgress(progress, maxWork);

                    //logs if maxWork is smaller than the done progress
                    if (maxWork < progress) {
                        logger.log(Level.INFO,
                                "maxWork smaller than progress, maxWork: "
                                        + maxWork +
                                        ", Progress: " +
                                        progress);
                    }
                }
                service.shutdown();
                while (!service.isTerminated()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                }
                System.out.println("finally terminated");
            }

            private void getBySource(Set<PostSearchEntry> searchEntries) {

            }
        };
    }
}
