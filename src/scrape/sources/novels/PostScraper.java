package scrape.sources.novels;

import Enterprise.misc.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import scrape.Post;
import scrape.SearchEntry;
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
    private SearchEntry search;
    private Source source;
    private PostConfigs postConfigs;
    private Document document;
    private Elements formattedElements;

    public static PostScraper scraper(SearchEntry searchEntry) throws IOException {
        validate(searchEntry);
        PostScraper scraper = new PostScraper();
        scraper.init(searchEntry);
        return scraper;
    }
    public static PostScraper scraper(Source source) throws IOException {
        Objects.requireNonNull(source);
        PostScraper scraper = new PostScraper();
        scraper.init(source);
        scraper.formattedElements = scraper.getAll();
        return scraper;
    }

    private static void validate(SearchEntry searchEntry) {
        Objects.requireNonNull(searchEntry, "null for source not allowed");
        Objects.requireNonNull(searchEntry.getKeyWords(), "null not allowed");
        Objects.requireNonNull(searchEntry.getSource(), "null not allowed");
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

    private static String abbreviate(String s) {
        StringBuilder builder = new StringBuilder();
        for (String s1 : s.split("[^a-zA-Z0-9öäü]")) {
            if (!s1.isEmpty()) {
                builder.append(s1.substring(0, 1).toUpperCase());
            }
        }
        return builder.toString();
    }

    public List<Post> getPosts(SearchEntry entry) throws IOException {
        validate(entry);
        Objects.requireNonNull(document);

        this.search = entry;
        Elements filtered = filterPosts(formattedElements);
        return new PostParser().toPosts(filtered, entry);
    }

    public List<Post> getPosts() {
        Objects.requireNonNull(document);
        Elements elements = getPostElements();
        return new PostParser().toPosts(elements, search);
    }

    private Elements getPostElements() {
        formattedElements = getAll();
        if (search.getKeyWords().isEmpty()) {
            return formattedElements;
        } else {
            return filterPosts(formattedElements);
        }
    }

    private void init(Source source) throws IOException {
        this.source = source;
        initScraper();
    }

    private Elements filterPosts(Elements posts) {
        if (search == null) {
            throw new IllegalStateException();
        }

        Elements elements = new Elements();
        for (Element post : posts) {
            if (inText(post)) {
                elements.add(post);
            } else {
                post.attributes().forEach(attribute -> searchAttribute(elements, post, attribute));
                post.childNodes().forEach(node -> node.attributes().forEach(attribute -> searchAttribute(elements, post, attribute)));
            }
        }
        return elements;
    }

    private boolean inText(Element post) {
        boolean contains = search.getKeyWords().stream().anyMatch(s -> post.text().contains(s));
        String abbreviation = abbreviate(search.getCreationKey().getTitle());
        boolean containsAbbr = false;

        if (!abbreviation.isEmpty() && abbreviation.length() > 1) {
            containsAbbr = post.text().contains(abbreviation);
        }

        return contains ||
                post.text().contains(search.getCreationKey().getTitle()) ||
                containsAbbr;
    }

    private void searchAttribute(Elements elements, Element post, Attribute attribute) {
        if (search.getKeyWords().stream().anyMatch(s -> attribute.getValue().contains(s))) {
            elements.add(post);
        }
    }


    private void init(SearchEntry entry) throws IOException {
        this.source = entry.getSource();
        initScraper();
        this.search = entry;
    }

    private void initScraper() throws IOException {
        if (source.getConfigs().isInit()) {
            this.postConfigs = source.getConfigs();
        } else {
            this.postConfigs = null;
        }
        document = getDocument(source.getUrl());
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
        PostConfigs configs = search.getSource().getConfigs();

        if (PostConfigsSetter.setPostConfigs(configs, document)) {
            return PostFormat.format(document, configs);
        } else {
            Log.classLogger(this).log(Level.WARNING, search.getSource().getUrl() + " is not supported");
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
        Post post = search.getSource().getNewestPost(search.getCreationKey());

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


    private Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }
}
