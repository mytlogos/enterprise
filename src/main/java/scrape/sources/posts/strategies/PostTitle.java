package scrape.sources.posts.strategies;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.posts.strategies.intface.TitleElement;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 */
public enum PostTitle implements TitleElement {
    ENTRY_TITLE("[class*=entry-title]", Type.INNER_LINK),
    POST_TITLE(".post-title", Type.INNER_LINK),
    TITLE(".title", Type.INNER_LINK),
    OUTER_ENTRY_TITLE("a .entry-title", Type.PARENT_LINK),
    POST_LINK("a:contains(permalink)", "h2", Type.PERMALINK),
    LINK_CONTAINS("h3 > a[href*=news], p > a[href*=post]", Type.INNER_LINK),
    QIDIAN("href", "book", "title", Type.TITLE_SEPARATED),
    LINK("a", Type.LINK_TEXT),
    RECENT_POSTS(".h-recent-posts", Type.PARENT_LINK),

    ENTRY_AND_LINK(PostTitle.ENTRY_TITLE, PostTitle.LINK) {
        @Override
        public Element apply(Element element) {
            return Arrays.stream(this.titles).map(postTitle -> postTitle.type.get(postTitle, element)).filter(Objects::nonNull).findFirst().orElse(null);
        }
    },
    ENTRY_AND_PARENT(PostTitle.ENTRY_TITLE, PostTitle.RECENT_POSTS) {
        @Override
        public Element apply(Element element) {
            return Arrays.stream(this.titles).map(postTitle -> postTitle.type.get(postTitle, element)).filter(Objects::nonNull).findFirst().orElse(null);
        }
    };


    Type type;
    String selectorOne;
    String selectorTwo;
    PostTitle[] titles;
    private String attribute;
    private String attributeValue;
    private String valueContainer;
    private String tag;


    PostTitle(String attribute, String value, String container, Type type) {
        this.attribute = attribute;
        this.attributeValue = value;
        this.valueContainer = container;
        this.type = type;
    }

    PostTitle(String tag, Type type) {
        this.tag = tag;
        this.type = type;
    }

    PostTitle(String s, String s1, Type permalink) {
        this.type = permalink;
        selectorOne = s;
        selectorTwo = s1;
    }


    PostTitle(PostTitle... titles) {
        this.titles = titles;
    }

    private static Element getElement(String title, String link) {
        return new PostFormat().titleElement(title, link);
    }

    @Override
    public Element apply(Element element) {
        return this.type.get(this, element);
    }

    enum Type {
        INNER_LINK {
            @Override
            Element get(PostTitle title, Element element) {
                Elements elements = element.select(title.tag);
                if (!elements.isEmpty()) {
                    for (Element selected : elements) {
                        Elements byTag = selected.getElementsByTag("a");

                        if (byTag.size() == 1) {
                            String link = byTag.get(0).attr("abs:href");
                            String titleString = byTag.get(0).text();

                            return getElement(titleString, link);
                        }
                    }
                } else {
                    if (element.attr("class").contains("sticky")) {
                        Elements entry_format = element.select("a.entry-format");

                        if (entry_format.size() == 1) {
                            String link = entry_format.get(0).attr("abs:href");
                            String titleString = entry_format.get(0).text();

                            return getElement(titleString, link);
                        }
                    }
                }
                return null;
            }
        },
        OUTER_LINK {
            @Override
            Element get(PostTitle title, Element element) {
                for (Element selected : element.select(title.tag)) {
                    Element parent = selected.parent();

                    Element newElement = getElement(parent.text(), parent.attr("abs:href"));
                    if (newElement != null) return newElement;
                }
                return null;
            }
        },
        TITLE_SEPARATED {
            @Override
            Element get(PostTitle title, Element element) {
                Elements elements = element.getElementsByAttributeValueContaining(title.attribute, title.attributeValue);

                if (elements.size() == 2) {

                    String novel = elements.get(0).attr(title.valueContainer);
                    String chapter = elements.get(1).attr(title.valueContainer);

                    String titleString = novel + " " + chapter;
                    String link = elements.get(1).attr("abs:href");

                    return getElement(titleString, link);
                }
                return null;
            }
        },
        LINK_TEXT {
            @Override
            Element get(PostTitle title, Element element) {
                Elements elements = element.select(title.tag);
                if (elements.size() == 1) {
                    Element copy = elements.get(0).clone();
                    copy.select("span, em").remove();

                    String titleString = copy.text();
                    String link = copy.attr("abs:href");

                    return getElement(titleString, link);
                }
                return null;
            }
        },
        PARENT_LINK {
            @Override
            Element get(PostTitle title, Element element) {
                Elements elements = element.select(title.tag);

                if (!elements.isEmpty()) {

                    Element node = elements.get(0);
                    Element parent = node.parent();
                    int maxDepth = 4;

                    for (int i = 0; i < maxDepth; i++) {
                        if (parent.tagName().equals("a")) {
                            String link = parent.attr("abs:href");
                            String titleString = node.text();

                            return getElement(titleString, link);
                        } else {
                            parent = parent.parent();
                        }
                    }
                }
                return null;
            }
        }, PERMALINK {
            @Override
            Element get(PostTitle title, Element element) {
                Elements linkElements = element.select(title.selectorOne);
                Elements titleElements = element.select(title.selectorTwo);

                if (linkElements.size() == titleElements.size() && linkElements.size() == 1) {

                    String link = linkElements.get(0).attr("abs:href");
                    String titleString = titleElements.get(0).text();

                    return PostTitle.getElement(titleString, link);
                } else {
                    return null;
                }
            }
        };

        abstract Element get(PostTitle title, Element element);
    }
}
