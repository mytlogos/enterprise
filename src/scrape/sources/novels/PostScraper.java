package scrape.sources.novels;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.Post;
import scrape.sources.PostConfigs;
import scrape.sources.Source;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class PostScraper {
    private Source source;
    private PostConfigs configs;
    private Document document;
    private LocalDateTime localDateTime;

    public static PostScraper scraper(Source source) throws IOException {
        Objects.requireNonNull(source, "null for source not allowed");
        PostScraper scraper = new PostScraper();
        scraper.init(source);
        return scraper;
    }

    public void load(Source source) throws IOException {
        Objects.requireNonNull(source, "null for source not allowed");
        init(source);
    }

    public List<Post> getPosts() {
        Objects.requireNonNull(source);
        Objects.requireNonNull(document);
        Elements elements = getPostElements(null);
        return new PostParser().toPosts(elements);
    }

    public List<Post> getPosts(String match) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(document);

        if (match == null || match.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Elements elements = getPostElements(match);
        return new PostParser().toPosts(elements);
    }

    private Elements getPostElements(String match) {
        Elements posts = getAll();
        if (match == null) {
            return posts;
        } else {
            return filterPosts(posts, match);
        }
    }

    private Elements filterPosts(Elements posts, String match) {
        // TODO: 07.09.2017
        return null;
    }

    private void init(Source source) throws IOException {
        if (source.getConfigs().isInit()) {
            this.configs = source.getConfigs();
        } else {
            this.configs = null;
        }
        document = getDocument(source.getUrl());
        this.source = source;
    }

    private Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }


    private Elements getAll() {
        checkFeed();
        Post post = source.getNewestPost(null);

        if (post != null) {
            localDateTime = post.getTimeStamp();
        }

        Elements elements;
        if (configs == null) {
            elements = search(SiteArchive.hasArchive(document));
        } else {
            elements = search(configs.isArchive());
        }
        return elements;
    }

    private Elements search(boolean bool) {
        Elements elements;
        if (bool) {
            elements = searchArchives();
        } else {
            elements = postsFromPage(document);
            while (!hasNextPage(document)) {
                Elements secondaryElements = fromNextPage(document);
                elements.addAll(secondaryElements);

                if (checkTime(secondaryElements)) break;
            }
        }
        return elements;
    }

    private Elements fromNextPage(Document document) {
        // TODO: 08.09.2017 gets the next page
        return new Elements();
    }

    private void checkFeed() {
        if (Feed.hasFeed(source)) {
            Feed feed = new Feed();
            // TODO: 08.09.2017
        }
    }

    private Elements postsFromPage(Document document) {
        // TODO: 08.09.2017  format all posts to specific format of
        // TODO: 08.09.2017 title, body, footer and timestamp
        Elements elements = new Elements();
        if (configs == null) {
            for (BasicNovelPosts basicNovelPosts : BasicNovelPosts.values()) {
                if (!(elements = basicNovelPosts.get(document)).isEmpty()) {

// TODO: 08.09.2017 check if not whole basicNovelPosts instance should be stored in source
                    configs.setBody(basicNovelPosts.bodyStrategy());
                    configs.setPosts(basicNovelPosts.postsStrategy());
                    break;
                }
            }
        } else {
            Elements bodyWithPosts = configs.getBody().get(document);
            elements = configs.getPosts().get(Jsoup.parse(bodyWithPosts.html()));
        }

        return elements;
    }

    private Elements searchArchives() {
        if (configs == null) {
            SiteArchive archive = SiteArchive.archiveSearcher(document);

            outer:
            {
                for (Document document : archive) {
                    Elements elements = postsFromPage(document);
                    //if timeStamp from newest stored post in source equals/is greater than oldest scraped time,
                    //should stop scraping for posts
                    if (checkTime(elements)) break;

                    //check if this archive has a next page and get content
                    while (hasNextPage(document)) {
                        Elements secondaryElements = fromNextPage(document);
                        elements.addAll(secondaryElements);

                        if (checkTime(secondaryElements)) break outer;
                    }
                }
            }
        } else {
            return configs.getArchive().get(document);
        }

        return null;
    }

    private boolean hasNextPage(Document document) {
        return false;
    }

    private boolean checkTime(Elements elements) {
        LocalDateTime dateTime = getOldestTime(elements);

        return source.getNewestPost(null).getTimeStamp().compareTo(dateTime) >= 0;
    }

    private LocalDateTime getOldestTime(Elements elements) {
        Elements timeElements = elements.select("time[timestamp]");
        List<LocalDateTime> localDateTimes = new ArrayList<>();
        for (Element timeElement : timeElements) {
            localDateTimes.add(ParseTime.parseTime(timeElement.attr("timestamp")));
        }
        //returns the max localDateTime and if no result is present, it returns now()
        return localDateTimes.stream().min(LocalDateTime::compareTo).orElse(LocalDateTime.now());
    }

}
