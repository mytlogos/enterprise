package enterprise.test;

import enterprise.data.Default;
import scrape.sources.Source;
import scrape.sources.posts.Post;
import tools.Log;
import tools.TimeMeasure;

import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static scrape.sources.SourceType.START;

/**
 * Created on 08.08.2017.
 *
 */
public class tester {
    static Connection con;
    static Logger logger = Log.classLogger(tester.class);

    private static Source source;

    /**
     * Searches for given keyWords through the every Source in the corresponding mapped SourceList
     * in {@code keySources}. Fails for specific sources if the SourceLinks are corrupt or invalid.
     */
    /*private void searchForSpecificPosts(List<Post> postList) {
        PostScraper host = new PostScraper();

        for (List<String> keyWordList : keySources.keySet()) {

            ObservableList<Source>sources = keySources.getAll(keyWordList);

            //searches through every given Source with the given keyWords
            for (Source source : sources) {
                try {
                    updateMessage("Lade Posts von " + source.getSourceName());
                    //loads the HTTP document of the given URL
                    host.load(source);

                    //searches for the keyWord in Posts, wraps the content in Post objects and gathers them
                    for (String keyWord : keyWordList) {

                        List<Post> posts = host.getPosts(keyWord);
                        postList.addAll(posts);
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
    }*/
    public static void main(String... aArgs) {

        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int number = new Random().nextInt(4);
            for (int j = 0; j < 20; j++) {
                posts.add(generatePost(j, number));
            }
        }

/*
        String ROOT = "";
        FileVisitor<Path> fileProcessor = new ProcessFile();
        Files.walkFileTree(Paths.getAll(ROOT), fileProcessor);
        */
    }

    private static Post generatePost(int difference, int number) {
        LocalDateTime dateTime;
        if (number == 0) {
            dateTime = LocalDateTime.now().minusWeeks(difference);
        } else if (number == 1) {
            dateTime = LocalDateTime.now().minusDays(difference);
        } else if (number == 2) {
            dateTime = LocalDateTime.now().minusMonths(difference);
        } else if (number == 3) {
            dateTime = LocalDateTime.now().minusYears(difference);
        } else {
            dateTime = LocalDateTime.now();
        }
        return new Post(Default.SOURCE, "titel", dateTime, Default.SOURCE.getUrl(), null, false);
    }

    /**
     * Separates the Content of the {@code searchEntries} based on the fact,
     * if the {@code List}s in the {@code keySet} are empty or not.
     * <p>Values with empty key Lists are added to the {@code noKeySources} List, Values with filled key Lists,
     * are put into the {@code keySources} Map</p>
     */
    /*private void separateMapContent() {
        //gets the keys, the keyWords list, of the searchEntries
        Set<List<String>> keySet = searchEntries.keySet();

        //aborts if there are no keys
        if (keySet.isEmpty()) {
            return;
        }
        //separates the empty keys and put their values into a list of SourceLists
        //puts not-empty keys with their values into a new HashMap
        for (List<String> strings : keySet) {
            if (strings.isEmpty()) {
                noKeySources.add(searchEntries.getAll(strings));
            } else {
                keySources.put(strings, searchEntries.getAll(strings));
            }
        }
        //calculates the maximal Amount of sources and keyWords this ScheduledPostScraper has to search for
        maxWork = calcMaxWork(noKeySources,keySources);
    }*/
    private static Predicate<Post> timeFilter() {
        return post -> {
            if (post.isSticky()) {
                LocalDateTime limit = LocalDateTime.now().minusYears(1);
                return limit.isBefore(post.getTimeStamp());
            } else {
                LocalDateTime limit = LocalDateTime.now().minusWeeks(1);
                return limit.isBefore(post.getTimeStamp());
            }
        };
    }

    private static void repeatingSources(final int repetitions) throws URISyntaxException {
        TimeMeasure measure = TimeMeasure.start();
        for (int i = 0; i < repetitions; i++) {
            for (String s : mapclass.allLinks()) {
                source = Source.create(s, START);
            }
        }
        measure.finish();
        System.out.println(measure.getMessage(s -> s + " \ttime needed for creating " + (mapclass.allLinks().size() * repetitions) + " \tsources"));
    }

    private static void repeatingOneSource(final int repetitions) throws URISyntaxException {
        TimeMeasure measure = TimeMeasure.start();
        for (int i = 0; i < repetitions * 70; i++) {
            source = Source.create(mapclass.allLinks().get(1), START);
        }
        measure.finish();
        System.out.println(measure.getMessage(s -> s + " \ttime needed for creating " + (mapclass.allLinks().size() * repetitions) + " \tsources"));
    }

    static Connection getCon() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }

    static void doSth() {
        LocalDateTime first = LocalDateTime.now();
        for (int x = 0; x < 100000; x++) {
            String string = "Method " + new Object() {
            }.getClass().getEnclosingMethod().getName();
        }
        LocalDateTime second = LocalDateTime.now();
        for (int x = 0; x < 100000; x++) {
            StackTraceElement[] elements = new Throwable().getStackTrace();
            String string = "Method " + elements[0];
        }
        LocalDateTime third = LocalDateTime.now();
        LocalDateTime temp = LocalDateTime.now();
        for (int i = 0; i < 100000; i++) {
            LocalDateTime loop = LocalDateTime.now();
            System.out.println(loop.getNano() - temp.getNano());
            temp = loop;
        }
        System.out.println("Erste Schleife: " + (second.getNano() - first.getNano()));
        System.out.println("Zweite Schleife: " + (third.getNano() - second.getNano()));
    }

    private static final class ProcessFile extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(
                Path aDir, BasicFileAttributes aAttrs
        ) {
            System.out.println("Processing directory:" + aDir);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(
                Path aFile, BasicFileAttributes aAttrs
        ) {
            System.out.println("Processing file:" + aFile);
            return FileVisitResult.CONTINUE;
        }
    }

}
