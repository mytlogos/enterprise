package scrape.sources.novels;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.Post;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for getting Posts from
 * the internet which are related to novels.
 */
public class NovelPosts {
    private URI baseUri;
    private Document doc;

    /**
     * The constructor of {@code NovelPosts}.
     */
    public NovelPosts() {
    }

    /**
     * Gets the html document of the specified
     * {@code uri}, gets the all Elements
     * that resemble Posts and parses the content
     * to {@code Post}s afterwards.
     *
     * @return posts - list of parsed posts
     * @throws IOException if an error occurred
     * @throws URISyntaxException if the {@code uri} is invalid
     */
    public List<Post> getPosts(String uri) throws IOException, URISyntaxException {
        init(uri);
        Elements postElements = getPostElements();
        return new PostParser().toPosts(postElements);
    }

    /**
     * Gets the html document of the
     * specified {@code uri}.
     *
     * @param uri uri to load from
     * @throws IOException if an error occurred
     * @throws URISyntaxException if the {@code uri} is invalid
     */
    public void loadDoc(String uri) throws IOException, URISyntaxException {
        init(uri);
    }

    /**
     * Gets the all Elements of the saved document,
     * that resemble Posts and parses the content
     * to {@code Post}s afterwards.
     * <p>
     * Before this document is called, it is imperative
     * to call {@link #loadDoc(String)} before , to load the html
     * document, else it will throw a {@code NullPointerException}.
     * </p>
     *
     * @return posts - list of parsed posts
     * @throws IOException if an error occurred
     */
    public List<Post> getSpecificPosts(String match) throws IOException{
        if (doc == null) {
            throw new NullPointerException("no document available");
        }
        Elements postElements = getPostElements();
        postElements = searchPosts(match, postElements);
        return new PostParser().toPosts(postElements);
    }

    /**
     * Gets the {@code baseUri} of the provided
     * {@code uri} and fetches the
     * html document of the {@code startUri}.
     *
     * @param uri uri to fetch the html document from
     * @throws URISyntaxException if uri is invalid
     * @throws IOException if an error occurred
     */
    private void init(String  uri) throws URISyntaxException, IOException {
        URI startUri = new URI(uri);
        baseUri = new URI( startUri.getScheme()+"://" + startUri.getHost());
        doc = getDocument(startUri);
    }

    /**
     * Connects to the site specified by the {@code uri}
     * and gets the html document.
     *
     * @param uri uri to fetch the document from
     * @return the parsed html document
     * @throws IOException if an error occurred
     */
    private Document getDocument(URI uri) throws IOException {
        return Jsoup.connect(uri.toString()).get();
    }

    /**
     * Gets all elements of the saved document,
     * which resembles a 'Post'.
     * It searches for 'article' tags or 'post hentry'
     * or 'chapter row' classes (tags and classes from html).
     *
     * @return elements - all elements resembling posts
     * @throws IOException if an error occurred
     */
    private Elements getPostElements() throws IOException {
        Elements postElements = doc.getElementsByTag("article");
        if (postElements.size() == 0) {
                postElements = doc.getElementsByClass("post hentry");
        }
        if (postElements.size() == 0) {
            Elements elements = doc.getElementsByClass("chapter row");
            postElements = getPosts(elements);
        }
        return postElements;
    }

    /**
     * Searches in the {@code elements} for text and urls
     * containing the {@code match}.
     *
     * @param match match to filter after
     * @param elements elements to filer
     * @return selectedPosts - only elements containing the {@code match}
     */
    private Elements searchPosts(String match, Elements elements) {
        Elements selectedPosts = new Elements();

        for (Element element : elements) {

            if (element.text().contains(match)) {
                selectedPosts.add(element);
            } else {
                selectedPosts.addAll(element.getElementsByAttributeValueContaining("href", match));
            }
        }
        return selectedPosts;
    }

    /**
     * // TODO: 25.08.2017 do the doc
     * //is this tailored only for gravity tales?
     * @param elements
     * @return
     * @throws IOException
     */
    private Elements getPosts(Elements elements) throws IOException {
        Elements postElements;
        List<Element> link = new ArrayList<>();
        List<String> completeLinks = new ArrayList<>();

        for (Element element : elements) {
            link.addAll(element.getElementsByAttributeValueContaining("href", "post"));
        }

        for (Element element : link) {
            completeLinks.add(baseUri.toString() + element.attr("abs:href"));
        }

        postElements = getPostContent(completeLinks);
        return postElements;
    }

    /**
     * // TODO: 25.08.2017 do the doc
     * //is this tailored only for gravity tales?
     * @param links
     * @return
     * @throws IOException
     */
    private Elements getPostContent(List<String> links) throws IOException {
        Elements elements = new Elements();
        for (String link : links) {
            Document document = Jsoup.connect(link).get();
            elements.addAll(document.getElementsByTag("article"));
        }
        return elements;
    }

    //deprecated maybe
    private Matcher getMatcher(String expression, String toMatch) {
        List<String> strings = new ArrayList<>();
        strings.addAll(Arrays.asList(expression.split("[\\s,-]")));

        StringBuilder builder = new StringBuilder("(?i)");

        if (strings.size() == 1) {
            builder.append(strings.get(0));
        } else {
            for (String string : strings) {
                builder.append(getPattern(string));
            }
        }

        System.out.println(builder);
        Pattern pattern = Pattern.compile(builder.toString());
        return pattern.matcher(toMatch);
    }

    //deprecated maybe
    private String getPattern(String word) {
        StringBuilder string = new StringBuilder("");
        string.append("[\\s-]?");

        if (word.chars().allMatch(Character::isDigit)) {
            string.append("(").append(word).append(")?");
        } else {
            string.append(word.substring(0, 1));
            string.append("(").append(word.substring(1)).append(")?");
        }

        return string.toString();
    }
}
