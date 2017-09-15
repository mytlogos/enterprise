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
import scrape.sources.novels.strategies.Archive;
import scrape.sources.novels.strategies.PostConfigsSetter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;

/**
 *
 */
public class PostScraper {
    private Source source;
    private PostConfigs postConfigs;
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

    private static Document cleanDoc(Document document) {
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

    /*public List<Post> getPosts() {
        Objects.requireNonNull(source);
        Objects.requireNonNull(document);
        Elements elements = getPostElements(null);
        return new PostParser().toPosts(elements);
    }*/
    public Elements getPosts() {
        Objects.requireNonNull(source);
        Objects.requireNonNull(document);
        return getPostElements(null);
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
        checkFeed();
        Post post = source.getNewestPost(null);

        if (post != null) {
            localDateTime = post.getTimeStamp();
        }

        Elements elements;
        if (postConfigs == null) {
            elements = search(Archive.hasArchive(document));
        } else {
            elements = search(postConfigs.isArchive());
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

    private Elements search(boolean isArchive) {
        Elements elements;
        if (isArchive) {
            elements = searchArchives();
        } else {
            // TODO: 14.09.2017 do it with Pages
            elements = postsFromPage(document);
            while (hasNextPage(document)) {
                Elements secondaryElements = fromNextPage(document);
                elements.addAll(secondaryElements);

                if (checkTime(secondaryElements)) break;
            }
        }
        return elements;
    }

    private Elements postsFromPage(Document document) {
        Document cleaned = cleanDoc(document);
        if (postConfigs == null) {
            return initConfigs(cleaned);
        } else {
            return getFormattedElements(cleaned, postConfigs);
        }
    }

    private Elements getFormattedElements(Document cleaned, PostConfigs configs) {
        Elements result = new Elements();

        Element bodyWithPosts = configs.getBody().apply(cleaned);
        Elements postElements = configs.getPosts().apply(bodyWithPosts);

        for (Element postElement : postElements) {
            Element article = new Element("article");

            setElement(configs.getTitle(), postElement, article);
            setElement(configs.getTime(), postElement, article);
            setElement(configs.getPostBody(), postElement, article);
            setElement(configs.getFooter(), postElement, article);

            result.add(article);
        }
        return result;
    }

    private Elements initConfigs(Document document) {
        PostConfigs configs = source.getConfigs();

        if (PostConfigsSetter.setPostConfigs(configs, document)) {
            return getFormattedElements(document, configs);
        } else {
            Log.classLogger(this).log(Level.WARNING, source.getUrl() + " is not supported");
            return null;
        }
    }

    private boolean setElement(Function<Element, Element> function, Element post, Element article) {
        if (function != null) {
            Element content = function.apply(post);
            article.appendChild(content);
            return true;
        }
        return false;
    }

    private Elements searchArchives() {
        /*if (postConfigs == null) {
            Archive.SiteArchive archive = Archive.SiteArchive.archiveSearcher(document);

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
            return postConfigs.getArchive().get(document);
        }*/

        return null;
    }

    private boolean hasNextPage(Document document) {
        return true;
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
