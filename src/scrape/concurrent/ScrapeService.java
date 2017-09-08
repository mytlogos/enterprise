package scrape.concurrent;

import Enterprise.misc.Log;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import scrape.sources.Post;
import scrape.sources.Source;
import scrape.sources.SourceList;
import scrape.sources.novels.NovelPosts;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A one-time executing Scraper for testing purposes.
 */
public class ScrapeService extends Service<List<Post>> {

    private Logger logger = Log.packageLogger(this);

    private Map<List<String>, SourceList> searchMap = new HashMap<>();

    public ScrapeService() {
    }

    /**
     * Sets the searchMap of this {@code ScrapeService}.
     *
     * @param searchMap map, which holds the sources and keyWords to search for
     * @throws NullPointerException if searchMap is null
     */
    public void setSearchMap(Map<List<String>, SourceList> searchMap) {
        if (searchMap != null) {
            this.searchMap = searchMap;
        } else {
            throw new NullPointerException("searchMap can not be null!");
        }
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
                Thread.currentThread().setName("ScrapePosts");

                updateMessage("LadeService wird durchgeführt...");

                updateTitle("ScrapePost");

                separateMapContent();

                //aborts execution if there is no Work to do
                if (maxWork == 0) {
                    return new ArrayList<>();
                }

                //start the Progressbar
                updateProgress(progress, maxWork);

                System.out.println("SourceListen ohne Schlüssel " + noKeySources);
                System.out.println("SourceListen mit Schlüssel " + keySources);

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
                NovelPosts host = new NovelPosts();

                for (List<String> keyWordList : keySources.keySet()) {

                    SourceList sources = keySources.get(keyWordList);

                    //searches through every given Source with the given keyWords
                    for (Source source : sources) {
                        try {
                            updateMessage("Lade Posts von " + source.getSourceName());
                            //loads the HTTP document of the given URL
                            host.load(source.getUrl());

                            //searches for the keyWord in Posts, wraps the content in Post objects and gathers them
                            for (String keyWord : keyWordList) {

                                List<Post> posts = host.getSpecificPosts(keyWord);

                                postList.addAll(posts);
                            }
                        } catch (MalformedURLException | IllegalArgumentException e) {
                            // TODO: 21.08.2017 look if this catch clause is really necessary
                            logger.log(Level.WARNING, "Searching for specific Posts failed", e);

                        } catch (IOException | URISyntaxException e) {
                            logger.log(Level.WARNING, "Failed in getting the HTTP Document", e);
                        }

                        progress++;
                        System.out.println("Fortschritt key " + progress);
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
                NovelPosts host = new NovelPosts();

                for (SourceList noKeySource : noKeySources) {
                    for (Source source : noKeySource) {
                        updateMessage("Lade Posts von " + source.getSourceName());
                        try {
                            List<Post> posts1 = host.getPosts(source);
                            postList.addAll(posts1);

                        } catch (MalformedURLException | IllegalArgumentException e) {
                            // TODO: 21.08.2017 look if this catch clause is really necessary
                            logger.log(Level.WARNING, "Searching for specific Posts failed", e);

                        } catch (IOException | URISyntaxException e) {
                            logger.log(Level.WARNING, "Failed in getting the HTTP Document", e);
                        }
                        progress++;
                        System.out.println("Fortschritt noKey " + progress);
                        updateProgress(progress, maxWork);

                        // TODO: 21.08.2017 this log is for debug purposes
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

                for (List<String> keyWords : keySources.keySet()) {
                    result += keyWords.size();
                    result += keySources.get(keyWords).size();
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
