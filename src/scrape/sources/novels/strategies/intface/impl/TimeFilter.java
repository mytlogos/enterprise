package scrape.sources.novels.strategies.intface.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.novels.ParseTime;
import scrape.sources.novels.strategies.PostFormat;
import scrape.sources.novels.strategies.intface.ElementFilter;
import scrape.sources.novels.strategies.intface.TimeElement;

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

    /**
     *
     */
    public enum Time implements TimeElement {
        DATETIME("time", "datetime", Type.ATTRIBUTE_ONLY) {
        },
        LINK_TITLE("a", "title", Type.ATTRIBUTE) {
        },
        LINK_TEXT("a", "abs:href", Type.LINK_TEXT) {
        },
        PARAGRAPH("p", "data-timestamp", Type.ATTRIBUTE_ONLY) {
        },
        ENTRY_META("div", "entry-meta", Type.TEXT) {
        },
        SPAN_TEXT("span", "updated", Type.TEXT) {
        },
        SPAN_TITLE("span", "title", Type.ATTRIBUTE) {
        },
        PUBLISHED("abbr", "title", Type.ATTRIBUTE) {
        },
        TITLE_LINK("h1[class*=title]>a[href], h2[class*=title]>a[href], h3[class*=title] > a[href], h4[class*=title] > a[href]", "abs:href", Type.LAST_RESORT) {
        },
        DATE_HEADER("h2", "date-header", Type.PARENT) {
        };

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

            if (ParseTime.isParseAble(timeString)) {
                return PostFormat.timeElement(timeString);
            }
            return null;
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
                    if (elements.isEmpty()) {
                        return null;
                    }

//                    System.out.println("TIME: " + elements.get(0));
                    // TODO: 14.09.2017 sth was wrong here, copy of LINK_TEXT
                    return null;
                }
            },
            LINK_TEXT {
                @Override
                String get(Time time, Element element) {
                    Elements elements = element.getElementsByAttribute(time.attr);

                    for (Element element1 : elements) {
                        if (element1.text().matches(".*[0-5]?\\d\\s(minute(s)?|second(s)?|hour(s)?|day(s)?|week(s)?|month(s)?|year(s)?)\\sago")) {
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
            LAST_RESORT {
                @Override
                String get(Time time, Element element) {
                    String timeString = null;

                    Elements postContentLinkElements = element.select(time.tag);
                    if (!postContentLinkElements.isEmpty()) {
                        String postContentLink = postContentLinkElements.get(0).attr(time.attr);

                        if (!postContentLink.isEmpty()) {
                            try {
                                Document document = Jsoup.connect(postContentLink).get();
                                Elements elements = document.getElementsByAttributeValueContaining("property", "article:published_time");
                                if (!elements.isEmpty()) {
                                    timeString = elements.get(0).attr("content");
                                }
                            } catch (IOException ignored) {
                                // TODO: 11.09.2017 do sth here
                            }
                        }
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
            };

            abstract String get(Time time, Element element);


        }

    }
}
