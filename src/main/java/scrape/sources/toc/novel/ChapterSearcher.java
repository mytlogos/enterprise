package scrape.sources.toc.novel;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.PatternSearcher;
import scrape.sources.toc.TocBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class ChapterSearcher {
    //get all links from contentElement
    //remove links leading to other domains, or links with crossed text
    // remove links with prologue/epilogue
    // ignore links leading to sites with asiatic languages

    public Element getChapterToc(Element contentElement) {
        String firstLvlDomain = getFirstLvlDomain(contentElement);

        Elements toc = contentElement.select(":matchesOwn((?i)^(index|table of content(s)?|content(s)?|chapters|chapter list)(\\s|$)):not(a)").not(".sp-head");

        Elements allLinks = getAllLinks(contentElement, toc);

        Elements ownDomainLinks = allLinks.select("[href*=" + firstLvlDomain + "]:not(strike,s,del)");
        Elements beginning = ownDomainLinks.select("a:matches((?i)prologue), [href~=prologue(/|$)]");

        Element linkToc = null;

        if (!ownDomainLinks.isEmpty()) {
            System.out.println("for " + contentElement.baseUri());
            List<Element> elements;
            if (beginning.size() == 1) {
                elements = ownDomainLinks.subList(ownDomainLinks.indexOf(beginning.get(0)), ownDomainLinks.size());
            } else {
                elements = ownDomainLinks;
            }
            linkToc = process(elements);
        }
        return linkToc;
    }

    private Element process(List<Element> selected) {
        List<Element> elements = getElements(selected);
        return createToc(elements);
    }

    private List<Element> getElements(List<Element> selected) {
        Map<String, Element> texts = selected
                .stream()
                .collect(Collectors.toMap(
                        Element::text,
                        Function.identity(),
                        (v1, v2) -> v1,
                        TreeMap::new));

        List<String> filteredTexts = PatternSearcher.searchText(new ArrayList<>(texts.keySet()));

        List<Element> elements;

        // TODO: 10.10.2017 do a secondary search for extras and interludes?
        // TODO: 12.10.2017 remove elements which have same link
        // TODO: 12.10.2017 make sth like volume, chapter, title
        // TODO: 12.10.2017 add prologue and epilogue
        // TODO: 12.10.2017 if it leads to another host: determine if chapter or toc, if toc, get chapters
        if (isTolerable(texts, filteredTexts)) {
            elements = getLinkElements(selected);
        } else {
            elements = getTextElements(texts, filteredTexts);
        }
        return elements;
    }

    private List<Element> getTextElements(Map<String, Element> texts, List<String> filteredTexts) {
        List<Element> elements;
        filteredTexts.sort(getStringComparator());

        elements = filteredTexts
                .stream()
                .map(texts::get)
                .filter(element -> element.hasAttr("href"))
                .collect(Collectors.toList());
        return elements;
    }

    private Comparator<String> getStringComparator() {
        // TODO: 12.10.2017 make a better sorting algorithm, does not sort after ascending number order but more like 1,10,11,2,3,30,31,4
        return (o1, o2) -> {
            Pattern pattern = Pattern.compile("\\d{1,4}");
            int first = 0;
            int second = 0;
            Matcher firstMatcher = pattern.matcher(o1);
            Matcher secondMatcher = pattern.matcher(o2);

            if (firstMatcher.find()) {
                String integer = firstMatcher.group();
                if (!integer.isEmpty()) {
                    first = Integer.parseInt(integer);
                }
            }
            if (secondMatcher.find()) {
                String integer = secondMatcher.group();
                if (!integer.isEmpty()) {
                    second = Integer.parseInt(integer);
                }
            }
            return first - second;
        };
    }

    private String decode(Element e) {
        try {
            return URLDecoder.decode(e.absUrl("href"), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return "";
        }
    }

    private List<Element> getLinkElements(List<Element> selected) {
        List<Element> elements;
        Map<String, Element> mappedLinks = selected
                .stream()
                .collect(Collectors.toMap(this::decode, Function.identity(), (v1, v2) -> v1, TreeMap::new));

        List<String> filteredLinks = PatternSearcher.searchLinks(new ArrayList<>(mappedLinks.keySet()));
        filteredLinks.sort(getStringComparator());

        elements = filteredLinks.
                stream().
                map(s -> (mappedLinks.get(s)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return elements;
    }

    private boolean isTolerable(Map<String, Element> texts, List<String> filteredTexts) {
        if (texts.isEmpty()) {
            return false;
        }
        double first = Math.sqrt(filteredTexts.size());
        double second = Math.sqrt(texts.size());
        double relation = (second - first) / second;

        // TODO: 12.10.2017 maybe do sth make better to relate the size of the two lists
        return relation < 0.5;
    }

    private Element createToc(List<Element> elements) {
        TocBuilder tocBuilder;

        if (elements.isEmpty()) {
            tocBuilder = new TocBuilder();
        } else {
            tocBuilder = new TocBuilder(elements.get(0).baseUri());
        }

        elements.forEach(element -> {
            String url = element.absUrl("href");
            String text = element.text();
            tocBuilder.addChapter(text, url);
        });
        return tocBuilder.getToc();
    }

    private Elements getAllLinks(Element contentElement, Elements toc) {
        Elements allLinks;
        if (toc.hasText() && toc.size() == 1) {
            Element child = getFirstLvlChild(toc.get(0), contentElement);

            if (child != null) {
                allLinks = child.select("a[href]");
            } else {
                allLinks = contentElement.select("a[href]");
            }

        } else {
            allLinks = contentElement.select("a[href]");
        }
        return allLinks;
    }

    private Element getFirstLvlChild(Element element, Element contentElement) {
        if (element.parents().contains(contentElement)) {

            Element parent = element.parent();
            return parent == contentElement ? element : getFirstLvlChild(parent, contentElement);
        } else {
            return null;
        }
    }

    private String getFirstLvlDomain(Element contentElement) {
        String link = contentElement.baseUri();

        URI uri = URI.create(link);
        String host = uri.getHost();
        Pattern pattern = Pattern.compile("^(www.)?(\\w+)\\.");
        Matcher matcher = pattern.matcher(host);

        return matcher.find() ? matcher.group(2) : ".";
    }
}
