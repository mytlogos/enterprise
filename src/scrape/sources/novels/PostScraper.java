package scrape.sources.novels;

import Enterprise.misc.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import scrape.sources.Post;
import scrape.sources.PostConfigs;
import scrape.sources.Source;
import scrape.sources.novels.strategies.ArchiveGetter;
import scrape.sources.novels.strategies.PostConfigsSetter;
import scrape.sources.novels.strategies.PostFormat;
import scrape.sources.novels.strategies.intface.ArchiveSearcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

/**
 *
 */
public class PostScraper {
    private Source source;
    private PostConfigs postConfigs;
    private Document document;

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

    public static Document cleanDoc(Document document) {
        Whitelist whitelist = Whitelist.relaxed();
        whitelist.addAttributes(":all", "id", "class", "style");
        whitelist.addAttributes("div", "class", "id");
        whitelist.addAttributes("a", "id", "datetime");
        whitelist.addAttributes("p", "data-timestamp");
        whitelist.addAttributes("time", "datetime");
        whitelist.addAttributes("abbr", "title");
        whitelist.addAttributes("span", "title");
        whitelist.addAttributes("em", "data-timestamp");
        whitelist.addTags("time");
        whitelist.addTags("style");
        whitelist.addTags("main");
        whitelist.addTags("article");
        document.getElementsByAttributeValueContaining("class", "comment").remove();
        document.getElementsByAttributeValueContaining("class", "share").remove();
        document.getElementsByAttributeValueContaining("id", "comment").remove();
        document.getElementsByAttributeValueContaining("id", "share").remove();

        String cleaned = Jsoup.clean(document.outerHtml(), document.baseUri(), whitelist);

        return Jsoup.parse(cleaned, document.baseUri());
    }

    public List<Post> getPosts(String match) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(document);

        if (match == null || match.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Elements elements = getPostElements(match);
        return new PostParser().toPosts(elements, source);
    }


    public List<Post> getPosts() {
        Objects.requireNonNull(source);
        Objects.requireNonNull(document);
        Elements elements = getPostElements(null);
        return new PostParser().toPosts(elements, source);
    }

    private Elements getPostElements(String match) {
        Elements posts = getAll();
        if (match == null) {
            return posts;
        } else {
            return filterPosts(posts, match);
        }
    }

    public Elements getElementPosts() {
        Objects.requireNonNull(source);
        Objects.requireNonNull(document);
        return getPostElements(null);
    }

    private Elements filterPosts(Elements posts, String match) {
        Elements elements = new Elements();
        for (Element post : posts) {
            if (post.text().contains(match)) {
                elements.add(post);
            } else {
                post.attributes().forEach(attribute -> {
                    if (attribute.getValue().contains(match)) {
                        elements.add(post);
                    }
                });
                post.childNodes().forEach(node -> node.attributes().forEach(attribute -> {
                    if (attribute.getValue().contains(match) && !elements.contains(post)) {
                        elements.add(post);
                    }
                }));
            }
        }
        return elements;
    }

    private Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }

    private void init(Source source) throws IOException {
        if (source.getConfigs().isInit()) {
            this.postConfigs = source.getConfigs();
        } else {
            this.postConfigs = null;
        }
        document = getDocument(source.getUrl());
        this.source = source;
    }

    private Elements getAll() {
        Elements elements;
        if (postConfigs == null) {
            elements = search(ArchiveGetter.hasArchive(document));
        } else {
            elements = search(postConfigs.isArchive());
        }
        return elements;
    }

    private void checkFeed() {
        if (Feed.hasFeed(source)) {
            Feed feed = new Feed();
            // TODO: 08.09.2017
        }
    }

    private Elements search(boolean isArchive) {
        Elements elements;
        if (isArchive) {
            elements = searchArchives();
        } else {
            // TODO: 14.09.2017 do it with Pages
            elements = postsFromPage(document);
            /*if (!checkTime(elements)) {
                checkSecondaryPages(document, elements);
            }*/
        }
        return elements;
    }

    private Elements postsFromPage(Document document) {
        Document cleaned = cleanDoc(document);
        if (postConfigs == null) {
            return initConfigs(cleaned);
        } else {
            return PostFormat.format(cleaned, postConfigs);
        }
    }

    private Elements initConfigs(Document document) {
        PostConfigs configs = source.getConfigs();

        if (PostConfigsSetter.setPostConfigs(configs, document)) {
            return PostFormat.format(document, configs);
        } else {
            Log.classLogger(this).log(Level.WARNING, source.getUrl() + " is not supported");
            return new Elements();
        }
    }

    private Elements searchArchives() {
        Elements elements = new Elements();
        if (postConfigs == null) {
            ArchiveSearcher searcher = ArchiveGetter.getArchiveSearcher(document);

            if (searcher != null) {
                elements = getArchivePosts(searcher);
            }
        } else {
            elements = getArchivePosts(postConfigs.getArchive());
        }
        return elements;
    }

    /**
     * Iterates over available archives and scrapes their posts.
     * Stops if no archive is available anymore or the oldest scraped
     * {@code Post} is older than the newest post of the source.
     *
     * @param archive archiveSearcher to use for this source
     * @return formatted scraped Elements
     */
    private Elements getArchivePosts(ArchiveSearcher archive) {
        Elements postElements = new Elements();
        Iterator<Document> iterator = archive.iterator(document);

        while (iterator.hasNext()) {
            Document document = iterator.next();
            Elements elements = postsFromPage(document);
            postElements.addAll(elements);

            //if timeStamp from newest stored post in source equals/is greater than oldest scraped time,
            //should stop scraping for posts
            if (checkTime(elements)) break;
//                if (checkSecondaryPages(document, elements)) break;
        }
        return postElements;
    }

    private boolean checkSecondaryPages(Document document, Elements elements) {
        //check if this archive has a next page and get content
        while (hasNextPage(document)) {
            Elements secondaryElements = fromNextPage(document);
            elements.addAll(secondaryElements);

            if (checkTime(secondaryElements)) return true;
        }
        return false;
    }

    private Elements fromNextPage(Document document) {
        // TODO: 08.09.2017 gets the next page
        return new Elements();
    }

    private boolean hasNextPage(Document document) {
        return true;
    }

    /**
     * Checks if a an element of the input parameter has an
     * time attribute value which is older than the time of the newest
     * Post of the source.
     * If no newest Post is available, it will check the size
     * if the size of elements is smaller than 100.
     *
     * @param elements elements to check
     * @return true if the time of the newest Post is greater then the oldest post of
     */
    private boolean checkTime(Elements elements) {
        LocalDateTime dateTime = getOldestTime(elements);
        Post post = source.getNewestPost(null);

        if (post == null) {
            //not more than 100 posts at once
            return elements.size() < 100;
        } else {
            return post.getTimeStamp().compareTo(dateTime) >= 0;
        }
    }

    /**
     * Returns the oldest {@code LocalDateTime} of the attributes specified by {@link PostFormat}
     * of {@code elements}.
     * If there is no oldest {@code LocalDateTime}, it will return {@code LocalDateTime.now()}
     *
     * @param elements elements to get the oldest time from
     * @return oldest time or {@code LocalDateTime.now()}
     * @throws IllegalArgumentException if an element of the input parameter
     *                                  has no parsable time
     */
    private LocalDateTime getOldestTime(Elements elements) {
        List<LocalDateTime> localDateTimes = new ArrayList<>();

        for (Element element : elements) {
            String time = PostFormat.getTime(element);
            LocalDateTime localDateTime = ParseTime.parseTime(time);

            if (localDateTime == null) {
                throw new IllegalArgumentException("no parsable Time available");
            } else {
                localDateTimes.add(localDateTime);
            }
        }
        //returns the max localDateTime and if no result is present, it returns now()
        return localDateTimes.stream().min(LocalDateTime::compareTo).orElse(LocalDateTime.now());
    }

}
