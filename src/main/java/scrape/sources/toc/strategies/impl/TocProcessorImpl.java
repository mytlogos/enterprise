package scrape.sources.toc.strategies.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.PatternSearcher;
import scrape.sources.chapter.GravityNovel;
import scrape.sources.posts.strategies.ContentWrapper;
import scrape.sources.toc.strategies.intface.TocProcessor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class TocProcessorImpl implements TocProcessor {


    private static String getUrl(Element e) {
        try {
            return URLDecoder.decode(e.absUrl("href"), "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return "";
        }
    }

    @Override
    public Element process(Document document) {
        Element gravityTalesToc = checkGravityTales(document);
        if (gravityTalesToc != null) return gravityTalesToc;

        ContentWrapper wrapper = ContentWrapper.tryAll(document);
        if (wrapper == null) {
            System.out.println(document.location() + " not supported");
            return null;
        } else {
            Element content = wrapper.apply(document);
            Elements selected = content.select("a");

            selected.forEach(element -> System.out.println(element.attr("href")));

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

            return createToc(elements);
        }
    }

    @Override
    public void processStructure(Document document) {

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

    private List<Element> getLinkElements(Elements selected) {
        List<Element> elements;
        Map<String, Element> mappedLinks = selected
                .stream()
                .collect(Collectors.toMap(TocProcessorImpl::getUrl, Function.identity(), (v1, v2) -> v1, TreeMap::new));

        List<String> filteredLinks = PatternSearcher.searchLinks(new ArrayList<>(mappedLinks.keySet()));
        filteredLinks.sort(getStringComparator());

        elements = filteredLinks.
                stream().
                map(s -> (mappedLinks.get(s)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return elements;
    }

    private Element checkGravityTales(Document document) {
        if (document.location().contains("gravitytales")) {
            List<Element> elements = GravityNovel.lookUpToc(document.location());
            return createToc(elements);
        }
        return null;
    }

    private Element createToc(List<Element> elements) {

        Element element = new Element("div");

        element.attr("id", "toc");

        elements.forEach(e -> e.removeAttr("title"));
        elements.forEach(element::appendChild);
        return element;
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
}
