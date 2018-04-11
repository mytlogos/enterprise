package scrape.sources.novel.chapter.strategies.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.LinkDetective;
import scrape.sources.SourceAccessor;
import scrape.sources.novel.chapter.strategies.ChapterFormat;
import scrape.sources.novel.chapter.strategies.intface.PaginationElement;
import scrape.sources.novel.chapter.strategies.intface.PaginationFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * // TODO: 06.10.2017 link should match the baseUri, same novel, not sth else
 */
public class ChapterPagination implements PaginationFilter {
    private static final PaginationElement LastElement = element -> {
        LinkDetective detective = new LinkDetective();
        String next = detective.tryNextLink(element.baseUri());
        String prev = detective.tryPrevLink(element.baseUri());

        if (((next != null) && !next.isEmpty()) || (prev != null && !prev.isEmpty())) {
            Element prevElement = new Element("a");
            prevElement.attr("href", prev);

            Element nextElement = new Element("a");
            nextElement.attr("href", next);

            return new ChapterFormat().getPaginationElement(prevElement, nextElement);
        }

        return null;
    };

    @Override
    public Collection<PaginationElement> getFilter() {
        return new ArrayList<>(Arrays.asList(Pagination.values()));
    }


    public enum Pagination implements PaginationElement {
        CLASS_WP(".wp-post-navigation-pre > a", ".wp-post-navigation-next > a"),
        LINK_REL("a[rel=prev]", "a[rel=next]"),
        LINK_IS_P_CHILD("p.alignleft > a", "p.alignright > a"),
        LINK_IS_DIV_CHILD("div.nav-previous > a", "div.nav-next > a"),
        LINK_FIRST_LAST_CHILD("p > a:first-child", "p > a:last-child"),
        LINK_CONTAINS_SPECIAL("a:contains(<<)", "a:contains(>>)"),
        LINK_CONTAINS_TEXT("a:contains(previous)", "a:contains(next)"),
        LAST_RESORT("", ""),;

        private final String previous;
        private final String next;

        Pagination(String previous, String next) {
            this.previous = previous;
            this.next = next;
        }

        @Override
        public Element apply(Element element) {
            if (this == LAST_RESORT) {
                return LastElement.apply(element);
            } else {
                Elements prevs = element.select(previous);
                Elements nexts = element.select(next);


                Element prev = getElement(prevs);
                Element next = getElement(nexts);

                if (prev != null || next != null) {
                    return new ChapterFormat().getPaginationElement(prev, next);
                }
            }
            return null;
        }

        private Element getElement(Elements elements) {
            Element result = null;
            if (elements.size() == 1) {
                result = checkElement(elements.get(0));
            } else if (elements.size() == 2) {
                result = compareFirstTwo(elements);
            }
            return result;
        }

        private Element checkElement(Element element) {
            Element result = null;
            String link = element.absUrl("href");

            if (!link.isEmpty()) {
                if (compareUri(element, link) != null) {
                    result = element;
                } /*else {
                    result = compareLanguages(element, link);
                }*/
            }
            return result;
        }

        private Element compareFirstTwo(Elements elements) {
            Element result;
            Element first = elements.get(0);
            Element second = elements.get(1);

            String firstLink = first.absUrl("href");
            String secondLink = second.absUrl("href");

            firstLink = compareUri(first, firstLink);
            secondLink = compareUri(first, secondLink);

            if (firstLink != null && secondLink != null && firstLink.equals(secondLink)) {
                result = first;
            } else if (firstLink != null) {
                result = first;
            } else {
                result = second;
            }
            return result;
        }

        private Element compareLanguages(Element element, String link) {
            Element result = null;
            try {
                Document document = SourceAccessor.getDocument(link);
                String originLang = element.ownerDocument().select("html").first().attr("lang");
                String nextOneLang = document.select("html").first().attr("lang");

                int originLangSep = originLang.indexOf("-");
                int nextOneLangSep = nextOneLang.indexOf("-");

                if (originLangSep >= 0) {
                    originLang = originLang.substring(originLangSep);
                }
                if (nextOneLangSep >= 0) {
                    nextOneLang = originLang.substring(nextOneLangSep);
                }

                System.out.println("LANGUAGE: " + nextOneLang);
                if (originLang.equals(nextOneLang)) {
                    result = element;
                }
            } catch (IOException e) {
                System.out.println("could not reach site " + link);
            }
            return result;
        }

        private String compareUri(Element element, String link) {
            try {
                URI mainUri = new URI(element.baseUri());
                URI linkUri = new URI(link);


                String mainHost = getDomain(mainUri.getHost());
                String linkHost = "";
                if (!link.isEmpty()) {
                    linkHost = getDomain(linkUri.getHost());
                }

                if (!mainHost.equals(linkHost)) {
                    link = null;
                }
            } catch (URISyntaxException e) {
                System.out.println("erroneous url!");
            }
            return link;
        }

        private String getDomain(String mainHost) {
            String[] splitted = mainHost.split("\\.");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < splitted.length; i++) {
                if (i + 1 < splitted.length) {
                    builder.append(splitted[i]).append(".");
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            mainHost = builder.toString();
            return mainHost;
        }
    }

}
