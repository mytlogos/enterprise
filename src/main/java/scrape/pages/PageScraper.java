package scrape.pages;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.ElementHierarchy;
import scrape.sources.Source;
import scrape.sources.SourceAccessor;

import java.io.IOException;
import java.util.*;

/**
 *
 */
public class PageScraper {

    private final Document document;
    private final Source source;

    public PageScraper(Source source) throws IOException {
        document = SourceAccessor.getDocument(source.getUrl());
        this.source = source;
    }

    public PageScraper(Document document, Source source) {
        this.document = document;
        this.source = source;
    }

    public static String cssSelector(Element element) {
        if (element == null) {
            return "";
        }

        if (element.id().length() > 0)
            return "#" + element.id();

        // Translate HTML namespace ns:tag to CSS namespace syntax ns|tag
        String tagName = element.tagName().replace(':', '|');
        StringBuilder selector = new StringBuilder(tagName);

        String classes = join(element.classNames());
        if (classes.length() > 0)
            selector.append('.').append(classes);

        if (element.parent() == null || element.parent() instanceof Document) // don't add Document to selector, as will always have a html node
            return selector.toString();

        selector.insert(0, " > ");
        if (element.parent().select(selector.toString()).size() > 1)
            selector.append(String.format(
                    ":nth-child(%d)", element.elementSiblingIndex() + 1));

        return cssSelector(element.parent()) + selector.toString();
    }

    private static String join(Collection<String> strings) {

        Iterator<String> iterator = strings.iterator();
        if (!iterator.hasNext())
            return "";

        String start = iterator.next();
        start = start.contains("(") ? "" : start;

        if (!iterator.hasNext()) { // only one, avoid builder
            return start;
        }

        StringBuilder sb = new StringBuilder(64).append(start);
        while (iterator.hasNext()) {
            String next = iterator.next();

            if (!next.contains("(")) {
                sb.append(".");
                sb.append(next);
            }
        }
        return sb.toString();
    }

    public Source getSource() {
        return source;
    }

    public void init() {
        PageConfig configs = source.getConfig(new PageConfig());

        String feed = new FeedManager(source).checkFeed();

        String navigation = new Navigation().tryAll(document);
        String content = new Content().tryAll(document);
        String sideBarRight = new SideBar().tryAll(document, content, true);
        String sideBarLeft = new SideBar().tryAll(document, content, false);

        configs.setContent(content);
        configs.setSideBarRight(sideBarRight);
        configs.setSideBarLeft(sideBarLeft);
        configs.setFeed(feed);
        configs.setNavigation(navigation);
        configs.setInit();
    }

    public Element getSideBarLeft() {
        if (!getConfigs().isInit()) {
            return null;
        }
        String sideBar = getConfigs().getSideBarRight();
        return select(sideBar);
    }

    public PageConfig getConfigs() {
        return source.getConfig(new PageConfig());
    }

    private Element select(String selector) {
        return selector == null || selector.isEmpty() ? null : document.select(selector).get(0);
    }

    public Element getSideBarRight() {
        if (!getConfigs().isInit()) {
            return null;
        }
        String sideBar = getConfigs().getSideBarRight();
        return select(sideBar);

    }

    public Element getContent() {
        if (!getConfigs().isInit()) {
            return null;
        }
        String content = getConfigs().getContent();
        return select(content);
    }

    public Element getNavigation() {
        if (!getConfigs().isInit()) {
            return null;
        }
        String navigation = getConfigs().getNavigation();
        return select(navigation);
    }


    @Override
    public String toString() {
        return "PageScraper{" +
                "source=" + source.getUrl() +
                ", PageConfig=" + source.getConfig(new PageConfig()) +
                '}';
    }

    public static class Content {
        public static final List<String> contentSelector = Arrays.asList("main", "#main", "#content", "[class*=content]", "[class*=main]");
        private static final List<String> structureBlockTags = Arrays.asList(
                "title", "section", "nav", "aside", "hgroup", "header", "footer",
                "pre", "div", "address", "figure",
                "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th",
                "td", "details", "plaintext", "template", "article", "main");

        String tryAll(Document document) {

            List<Element> elements = new ArrayList<>();
            for (String selector : contentSelector) {
                Elements select = document.select(selector);

                Element element = likelyMain(select);

                if (element != null) {
                    elements.add(element);
                }
            }
            if (elements.isEmpty()) {
                Elements children = document.body().children();
            }
            return highestTextDensity(elements);
        }

        private Element likelyMain(Elements elements) {
            return elements.first();
        }

        private String highestTextDensity(List<Element> elements) {
            Element result = null;
            double maxDens = 0.0;

            for (Element element : elements) {
                double txtLength = element.text().length();
                double density = txtLength / element.getAllElements().stream().filter(this::isStructureBlock).count();

                if (maxDens < density) {
                    result = element;
                    maxDens = density;
                }
            }
            return result == null ? null : cssSelector(result);
        }

        private boolean isStructureBlock(Element element) {
            return structureBlockTags.contains(element.tagName());
        }
    }

    private static class SideBar {
        private static final List<String> sideBarSelector = Arrays.asList("aside", "#secondary", ".secondary", ".widget-area", "[class*=sidebar]");

        String tryAll(Document document, String main, boolean after) {
            Element mainElement = main == null ? null : document.select(main).first();

            List<Element> elements = new ArrayList<>();

            for (String selector : sideBarSelector) {
                Elements select = document.select(selector);
                Element element = likelySideBar(select, mainElement, after);

                if (element != null) {
                    elements.add(element);
                }
            }
            return elements.isEmpty() ? null : cssSelector(elements.get(0));
        }

        private Element likelySideBar(Elements select, Element main, boolean after) {
            Element sideBar = main;

            for (Element element : select) {
                if (sideBar == null) {
                    sideBar = element;
                } else {
                    if (after) {
                        if (ElementHierarchy.isAfter(sideBar, element)) {
                            sideBar = element;
                        }
                    } else if (ElementHierarchy.isBefore(sideBar, element)) {
                        sideBar = element;
                    }
                }
            }
            return sideBar;
        }
    }


    /**
     *
     */
    public static class Navigation {
        public static final List<String> possibleSelector = Arrays.asList(
                "#navbar", "nav", ".navbar-nav", "#masthead", ".g_header", ".nav", ".navigation", ".nav-menu");
        private static final List<String> structureBlockTagsWithLists = Arrays.asList(
                "body", "link", "title", "section", "nav", "aside", "hgroup", "header", "footer",
                "ul", "ol", "pre", "div", "address", "figure", "dl", "dt", "dd", "li", "p",
                "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th",
                "td", "details", "plaintext", "template", "article", "main");

        String tryAll(Document document) {

            List<Element> elementsMap = new ArrayList<>();

            for (String selector : possibleSelector) {
                Elements elements = selector.isEmpty() ? new Elements() : document.select(selector);

                if (!elements.isEmpty()) {
                    Element element = likelyNavigation(elements);

                    if (element != null) {
                        elementsMap.add(element);
                    }
                }
            }

            return highestDensityNavigation(elementsMap);
        }

        private Element likelyNavigation(Elements elements) {
            if (elements.size() == 1) {
                return elements.get(0);
            }
            Element navigation = null;

            for (Element element : elements) {
                if (isNavigation(element)) {

                    if (navigation == null) {
                        navigation = element;
                    } else if (ElementHierarchy.isBefore(navigation, element)) {
                        navigation = element;
                    }
                }
            }
            return navigation;
        }

        String highestDensityNavigation(Collection<Element> elements) {
            Element navigation = getElement(elements);
            return navigation == null ? null : cssSelector(navigation);
        }

        private boolean isNavigation(Element element) {
            return !element.getElementsByAttributeValueContaining("class", "menu").isEmpty();
        }

        public Element getElement(Collection<Element> elements) {
            double highestDensity = 0.0;

            Element navigation = null;

            for (Element entry : elements) {
                int linksCount = entry.select("[href]").size();
                long count = entry.getAllElements().stream().filter(this::isLinkBlock).count();

                double density = ((double) linksCount) / ((double) count);

                if (density > highestDensity) {
                    highestDensity = density;
                    navigation = entry;
                }
            }
            return navigation;
        }

        private boolean isLinkBlock(Element element) {
            return structureBlockTagsWithLists.contains(element.tagName());
        }

    }
}
