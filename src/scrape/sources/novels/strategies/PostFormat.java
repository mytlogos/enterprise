package scrape.sources.novels.strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.PostConfigs;
import scrape.sources.novels.ParseTime;
import scrape.sources.novels.strategies.intface.FilterElement;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 */
public class PostFormat {

    private static final String POST_TAG = "article";
    private static final String TITLE_TAG = "h1";
    private static final String TIME_TAG = "time";
    private static final String LINK_ATTRIBUTE = "href";
    private static final String LINK_SELECTOR = TITLE_TAG + ">a[" + LINK_ATTRIBUTE + "]";
    private static final String TIME_ATTRIBUTE = "datetime";
    private static final String TIME_SELECTOR = TIME_TAG + "[" + TIME_ATTRIBUTE + "]";

    public static String getTitle(Element element) {
        return getString(element, LINK_SELECTOR, Element::text);
    }

    public static String getLink(Element element) {
        return getString(element, LINK_SELECTOR, element1 -> element1.attr(LINK_ATTRIBUTE));
    }

    public static String getTime(Element element) {
        return getString(element, TIME_SELECTOR, element1 -> element1.attr(TIME_ATTRIBUTE));
    }

    private static String getString(Element element, String selector, Function<Element, String> function) {
        Elements elements = element.select(selector);
        if (elements.size() == 1) {
            return function.apply(elements.get(0));
        } else {
            return null;
        }
    }

    public static Element timeElement(String time) {
        if (time == null || time.isEmpty()) {
            return null;
        }

        Element timeElement = new Element(TIME_TAG);

        LocalDateTime dateTime = ParseTime.parseTime(time);
        timeElement.attr("datetime", dateTime.toString());
        return timeElement;
    }

    public static Element titleElement(String title, String link) {
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

    public static Elements format(Elements elements, PostConfigs configs) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(configs);

        Elements posts = new Elements();

        for (Element element : elements) {
            Element post = new Element(POST_TAG);

            setPost(element, post, configs.getTitle());
            setPost(element, post, configs.getTime());
            setPost(element, post, configs.getPostBody());
            setPost(element, post, configs.getFooter());
            posts.add(post);
        }

        return posts;
    }

    public static Elements format(Document document, PostConfigs configs) {
        Objects.requireNonNull(document);
        Objects.requireNonNull(configs);

        Element wrapper = configs.getWrapper().apply(document);
        Elements elements = configs.getPosts().apply(wrapper);
        return format(elements, configs);
    }

    private static void setPost(Element element, Element post, FilterElement filterElement) {
        if (filterElement != null) {
            Element content = filterElement.apply(element);
            post.appendChild(content);
        }
    }
}
