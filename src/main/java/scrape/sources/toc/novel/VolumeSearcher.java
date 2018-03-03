package scrape.sources.toc.novel;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import scrape.sources.toc.Path;
import scrape.sources.toc.PathFinder;
import scrape.sources.toc.intface.TocBuilder;
import scrape.sources.toc.structure.CreationRoot;
import tools.Condition;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class VolumeSearcher extends Searcher {
    private final String[] volumeTerms = new String[]{
            "volume", "arc", "book"
    };
    private Element wrapperElement;
    private TocBuilder builder;

    public VolumeSearcher() {

    }

    @Override
    public CreationRoot search(Element wrapperElement, TocBuilder builder) {
        Condition.check().nonNull(wrapperElement, builder);
        this.builder = builder;
        this.wrapperElement = wrapperElement;
        return getVolumeTocs().stream().filter(Objects::nonNull).findFirst().orElse(null);
    }

    private List<CreationRoot> getVolumeTocs() {
        System.out.println("for " + wrapperElement.baseUri());

        //getAll volumeTerms with hits
        Map<String, Integer> occurrence = highestOccurrence(getWrapperClone().text());

        if (occurrence.isEmpty()) {
            return new ArrayList<>();
        }

        //search for elements with text containing the keys of occurrence
        VolumeManager container = searchNodesForVolume(wrapperElement, occurrence);

        Element firstCommonParent = container.getCommonPath().getLast();  //gets the common path of all volumes

        return getVolumeTocs(container, firstCommonParent);
    }

    private List<CreationRoot> getVolumeTocs(VolumeManager container, Element firstCommonParent) {
        container.resolve(container.getCommonPath());

        Map<String, List<Path>> resolvedMappedPaths = container.getResolvedMappedPaths();

        List<CreationRoot> tocs = new ArrayList<>();
        for (String key : resolvedMappedPaths.keySet()) {
            tocs.add(getVolumeToc(firstCommonParent, resolvedMappedPaths, key));
        }
        return tocs;
    }

    private CreationRoot getVolumeToc(Element firstCommonParent, Map<String, List<Path>> resolvedMappedPaths, String key) {
        List<Path> paths = resolvedMappedPaths.get(key);

        paths.sort(Comparator.comparingInt(this::getFirstIndex));

        SortedMap<Element, SortedMap<Element, List<Element>>> mapMap = new TreeMap<>(Comparator.comparingInt(Node::siblingIndex));

        for (int i = 0; i < paths.size(); i++) {
            if (i < paths.size() - 1) {
                Path path = paths.get(i);
                int currentVolumeIndex = getFirstIndex(path);
                int nextVolumeIndex = getFirstIndex(paths.get(i + 1));

                Elements elements = new Elements();

                for (int j = currentVolumeIndex + 1; j < nextVolumeIndex; j++) {
                    Element element = firstCommonParent.children().get(j);
                    elements.add(element);
                }

                Elements links = elements.select("a");

                SortedMap<Element, List<Element>> elementListMap;

                if (links.isEmpty()) {
                    elementListMap = lookSecondary(path);
                } else {
                    elementListMap = lookPrimary(firstCommonParent, links);
                }
                mapMap.put(path.getLast(), elementListMap);
            }
        }
        return buildToc(mapMap);
    }

    private CreationRoot buildToc(SortedMap<Element, SortedMap<Element, List<Element>>> map) {

        if (map.isEmpty()) {
            builder.init("N/A", "N/A");
        } else {
            // TODO: 05.11.2017 do the title thing
            builder.init("", map.firstKey().baseUri());
        }

        // TODO: 02.11.2017 read the chapter number from the element
        // TODO: 02.11.2017 check if element is an extra or not
        // TODO: 02.11.2017 check type of elements
        int volumeCounter = 1;
        for (Element volume : map.keySet()) {

            SortedMap<Element, List<Element>> elementListMap = map.get(volume);
            if (!elementListMap.isEmpty()) {
                builder.addSection("volume", false, volume.ownText(), volumeCounter++);
            }

            int chapterCounter = 1;
            for (Element chapter : elementListMap.keySet()) {
                builder.addPortion(chapter.ownText(), "", chapterCounter++, false);

                int subChapterCounter = 1;
                for (Element subChapter : elementListMap.get(chapter)) {
                    builder.addSubPortion(subChapter.ownText(), subChapter.absUrl("href"), subChapterCounter, false);
                }
            }
        }
        return builder.build();
    }

    private SortedMap<Element, List<Element>> lookPrimary(Element basicElement, Elements elements) {

        Map<Element, List<Element>> subsMap = PathFinder.group(basicElement, elements);
        SortedMap<Element, List<Element>> elementMap;

        if (subsMap.isEmpty()) {
            elementMap = getElementPartitionMap(elements);
        } else {
            elementMap = getElementListMap(subsMap);
        }
        return elementMap;
    }

    private SortedMap<Element, List<Element>> getElementListMap(Map<Element, List<Element>> subsMap) {
        List<Element> keys = new ArrayList<>(subsMap.keySet());
        keys.sort(getElementComparator());

        Map<Element, List<Element>> map = getElementPartitionMap(keys);
        SortedMap<Element, List<Element>> elementMap = new TreeMap<>(getElementComparator());

        for (Element key : map.keySet()) {
            List<Element> subList = map.get(key);

            for (Element sub : subsMap.keySet()) {
                if (subList.contains(sub)) {
                    elementMap.put(key, subsMap.get(sub));
                }
            }
        }
        return elementMap;
    }

    private SortedMap<Element, List<Element>> getElementPartitionMap(List<Element> elements) {
        SortedMap<Element, List<Element>> map = new TreeMap<>(getElementComparator());

        List<Element> elementList = new ArrayList<>();

        for (int i = elements.size() - 1; i >= 0; i--) {
            if (i > 0) {
                Element current = elements.get(i);
                Element previous = elements.get(i - 1);

                Element sibling = current.previousElementSibling();

                if (sibling != null && sibling.hasText() && sibling != previous) {
                    elementList.add(current);
                    map.put(sibling, elementList);
                    elementList = new ArrayList<>();
                } else {
                    elementList.add(current);
                }
                prependPreviousText(current);
            }
        }
        return map;
    }

    private Comparator<Element> getElementComparator() {
        return Comparator.comparingInt(Element::elementSiblingIndex);
    }

    private void prependPreviousText(Element current) {
        Node previousSibling = current.previousSibling();
        if (previousSibling instanceof TextNode) {
            current.prependText(((TextNode) previousSibling).text() + " ");
        }
    }

    private SortedMap<Element, List<Element>> lookSecondary(Path path) {
        path.getLast().remove();

        Elements secondaryLinks = getLinks(path.getFirst());

        if (!secondaryLinks.isEmpty()) {
            List<Path> linkPaths = PathFinder.getPaths(path.getFirst(), secondaryLinks);
            Element commonParent = PathFinder.getCommonPath(linkPaths).getLast();

            SortedMap<Element, List<Element>> elementListMap = lookPrimary(commonParent, secondaryLinks);

            System.out.println("SECONDARY");
            return elementListMap;
        }
        return new TreeMap<>();
    }

    private Elements getLinks(Element element) {
        return element.select("a").stream().filter(Element::hasText).collect(Collectors.toCollection(Elements::new));
    }

    private Map<String, List<Path>> getSimilarVolumes(VolumeManager container, Element last) {
        Map<String, List<Path>> pathsMap = new HashMap<>();

        if (container.isResolved()) {
            Map<String, Set<String>> selectors = container.getResolvedSelectors();
            searchVolumes(last, pathsMap, selectors);
        } else {
            Map<String, Set<String>> selectors = container.getSelectors();
            searchVolumes(wrapperElement, pathsMap, selectors);
        }
        return pathsMap;
    }

    private void searchVolumes(Element last, Map<String, List<Path>> pathsMap, Map<String, Set<String>> selectors) {
        for (String key : selectors.keySet()) {
            pathsMap.put(key, new ArrayList<>());
            for (String selector : selectors.get(key)) {
                Elements elements = last.select(selector + selectorLimiter()).
                        stream().
                        filter(Element::hasText).
                        filter(element -> !element.tagName().equals("a")).
                        collect(Collectors.toCollection(Elements::new));

                setPathMap(last, pathsMap, key, elements);
            }
        }
    }

    private void printVolumes(Map<Path, List<Path>> volumes) {
        for (Path elementPath : volumes.keySet()) {
            System.out.println("VOLUME");
            System.out.println(elementPath.getLast().text());
            System.out.println("LINKS");
            for (Path path : volumes.get(elementPath)) {
                System.out.println(path.getLast().text());
            }
        }
    }

    private int getFirstIndex(Path path) {
        return path.getFirst().elementSiblingIndex();
    }

    private Element getWrapperClone() {
        Element clone = wrapperElement.clone();
        clone.select("a:not([data-toggle])").remove();
        clone.select(":matchesOwn(chapter.\\d{1,4})").remove();
        return clone;
    }

    private String selectorLimiter() {
        return ":not(a:not([data-toggle]),:matchesOwn(chapter.\\d{1,4}))";
    }

    private VolumeManager searchNodesForVolume(Element basicElement, Map<String, Integer> occurrence) {
        List<String> keys = occurrence.
                keySet().
                stream().
                filter(s -> occurrence.get(s) != 0).
                sorted((o1, o2) -> occurrence.get(o2) - occurrence.get(o1)).
                collect(Collectors.toList());


        Map<String, List<Path>> mappedPaths = getVolumePathMap(basicElement, keys);

        VolumeManager volumeManager = new VolumeManager(mappedPaths);

        Map<String, List<Path>> similarVolumes = getSimilarVolumes(volumeManager, wrapperElement);
        volumeManager.addNewVolumes(similarVolumes);

        return volumeManager;
    }

    private Map<String, List<Path>> getVolumePathMap(Element basicElement, List<String> keys) {
        Map<String, List<Path>> mappedPaths = new HashMap<>();

        for (String s : keys) {
            mappedPaths.put(s, new ArrayList<>());
            putPath(basicElement, mappedPaths, s);
        }
        return mappedPaths;
    }

    private void putPath(Element basicElement, Map<String, List<Path>> paths, String key) {
        //search for key case insensitive, searched for key as a own word, in, at the beginning or the end of a line
        Elements elements = basicElement.select(":matchesOwn((?i)(\\W|^)" + key + "(\\W|$))" + selectorLimiter());
        setPathMap(basicElement, paths, key, elements);
    }

    private void setPathMap(Element basicElement, Map<String, List<Path>> paths, String key, Elements elements) {
        elements.forEach(element -> {
            Path path = PathFinder.getPath(element, basicElement, new Path());
            path.addChild(element);
            paths.get(key).add(path);
        });
    }

    private Map<String, Integer> highestOccurrence(String text) {
        Map<String, Integer> integerMap = new HashMap<>();

        for (String volumeTerm : volumeTerms) {
            Pattern pattern = Pattern.compile("\\W" + volumeTerm + "\\W", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);

            int counter = 0;
            while (matcher.find()) {
                counter++;
            }
            if (counter > 0) {
                integerMap.put(volumeTerm, counter);
            }
        }

        /*if (hasNumberSequence(text, integerMap)) {
            System.out.println("has sequence for sth");
        }*/
        for (String s : integerMap.keySet()) {
            int i = integerMap.get(s);
            if (i > 0) {
                System.out.println(s + " with " + i);
            }
        }
        return integerMap;
    }

    private boolean hasNumberSequence(String text, Map<String, Integer> map) {
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d)?)(?:[^a-zA-Z])");
        List<Integer> integerList = new ArrayList<>();

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            double number = Double.parseDouble(matcher.group(1));
            integerList.add((int) number);
        }
        List<Integer> maxSequence = getMaxSequence(integerList);

        if (maxSequence.size() > 1) {
            map.put(maxSequence.toString(), maxSequence.size());
            return true;
        }

        return false;
    }

    private List<Integer> getMaxSequence(List<Integer> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        if (list.size() == 1) {
            List<Integer> integers = new ArrayList<>();
            integers.add(list.get(0));
            return integers;
        }

        List<List<Integer>> listList = getMaxSequence(0, list, new ArrayList<>(), new ArrayList<>());
        if (listList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return listList.get(0);
        }
    }

    private List<List<Integer>> getMaxSequence(int position, List<Integer> integers, List<List<Integer>> sequence, List<Integer> builder) {
        if (position == integers.size() - 1) {
            builder.add(integers.get(position));
            sequence.add(builder);
            return sequence;
        }

        int first = integers.get(position);
        int second = integers.get(position + 1);

        if (first == second || Math.incrementExact(first) == second) {
            builder.add(first);
        } else {
            if (!builder.toString().isEmpty()) {
                builder.add(first);
            }
            sequence.add(builder);
            builder = new ArrayList<>();
        }

        sequence = getMaxSequence(++position, integers, sequence, builder);

        List<Integer> integerList = sequence.stream().max(Comparator.comparingInt(List::size)).orElse(new ArrayList<>());
        List<List<Integer>> strings = new ArrayList<>();
        strings.add(integerList);

        return strings;
    }


    private void printPath(Path path) {
        path.getPath().forEach(element -> System.out.print(elementToString(element) + " | "));
        System.out.println();
    }

    private String elementToString(Element element) {
        return element.tagName() + element.classNames() + " #" + element.id();
    }
}
