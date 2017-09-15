package scrape.sources.novels.strategies.intface.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.novels.strategies.intface.ElementFilter;
import scrape.sources.novels.strategies.intface.TitleElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class TitlesFilter implements ElementFilter<TitleElement> {
    @Override
    public Collection<TitleElement> getFilter() {
        List<TitleElement> filters = new ArrayList<>();
        Collections.addAll(filters, Titles.values());
        Collections.addAll(filters, Titles.Mixture.values());
        return filters;
    }

    /**
     *
     */
    public enum Titles implements TitleElement {
        ENTRY_TITLE("[class*=entry-title]", Type.INNER_LINK) {
        },
        POST_TITLE(".post-title", Type.INNER_LINK) {
        },
        TITLE(".title", Type.INNER_LINK) {
        },
        OUTER_ENTRY_TITLE("a > h1.entry-title", Type.OUTER_LINK) {
        },
        POST_LINK("a:contains(permalink)", "h2", Type.PERMALINK) {
        },
        LINK_CONTAINS("h3 > a[href*=news], p > a[href*=post]", Type.INNER_LINK) {
        },
        QIDIAN("href", "book", "title", Type.TITLE_SEPARATED) {
        },
        LINK("a", Type.LINK_TEXT) {
        },
        PARENT_LINK(".h-recent-posts", Type.PARENT_LINK) {
        };


        private final Type type;
        String selectorOne;
        String selectorTwo;
        private String attribute;
        private String attributeValue;
        private String valueContainer;
        private String tag;


        Titles(String attribute, String value, String container, Type type) {
            this.attribute = attribute;
            this.attributeValue = value;
            this.valueContainer = container;
            this.type = type;
        }

        Titles(String tag, Type type) {
            this.tag = tag;
            this.type = type;
        }

        Titles(String s, String s1, Type permalink) {
            this.type = permalink;
            selectorOne = s;
            selectorTwo = s1;
        }

        private static Element getElement(String title, String link) {
            if (!link.isEmpty() && !title.isEmpty()) {

                Element newElement = new Element("a");
                newElement.attr("href", link);
                newElement.text(title);
                return newElement;
            }
            return null;
        }

        @Override
        public Element apply(Element element) {
            return this.type.get(this, element);
        }

        public enum Mixture implements TitleElement {
            ENTRY_AND_LINK(ENTRY_TITLE, LINK) {
            },
            ENTRY_AND_PARENT(ENTRY_TITLE, PARENT_LINK);

            Titles[] mixture = new Titles[0];

            Mixture(Titles... titles) {
                mixture = titles;
            }

            @Override
            public Element apply(Element element) {
                for (Titles titles : mixture) {
                    Element titleLink = titles.type.get(titles, element);

                    if (titleLink != null) {
                        return titleLink;
                    }
                }
                return null;
            }
        }

        /*public static TitleElement tryAll(Elements elements) {
            if (!elements.isEmpty()) {
                Collection<TitleElement> filters = getFilter();
                Element first = elements.get(0);
                return tryFilter(elements, first, filters);
            } else {
                return null;
            }
        }

        private static TitleElement tryFilter(Elements elements, Element first, Collection<TitleElement> filters) {
            TitleElement filter = getTitleFilter(first, filters);

            if (filter != null) {
                for (Element element : elements) {
                    Element titleLink = filter.apply(element);
    //                System.out.println("MODE " + filter + " RESULT " + titleLink);
                    if (titleLink == null) {

                        filters.remove(filter);
                        return tryFilter(elements, first, filters);
                    }
                }
            }
            return filter;
        }

        private static TitleElement getTitleFilter(Element element, Collection<TitleElement> filters) {
            //most other loops throw either index or concurrentModification exception
            Iterator<TitleElement> iterator = filters.iterator();
            while (iterator.hasNext()) {
                TitleElement filter = iterator.next();
                Element titleLink = filter.apply(element);

                if (titleLink != null) {
                    return filter;
                } else {
                    iterator.remove();
                }
            }
            return null;
        }*/

        private enum Type {
            INNER_LINK {
                @Override
                Element get(Titles title, Element element) {
                    Elements elements = element.select(title.tag);
                    if (!elements.isEmpty()) {
                        for (Element selected : elements) {
                            Elements byTag = selected.getElementsByTag("a");

                            if (byTag.size() == 1) {
                                String link = byTag.get(0).attr("abs:href");
                                String titleString = element.text();

                                return getElement(titleString, link);
                            }
                        }
                    } else {
                        if (element.attr("class").contains("sticky")) {
                            Elements entry_format = element.select("a.entry-format");

                            if (entry_format.size() == 1) {
                                String link = entry_format.get(0).attr("abs:href");
                                String titleString = element.text();

                                return getElement(titleString, link);
                            }
                        }
                    }
                    return null;
                }
            },
            OUTER_LINK {
                @Override
                Element get(Titles title, Element element) {
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
                Element get(Titles title, Element element) {
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
                Element get(Titles title, Element element) {
                    Elements elements = element.select(title.tag);
                    if (elements.size() == 1) {
                        elements.select("span, em").remove();
                        Element node = elements.get(0);

                        String titleString = node.text();
                        String link = node.attr("abs:href");

                        return getElement(titleString, link);
                    }
                    return null;
                }
            },
            PARENT_LINK {
                @Override
                Element get(Titles title, Element element) {
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
                Element get(Titles title, Element element) {
                    Elements linkElements = element.select(title.selectorOne);
                    Elements titleElements = element.select(title.selectorTwo);

                    if (linkElements.size() == titleElements.size() && linkElements.size() == 1) {

                        String link = linkElements.get(0).attr("abs:href");
                        String titleString = titleElements.get(0).text();

                        return Titles.getElement(titleString, link);
                    } else {
                        return null;
                    }
                }
            };

            abstract Element get(Titles title, Element element);
        }
    }
}
