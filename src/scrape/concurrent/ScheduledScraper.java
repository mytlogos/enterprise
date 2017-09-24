package scrape.concurrent;

import Enterprise.gui.general.PostManager;
import Enterprise.misc.Log;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import scrape.sources.Post;
import scrape.sources.Source;
import scrape.sources.SourceList;
import scrape.sources.novels.NovelPosts;
import scrape.sources.novels.PostScraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Scheduled Service, which searches for content in the internet specified in the {@code searchMap}.
 * <p>
 * The searchMap is a {@code HashMap}, where a list of {@code String}s maps to a {@link SourceList}.
 * </p>
 * The list of Strings are the keyWords, which the {@link NovelPosts} will search for in the {@link Source} of the mapped
 * {@code SourceList}.
 * If the StringList is empty, it will search for every default content in the mapped {@code sources}
 * of the {@code SourceList}.
 * <p>
 * At the moment this Service is specified to search for Posts.
 * It will maybe further expanded to search for more, or delegate this task to another class.
 * </p>
 */
public class ScheduledScraper extends ScheduledService<List<Post>> {

    private Logger logger = Log.packageLogger(this);

    private Map<List<String>, SourceList> searchMap = new HashMap<>();

    /**
     * The constructor of this {@code ScheduledScraper}
     * <p>Sets the scheduling Period and maximum FailureCount </p>
     */
    public ScheduledScraper() {
        setPeriod(Duration.minutes(0.2));
        setMaximumFailureCount(5);
    }

    @Override
    protected Task<List<Post>> createTask() {
        return new Task<List<Post>>() {

            List<SourceList> noKeySources = new ArrayList<>();
            Map<List<String>, SourceList> keySources = new HashMap<>();

            int maxWork = 0;
            int progress = 0;

            @Override
            protected List<Post> call() throws Exception {
                Thread.currentThread().setName("ScheduledScraper");

                //the searchMap supplied from the PostManager
                searchMap = PostManager.getInstance().getSearchMap();

                updateMessage("LadeService wird durchgeführt...");

                updateTitle("Scrape Posts");

                //fills noKeySources and keySources
                separateMapContent();

                System.out.println(searchMap);

                //aborts execution if there is no Work to do
                if (maxWork == 0) {
                    return new ArrayList<>();
                }

                //start the Progressbar
                updateProgress(progress, maxWork);

                //list which will hold the Posts
                List<Post> postList = new ArrayList<>();

                updateMessage("Searching for no specific Posts");
                //uses the noKeySources list
                searchForPosts(postList);

                updateMessage("Searching for specific Posts");
                //uses the keySources map
                searchForSpecificPosts(postList);

                updateMessage("Fertig");
                return postList;
            }

            /**
             * Searches for given keyWords through the every Source in the corresponding mapped SourceList
             * in {@code keySources}. Fails for specific sources if the SourceLinks are corrupt or invalid.
             *
             * @param postList list of {@link Post}s, to operate on
             */
            private void searchForSpecificPosts(List<Post> postList) {
                PostScraper host = new PostScraper();

                for (List<String> keyWordList : keySources.keySet()) {

                    SourceList sources = keySources.get(keyWordList);

                    //searches through every given Source with the given keyWords
                    for (Source source : sources) {
                        try {
                            updateMessage("Lade Posts von " + source.getSourceName());
                            //loads the HTTP document of the given URL
                            host.load(source);

                            //searches for the keyWord in Posts, wraps the content in Post objects and gathers them
                            for (String keyWord : keyWordList) {

                                List<Post> posts1 = host.getPosts(keyWord);
                                postList.addAll(posts1);
                            }
                        } catch (MalformedURLException | IllegalArgumentException e) {
                            // TODO: 21.08.2017 look if this catch clause is really necessary
                            logger.log(Level.WARNING, "Searching for specific Posts failed", e);

                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Failed in getting the HTTP Document", e);
                        }

                        progress++;
                        updateProgress(progress, maxWork);
                        
                        //logs if maxWork is smaller than the done progress
                        if (maxWork < progress) {
                            logger.log(
                                Level.INFO,
                                "maxWork smaller than progress, maxWork: " + maxWork + ", Progress: " + progress);
                        }
                    }
                }
            }

            /**
             * Searches for every available post with the sources given in {@code noKeySources}.
             * Fails for specific sources if the SourceLinks are corrupt or invalid.
             *
             * @param postList list of {@link Post}s, to be operated on
             */
            private void searchForPosts(List<Post> postList) {
                PostScraper host;

                for (SourceList noKeySource : noKeySources) {
                    for (Source source : noKeySource) {
                        updateMessage("Lade Posts von " + source.getSourceName());
                        try {
                            host = PostScraper.scraper(source);
                            List<Post> posts1 = host.getPosts();


                            postList.addAll(posts1);
                        } catch (MalformedURLException | IllegalArgumentException e) {
                            // TODO: 21.08.2017 look if this catch clause is really necessary
                            logger.log(Level.WARNING, "Searching for specific Posts failed", e);

                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Failed in getting the html Document", e);
                        }
                        progress++;
                        updateProgress(progress, maxWork);

                        //logs if maxWork is smaller than the done progress
                        if (maxWork < progress) {
                            logger.log(
                                Level.INFO,
                                "maxWork smaller than progress, maxWork: " + maxWork + ", Progress: " + progress);
                        }
                    }
                }
            }

            /**
             * Calculates the maximal Work with the sizes of the given Lists:
             * <p>every {@code keyWord}list and {@link SourceList}.</p>
             *
             * @param noKeySources {@link List} of SourceLists, with no keyWords
             * @param keySources {@link Map} of {@code keyWord}lists mapped to {@code SourceList}s
             * @return the calculated size of the work to do, returns zero by default, if the lists are empty
             */
            private int calcMaxWork(List<SourceList> noKeySources, Map<List<String>,SourceList> keySources ) {
                int result = 0;
                for (SourceList noKeySource : noKeySources) {
                    result  += noKeySource.size();
                }

                for (List<String> strings : keySources.keySet()) {
                    result += strings.size();
                    result += keySources.get(strings).size();
                }
                return result;
            }

            /**
             * Separates the Content of the {@code searchMap} based on the fact,
             * if the {@code List}s in the {@code keySet} are empty or not.
             * <p>Values with empty key Lists are added to the {@code noKeySources} List, Values with filled key Lists,
             * are put into the {@code keySources} Map</p>
             *
             * Executes the Method {@link #calcMaxWork(List, Map)} afterwards.
             */
            private void separateMapContent() {
                //gets the keys, the keyWords list, of the searchMap
                Set<List<String>> keySet = searchMap.keySet();

                //aborts if there are no keys
                if (keySet.isEmpty()) {
                    return;
                }
                //separates the empty keys and put their values into a list of SourceLists
                //puts not-empty keys with their values into a new HashMap
                for (List<String> strings : keySet) {
                    if (strings.isEmpty()) {
                        noKeySources.add(searchMap.get(strings));
                    } else {
                        keySources.put(strings, searchMap.get(strings));
                    }
                }
                //calculates the maximal Amount of sources and keyWords this ScheduledScraper has to search for
                maxWork = calcMaxWork(noKeySources,keySources);
            }

        };
    }
}
