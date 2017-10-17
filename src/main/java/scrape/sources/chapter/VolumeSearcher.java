package scrape.sources.chapter;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class VolumeSearcher {

    private String link;
    private Element wrapperElement;
    private String[] volumeTerms = new String[]{
            "volume", "arc", "book"
    };

    public VolumeSearcher(String link, Element wrapperElement) {
        this.link = link;
        this.wrapperElement = wrapperElement;

        if (link == null || link.isEmpty() || wrapperElement == null) {
            throw new IllegalArgumentException();
        }
    }

    public void searchForVolumes() {
        System.out.println("for " + link);

        Map<String, Integer> occurrence = highestOccurrence(getWrapperClone().text()); //get volumeTerms with hits
        VolumeManager container = searchNodesForVolume(wrapperElement, occurrence);  //search for elements with text containing the keys of occurrence
        Path volumePath = container.getCommonPath();  //gets the common path of all volumes

        PathManager linkPathManager = new PathManager(wrapperElement, wrapperElement.select("a")); //a pathManager for the links, gets all paths for each link
        Path elementLinkedList = linkPathManager.getCommonPaths(); //gets the common path of all links

        if (!volumePath.isEmpty()) {
            Path mostCommonPath = PathFinder.getCommonPath(volumePath, elementLinkedList);  //gets common path of all links and volumes

            linkPathManager.resolvePaths(mostCommonPath);  //resolves the paths of the links against the common paths of links and volumes

            container.resolve(mostCommonPath);   //resolves the paths of all volumes against the common paths of links and volumes


            System.out.println("MOST COMMON PATH");
            printPath(mostCommonPath);
            System.out.println("VOLUME PATH");
            printPath(volumePath);
            System.out.println("LINK PATH");
            printPath(elementLinkedList);
            Element last = mostCommonPath.getLast();

            Map<Path, List<Path>> volumes = mapLinksToVolumes(container, linkPathManager, last);

//            printVolumes(volumes);
//            printPaths(container, linkPathManager, mostCommonPath);
        } else if (!occurrence.isEmpty()) {
            System.out.println("AN ERROR OCCURRED: FOUND VOLUMES BUT NO PATH");
        }
    }

    public void getVolumes() {
        System.out.println("for " + link);

        //get volumeTerms with hits
        Map<String, Integer> occurrence = highestOccurrence(getWrapperClone().text());

        if (occurrence.isEmpty()) {
            return;
        }

        //search for elements with text containing the keys of occurrence
        VolumeManager container = searchNodesForVolume(wrapperElement, occurrence);

        Element firstCommonParent = container.getCommonPath().getLast();  //gets the common path of all volumes

        container.resolve(container.getCommonPath());

        Map<String, List<Path>> resolvedMappedPaths = container.getResolvedMappedPaths();

        for (String key : resolvedMappedPaths.keySet()) {
            List<Path> paths = resolvedMappedPaths.get(key);
            for (int i = 0; i < paths.size(); i++) {

            }
        }
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
                Elements elements = last.select(selector + selectorLimiter());
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

    private Map<Path, List<Path>> mapLinksToVolumes(VolumeManager container, PathManager linkPathManager, Element last) {
        TreeMap<Path, List<Path>> volumes = new TreeMap<>(Comparator.comparingInt(path -> getIndex(last, path)));

        for (List<Path> paths : container.getResolvedMappedPaths().values()) {
            for (int i = 0, pathsSize = paths.size(); i < pathsSize; i++) {
                if (i < pathsSize - 1) {
                    Path path = paths.get(i);
                    int currentVolumeIndex = getIndex(last, path);
                    int nextVolumeIndex = getIndex(last, paths.get(i + 1));

                    List<Path> volumeLinks = new ArrayList<>();
                    for (Path elementPath : linkPathManager.getResolvedPaths()) {
                        if (currentVolumeIndex <= getIndex(last, elementPath) && getIndex(last, elementPath) < nextVolumeIndex) {
                            volumeLinks.add(elementPath);
                        }
                    }
                    volumes.put(path, volumeLinks);
                } else {
                    Path path = paths.get(i);
                    int currentVolumeIndex = getIndex(last, path);

                    List<Path> volumeLinks = new ArrayList<>();
                    for (Path elementPath : linkPathManager.getResolvedPaths()) {
                        if (currentVolumeIndex <= getIndex(last, elementPath)) {
                            volumeLinks.add(elementPath);
                        }
                    }
                    volumes.put(path, volumeLinks);
                }
            }
        }
        return volumes;
    }

    private int getIndex(Element last, Path path) {
        return last.children().indexOf(path.get(0));
    }

    private Element getWrapperClone() {
        Element clone = wrapperElement.clone();
        clone.select("a:not([data-toggle])").remove();
        clone.select(":matchesOwn(chapter.\\d{1,4})").remove();
        return clone;
    }

    private String selectorLimiter() {
        return ":not(a[data-toggle],:matchesOwn(chapter.\\d{1,4}))";
    }

    private VolumeManager searchNodesForVolume(Element basicElement, Map<String, Integer> occurrence) {
        List<String> keys = occurrence.
                keySet().
                stream().
                filter(s -> occurrence.get(s) != 0).
                sorted((o1, o2) -> occurrence.get(o2) - occurrence.get(o1)).
                collect(Collectors.toList());


        Map<String, List<Path>> mappedPaths = new HashMap<>();

        for (String s : keys) {
            mappedPaths.put(s, new ArrayList<>());
            putPath(basicElement, mappedPaths, s);
        }

        VolumeManager volumeManager = new VolumeManager(mappedPaths);

        Map<String, List<Path>> similarVolumes = getSimilarVolumes(volumeManager, wrapperElement);
        volumeManager.addNewVolumes(similarVolumes);

        return volumeManager;
    }

    private void putPath(Element basicElement, Map<String, List<Path>> paths, String key) {
        //search for key case insensitive, searched for key as a own word, in, at the beginning or the end of a line
        Elements elements = basicElement.select(":matchesOwn((?i)(\\W|^)" + key + "(\\W|$))" + selectorLimiter());
        setPathMap(basicElement, paths, key, elements);
    }

    private void setPathMap(Element basicElement, Map<String, List<Path>> paths, String key, Elements elements) {
        elements.forEach(element -> {
            Path path = PathFinder.getCommonPath(element, basicElement, new Path());
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
