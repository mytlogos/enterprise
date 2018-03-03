package scrape.sources.posts;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.Scraper;
import scrape.sources.PostsFilter;
import scrape.sources.Source;
import scrape.sources.posts.strategies.PostConfigsSetter;
import scrape.sources.posts.strategies.PostFormat;
import scrape.sources.posts.strategies.intface.ArchiveSearcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * // TODO: 02.10.2017 apply filter earlier
 * // TODO: 02.10.2017 problems in getting the right posts
 */
public class PostScraper extends Scraper<PostConfigs, PostSearchEntry> {
    private final Elements unformattedElements = new Elements();
    private Elements formattedElements;

    public static PostScraper scraper(PostSearchEntry searchEntry) throws IOException {
        validate(searchEntry);
        PostScraper scraper = new PostScraper();
        scraper.init(searchEntry);
        return scraper;
    }

    private static void validate(PostSearchEntry searchEntry) {
        Objects.requireNonNull(searchEntry, "null for searchEntry not allowed");
    }

    private void init(PostSearchEntry entry) throws IOException {
        this.source = entry.getSource();
        initScraper();
        this.search = entry;
    }

    private void initScraper() throws IOException {
        document = getDocument(source.getUrl());
        this.configs = source.getPostConfigs();
        if (!configs.isInit()) {
            initConfigs(document);
        }
    }

    public static PostScraper scraper(Source source) throws IOException {
        Objects.requireNonNull(source);
        PostScraper scraper = new PostScraper();
        scraper.init(source);
        scraper.formattedElements = scraper.getAll();
        return scraper;
    }

    private void init(Source source) throws IOException {
        this.source = source;
        initScraper();
    }

    public List<Post> getPosts(PostSearchEntry entry) {
        validate(entry);
        Objects.requireNonNull(document);

        this.search = entry;
        Elements filtered = new PostsFilter(search).filter(unformattedElements);
        return new PostParser().toPosts(filtered, entry);
    }

    private Elements getAll() {
        return search(configs.isArchive());
    }

    private Elements search(boolean isArchive) {
        Elements elements;
        if (isArchive) {
            elements = getArchivePosts(configs.getArchive());
        } else {
            // TODO: 14.09.2017 do it with Pages
            elements = postsFromPage(document);
            /*if (!checkTime(elements)) {
                checkSecondaryPages(document, elements);
            }*/
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

    public List<Post> getPosts() {
        Objects.requireNonNull(document);
        Elements elements = getPostElements();
        return new PostParser().toPosts(elements, search);
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
        Post post = search.getSource().getNewestPost(search.getCreation());

        if (post == null) {
            //not more than 100 posts at once
            return elements.size() < 100;
        } else {
            return post.getTimeStamp().compareTo(dateTime) >= 0;
        }
    }

    private Elements getPostElements() {
        formattedElements = getAll();
        if (search.getKeyWords().isEmpty()) {
            return formattedElements;
        } else {
            Elements elements = new PostsFilter(search).filter(unformattedElements);
            return new PostFormat().format(elements, source.getPostConfigs());
        }
    }

    private void initConfigs(Document document) {
        new PostConfigsSetter(configs, document).setConfigs();
    }

    private Elements postsFromPage(Document document) {
        Document cleaned = cleanDoc(document);
        unformattedElements.addAll(new PostFormat().unFormatted(cleaned, configs));
        return new PostFormat().format(cleaned, configs);
    }

    /**
     * Returns the oldest {@code LocalDateTime} of the attributes specified by {@link PostFormat}
     * of {@code elements}.
     * If there is no oldest {@code LocalDateTime}, it will return {@code LocalDateTime.now()}
     *
     * @param elements elements to getAll the oldest time from
     * @return oldest time or {@code LocalDateTime.now()}
     * @throws IllegalArgumentException if an element of the input parameter
     *                                  has no parsable time
     */
    private LocalDateTime getOldestTime(Elements elements) {
        List<LocalDateTime> localDateTimes = new ArrayList<>();

        for (Element element : elements) {
            String time = new PostFormat().getTime(element);
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

    private boolean checkSecondaryPages(Document document, Elements elements) {
        //check if this archive has a next page and getAll content
        while (hasNextPage(document)) {
            Elements secondaryElements = fromNextPage(document);
            elements.addAll(secondaryElements);

            if (checkTime(secondaryElements)) return true;
        }
        return false;
    }

    private boolean hasNextPage(Document document) {
        // TODO: 06.10.2017 implement this if needed
        return true;
    }

    private Elements fromNextPage(Document document) {
        // TODO: 08.09.2017 gets the next page if needed
        return new Elements();
    }
}
