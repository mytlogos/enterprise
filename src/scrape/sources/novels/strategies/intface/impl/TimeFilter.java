package scrape.sources.novels.strategies.intface.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.novels.ParseTime;
import scrape.sources.novels.PostScraper;
import scrape.sources.novels.strategies.PostFormat;
import scrape.sources.novels.strategies.intface.ElementFilter;
import scrape.sources.novels.strategies.intface.TimeElement;
import scrape.sources.novels.strategies.intface.TitleElement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class TimeFilter implements ElementFilter<TimeElement> {
    @Override
    public Collection<TimeElement> getFilter() {
        return new ArrayList<>(Arrays.asList(Time.values()));
    }


    private static Element getElement(String timeString) {
        if (ParseTime.isParseAble(timeString)) {
            return PostFormat.timeElement(timeString);
        }
        return null;
    }

    /**
     *
     */
    public enum Time implements TimeElement {
        DATETIME("time", "datetime", Type.ATTRIBUTE_ONLY) {
        },
        LINK_TITLE("a", "title", Type.ATTRIBUTE) {
        },
        LINK_TEXT("a", "abs:href", Type.RELATIVE_LINK_TEXT) {
        },
        PARAGRAPH("p", "data-timestamp", Type.ATTRIBUTE_ONLY) {
        },
        ENTRY_META("div", "entry-meta", Type.TEXT) {
        },
        SPAN_TEXT("span", "updated", Type.PREV_TEXT) {
        },
        SPAN_TITLE("span", "title", Type.ATTRIBUTE) {
        },
        PUBLISHED("abbr", "title", Type.ATTRIBUTE) {
        },
        DATE_HEADER("h2", "date-header", Type.PARENT) {
        },
        RELATIVE_SPAN_TEXT("span", "[class*=tim]", Type.RELATIVE_TEXT);

        private final String tag;
        private final String attr;
        private final Type type;

        Time(String tag, String attr, Type type) {
            this.tag = tag;
            this.attr = attr;
            this.type = type;
        }

        @Override
        public Element apply(Element element) {
            String timeString = this.type.get(this, element);
            return getElement(timeString);
        }

        private enum Type {
            ATTRIBUTE_ONLY {
                @Override
                String get(Time time, Element element) {
                    String timeString = null;
                    Elements attributes = element.getElementsByAttribute(time.attr);

                    if (attributes.size() <= 2 && attributes.size() > 0) {
                        Element timeElement = attributes.get(0);
                        timeString = timeElement.attr(time.attr);
                    }
                    return timeString;
                }
            },
            ATTRIBUTE {
                @Override
                String get(Time time, Element element) {
                    Elements elements = element.select(time.tag + "[" + time.attr + "]");
                    if (elements.isEmpty() || elements.size() != 1) {
                        return null;
                    } else {
                        return elements.get(0).attr(time.attr);
                    }
                }
            },
            RELATIVE_LINK_TEXT {
                @Override
                String get(Time time, Element element) {
                    Elements elements = element.getElementsByAttribute(time.attr);

                    for (Element element1 : elements) {
                        if (ParseTime.hasRelative(element1.text())) {
                            return element1.text();
                        }
                    }
                    return null;
                }
            },
            RELATIVE_TEXT {
                @Override
                String get(Time time, Element element) {
                    Elements elements = element.select(time.attr);

                    for (Element element1 : elements) {
                        if (ParseTime.hasRelative(element1.text())) {
                            return element1.text();
                        }
                    }
                    return null;
                }
            },
            TEXT {
                @Override
                String get(Time time, Element element) {
                    String timeString = "";
                    Elements elements = element.getElementsByAttributeValueContaining("class", time.attr);
                    if (elements.size() == 1) {
                        timeString = elements.get(0).text();
                    }
                    return timeString;
                }
            },
            PARENT {
                @Override
                String get(Time time, Element element) {
                    Elements elements = element.parents().select(time.tag + "." + time.attr);
                    if (!elements.isEmpty()) {
                        return elements.get(0).text();
                    } else {
                        return null;
                    }
                }
            }, PREV_TEXT {
                @Override
                String get(Time time, Element element) {
                    String timeString = TEXT.get(time, element);

                    if (timeString.isEmpty()) {
                        Element previous = element.parent().previousElementSibling();
                        if (previous != null && previous.classNames().contains("post-outer")) {
                            return (get(time, previous));
                        }
                    }

                    return timeString;
                }
            };

            abstract String get(Time time, Element element);


        }

    }

    public static class LINK_PAGE implements TimeElement {
        TitleElement titleElement = null;

        public LINK_PAGE(TitleElement titleElement) {
            this.titleElement = titleElement;
        }

        @Override
        public Element apply(Element element) {
            if (titleElement == null) {
                return null;
            } else {
                String link = PostFormat.getLink(titleElement.apply(element));
                return getTime(link);
            }
        }

        Element getTime(String link) {
            Element element = null;

            if (link == null) {
                return null;
            }

            try {
                Document document = Jsoup.connect(link).get();
                element = getByProperty(document);

                if (element == null) {
                    element = getByDateTime(document);
                }
            } catch (IOException ignored) {
                System.out.println("error occurred");
                // TODO: 11.09.2017 do sth here
            }
            return element;
        }

        private Element getByDateTime(Document document) {
            Document doc = PostScraper.cleanDoc(document);
            return Time.DATETIME.apply(doc.body());
        }

        private Element getByProperty(Document document) {
            Elements elements = document.getElementsByAttributeValueContaining("property", "article:published_time");
            String timeString = null;
            if (!elements.isEmpty()) {
                timeString = elements.get(0).attr("content");
            }
            return getElement(timeString);
        }
    }
}
