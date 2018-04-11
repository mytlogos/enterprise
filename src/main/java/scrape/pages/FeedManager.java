package scrape.pages;


import enterprise.data.Default;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Element;
import scrape.sources.Source;
import scrape.sources.SourceAccessor;
import scrape.sources.posts.ParseTime;
import scrape.sources.posts.Post;
import scrape.sources.posts.PostManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class FeedManager {
    private static List<String> feedAppends = new ArrayList<>();

    static {
        feedAppends.add("feed");
        feedAppends.add("feeds/posts/default");
        feedAppends.add("rss");
        feedAppends.add("rss/chapters/all");
        feedAppends.add("rss.php");
    }

    private final Source source;

    public FeedManager(Source source) {
        this.source = source;
    }

    public List<Post> getNewPosts() throws JDOMException, IOException {

        final PageConfig pageConfig = source.getConfig(new PageConfig());
        String feed = pageConfig.getFeed();

        if (feed == null) {
            if (!pageConfig.isInit()) {
                throw new IllegalStateException("configs for " + source + " not initialized!");
            }
            return null;
        }

        FeedParser parser = getParser(feed);

        Post newestPost = PostManager.getInstance().getNewestPost(source);

        if (newestPost == null || parser == null) {
            return null;
        }

        if (parser.hasNoPosts()) {
            return new ArrayList<>();
        }

        Post last = parser.parseLastPost();

        if (last.isBefore(newestPost)) {
            Post first = parser.parseFirstPost();

            if (first.isAfter(newestPost)) {
                return getPosts().stream().filter(newestPost::isBefore).collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        } else {
            return null;
        }
    }

    private FeedParser getParser(String url) throws JDOMException, IOException {
        if (url == null) {
            return null;
        }

        Pattern comment = Pattern.compile("comment", Pattern.CASE_INSENSITIVE);

        if (comment.matcher(url).find()) {
            return null;
        }

        final Document document;
        try {
            document = SourceAccessor.getXml(url);
        } catch (IOException e) {
            if (e instanceof HttpStatusException) {
                HttpStatusException statusEx = (HttpStatusException) e;

                int statusCode = statusEx.getStatusCode();

                if (statusCode > 400 && statusCode < 511) {
                    return null;
                } else {
                    throw new HttpStatusException(statusEx.getMessage(), statusCode, statusEx.getUrl());
                }
            } else {
                throw new IOException(e);
            }
        }

        if (document == null) {
            return null;
        }

        org.jdom2.Element rootElement = document.getRootElement();

        FeedParser parser;

        if (rootElement.getName().equalsIgnoreCase("feed")) {
            parser = new AtomParser(source);
        } else if (rootElement.getName().equalsIgnoreCase("rss")) {
            parser = new RssParser(source);
        } else {
            throw new IllegalArgumentException("unknown root element" + rootElement);
        }

        parser.init(rootElement);
        String title = parser.parseTitle();

        if (comment.matcher(title).find()) {
            return null;
        }
        return parser;
    }

    public List<Post> getPosts() throws JDOMException, IOException {
        FeedParser parser = getParser(source.getConfig(new PageConfig()).getFeed());
        return parser == null ? null : parser.parsePosts();
    }

    public String checkFeed() {
        String url = source.getUrl();
        try {
            final org.jsoup.nodes.Document document = SourceAccessor.
                    getDocument(url);

            return checkFeed(document);
        } catch (IOException | JDOMException e) {
            Default.LOGGER.log(Level.WARNING, "exception occurred while getting rss feed", e);
        }
        return null;
    }

    public String checkFeed(org.jsoup.nodes.Element root) throws JDOMException, IOException {
        String url = source.getUrl();

        Set<String> links = root.
                select("[type~=rss|atom],[href~=feed|rss], [href]:contains(rss), [href]:contains(atom), [href:contains(feed)]").
                stream().
                filter(this::isValidLink).
                map(element -> element.absUrl("href")).
                collect(Collectors.toSet());

        String feedLink = null;

        if (links.isEmpty()) {
            for (String append : feedAppends) {
                if (!url.endsWith("/")) {
                    url += "/";
                }
                url += append;

                feedLink = getFeedLink(feedLink, url);

            }
        } else {
            for (String link : links) {
                feedLink = getFeedLink(feedLink, link);
            }
        }

        return feedLink;
    }

    private String getFeedLink(String feedLink, String link) throws JDOMException, IOException {
        if (getParser(link) != null) {
            if (feedLink == null) {
                feedLink = link;
                //shortest link is in most times the link we want
            } else if (feedLink.length() > link.length()) {
                feedLink = link;
            }
        }
        return feedLink;
    }

    private boolean isValidLink(Element element) {
        String href = element.absUrl("href");

        if (href.contains("comment")) {
            return false;
        }

        try {
            URI linkUri = new URI(href);
            URI baseUri = new URI(source.getUrl());

            String baseUriHost = baseUri.getHost();
            String linkUriHost = linkUri.getHost();

            Matcher matcher = Pattern.compile("(.+\\.)?(.+)\\..+/?").matcher(linkUriHost);

            String linkHostName;

            if (matcher.find()) {
                linkHostName = matcher.group(2);
            } else {
                throw new IllegalArgumentException("not valid host");
            }
            return baseUriHost.contains(linkHostName);
        } catch (URISyntaxException e) {
            return false;
        }
    }


    private static abstract class FeedParser {
        Source source;
        org.jdom2.Element parseRoot;
        List<org.jdom2.Element> entries;

        FeedParser(Source source) {
            this.source = source;
        }

        boolean hasNoPosts() {
            return entries.isEmpty();
        }

        abstract void init(org.jdom2.Element root);

        String parseTitle() {
            String title = parseTxt(this.parseRoot.getChild(getFeedTitle(), parseRoot.getNamespace()));
            String subTitle = parseTxt(this.parseRoot.getChild(getFeedSubTitle(), parseRoot.getNamespace()));

            return title + " " + (subTitle == null ? "" : subTitle);
        }

        abstract String parseTxt(org.jdom2.Element element);

        abstract String getFeedTitle();

        abstract String getFeedSubTitle();

        private List<Post> parsePosts() {
            List<Post> posts = new ArrayList<>();

            for (org.jdom2.Element entry : entries) {
                Post post = parsePost(entry);
                posts.add(post);
            }
            return posts;
        }

        private Post parsePost(org.jdom2.Element element) {
            String title = parseTxt(element.getChild(getEntryTitle(), element.getNamespace()));
            org.jdom2.Element linkElement = element.getChild(getEntryLink(), element.getNamespace());

            String link = parseTxt(linkElement);
            String updated = parseTxt(element.getChild(getEntryTime(), element.getNamespace()));

            if (link.isEmpty()) {
                List<org.jdom2.Element> links = element.getChildren(getEntryLink(), element.getNamespace());

                for (org.jdom2.Element entryLinkElement : links) {
                    Attribute rel = entryLinkElement.getAttribute("rel");

                    if (rel != null && rel.getValue().equalsIgnoreCase("alternate")) {
                        link = entryLinkElement.getAttribute("href").getValue();
                        break;
                    }
                }
            }

            LocalDateTime published = ParseTime.parseTime(updated);
            return new Post(source, title, published, link, false);
        }

        abstract String getEntryTitle();

        abstract String getEntryLink();

        abstract String getEntryTime();

        private Post parseFirstPost() {
            return parsePost(entries.get(0));
        }

        private Post parseLastPost() {
            return parsePost(entries.get(entries.size() - 1));
        }
    }

    private static class RssParser extends FeedParser {

        private RssParser(Source source) {
            super(source);
        }

        @Override
        public void init(org.jdom2.Element root) {
            if (root == null) {
                throw new IllegalArgumentException("root is null!");
            }
            if (!root.getName().equalsIgnoreCase("rss")) {
                throw new IllegalArgumentException("not rss root");
            }
            this.parseRoot = root.getChild("channel");
            this.entries = parseRoot.getChildren("item");
        }

        @Override
        String parseTxt(org.jdom2.Element element) {
            return element == null ? null : element.getTextNormalize();
        }

        @Override
        String getFeedTitle() {
            return "title";
        }

        @Override
        String getFeedSubTitle() {
            return "description";
        }

        @Override
        String getEntryTitle() {
            return "title";
        }

        @Override
        String getEntryLink() {
            return "link";
        }

        @Override
        String getEntryTime() {
            return "pubDate";
        }

    }

    private static class AtomParser extends FeedParser {
        private Pattern cdataPattern = Pattern.compile("<!\\[CDATA\\[(.+)]]>");

        private AtomParser(Source source) {
            super(source);
        }

        @Override
        void init(org.jdom2.Element root) {
            if (root == null) {
                throw new IllegalArgumentException("root is null!");
            }
            if (!root.getName().equalsIgnoreCase("feed")) {
                throw new IllegalArgumentException("not rss root");
            }
            this.parseRoot = root;
            this.entries = parseRoot.getChildren("entry", parseRoot.getNamespace());
        }

        String parseTxt(org.jdom2.Element element) {
            if (element == null) {
                return null;
            }

            String input = element.getTextNormalize();
            Matcher matcher = cdataPattern.matcher(input);

            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return input;
            }
        }

        @Override
        String getFeedTitle() {
            return "title";
        }

        @Override
        String getFeedSubTitle() {
            return "subtitle";
        }

        @Override
        String getEntryTitle() {
            return "title";
        }

        @Override
        String getEntryLink() {
            return "link";
        }

        @Override
        String getEntryTime() {
            return "updated";
        }

    }
}
