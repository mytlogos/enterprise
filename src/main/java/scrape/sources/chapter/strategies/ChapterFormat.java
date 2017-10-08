package scrape.sources.chapter.strategies;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.Formatter;
import scrape.sources.chapter.ChapterConfigs;

import java.util.Objects;

/**
 *
 */
public class ChapterFormat extends Formatter {
    private static final String TITLE_TAG = "h1";
    private static final String TITLE_ID = "enterprise-title";
    private static final String PAGINATION_CONTAINER = "a";
    private static final String PAGINATION_ID = "pagination";
    private static final String PAGINATION_PREV_ID = "prev";
    private static final String PAGINATION_NEXT_ID = "next";
    private static final String PAGINATION_LINK_ATTRIBUTE = "href";
    private static final String CONTENT_TAG = "div";
    private static final String CONTENT_ID = "enterprise-chapter-content";
    private static final String CONTAINER_TAG = "article";


    public String getTitle(Element element) {
        return stringByIdText(element, TITLE_ID);
    }

    public Element getTitleElement(String title) {
        Element element = new Element(TITLE_TAG);
        element.text(title);
        return element;
    }

    public Element getPaginationElement(Element prev, Element next) {
        Element element = new Element("div");
        element.attr("id", PAGINATION_ID);

        appendLinkElement(element, prev, PAGINATION_PREV_ID);
        appendLinkElement(element, next, PAGINATION_NEXT_ID);
        return element;
    }

    public String getPreviousLink(Element element) {
        return stringByIdAttr(element, PAGINATION_PREV_ID, PAGINATION_LINK_ATTRIBUTE);
    }

    public String getNextLink(Element element) {
        return stringByIdAttr(element, PAGINATION_NEXT_ID, PAGINATION_LINK_ATTRIBUTE);
    }

    public Element getContentElement(Element element) {
        // TODO: 06.10.2017 remove all unnecessary elements like pagination
        System.out.println(element);
        return element;
    }

    private void appendLinkElement(Element container, Element infoHolder, String id) {
        Element linkContainer = new Element(PAGINATION_CONTAINER);
        linkContainer.attr("id", id);

        if (infoHolder != null) {
            String link = infoHolder.absUrl("href");
            linkContainer.attr(PAGINATION_LINK_ATTRIBUTE, link);

        }

        container.appendChild(linkContainer);
    }

    public Element format(Document document, ChapterConfigs configs) {
        Element wrapper = configs.getWrapper().apply(document);
        return format(wrapper, configs);
    }

    public Element format(Element element, ChapterConfigs configs) {
        Objects.requireNonNull(element);
        Objects.requireNonNull(configs);

        Element post = new Element(CONTAINER_TAG);

        setPart(element.clone(), post, configs.getTitle());
        setPart(element.clone(), post, configs.getContent());
        // FIXME: 07.10.2017 somehow some elements disappeared (.entry-content), could not catch the change through printing or everything else, just disappears that ***
        // FIXME: 07.10.2017 cloning the elements is a temporary solution
        setPart(element.clone(), post, configs.getPagination());
        return post;
    }

}
