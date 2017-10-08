package scrape.sources.posts.strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.Formatter;
import scrape.sources.ElementsSieve;
import scrape.sources.posts.ParseTime;
import scrape.sources.posts.PostConfigs;
import scrape.sources.posts.PostSearchEntry;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 */
public class PostFormat extends Formatter {

    private static final String POST_TAG = "article";
    private static final String TITLE_TAG = "h1";
    private static final String TIME_TAG = "time";
    private static final String LINK_ATTRIBUTE = "href";
    private static final String LINK_SELECTOR = TITLE_TAG + ">a[" + LINK_ATTRIBUTE + "]";
    private static final String TIME_ATTRIBUTE = "datetime";
    private static final String TIME_SELECTOR = TIME_TAG + "[" + TIME_ATTRIBUTE + "]";

    public String getTitle(Element element) {
        return getString(element, LINK_SELECTOR, Element::text);
    }

    public String getLink(Element element) {
        return getString(element, LINK_SELECTOR, element1 -> element1.attr(LINK_ATTRIBUTE));
    }

    public String getTime(Element element) {
        return getString(element, TIME_SELECTOR, element1 -> element1.attr(TIME_ATTRIBUTE));
    }

    private String getString(Element element, String selector, Function<Element, String> function) {
        Elements elements = element.select(selector);
        if (elements.size() == 1) {
            return function.apply(elements.get(0));
        } else {
            return null;
        }
    }

    public Element timeElement(String time) {
        if (time == null || time.isEmpty()) {
            return null;
        }

        Element timeElement = new Element(TIME_TAG);

        LocalDateTime dateTime = ParseTime.parseTime(time);
        timeElement.attr("datetime", dateTime.toString());
        return timeElement;
    }

    public Element titleElement(String title, String link) {
        if (!link.isEmpty() && !title.isEmpty()) {

            Element titleElement = new Element(TITLE_TAG);
            Element titleLink = new Element("a");

            titleLink.attr(LINK_ATTRIBUTE, link);
            titleLink.text(title);

            titleElement.appendChild(titleLink);
            return titleElement;
        }
        return null;
    }

    public Elements format(Elements elements, PostConfigs configs) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(configs);

        Elements posts = new Elements();

        for (Element element : elements) {
            Element post = new Element(POST_TAG);

            if (element.hasClass("sticky")) {
                post.addClass("sticky");
            }

            setPart(element, post, configs.getTitle());
            setPart(element, post, configs.getTime());
            setPart(element, post, configs.getPostContent());
            setPart(element, post, configs.getFooter());
            posts.add(post);
        }

        return posts;
    }

    public Elements format(Document document, PostConfigs configs) {
        Elements elements = unFormatted(document, configs);
        return format(elements, configs);
    }

    public Elements unFormatted(Document document, PostConfigs configs) {
        Objects.requireNonNull(document);
        Objects.requireNonNull(configs);

        Element wrapper = configs.getWrapper().apply(document);
        return configs.getPosts().apply(wrapper);
    }

    public Elements format(Document document, PostConfigs configs, PostSearchEntry entry) {
        Objects.requireNonNull(document);
        Objects.requireNonNull(configs);

        Element wrapper = configs.getWrapper().apply(document);
        Elements elements = configs.getPosts().apply(wrapper);

        elements = new ElementsSieve(entry).filterPosts(elements);
        return format(elements, configs);
    }
}
