package scrape.sources.toc;

import org.jsoup.nodes.Element;

import java.util.*;

/**
 *
 */
public class PathFinder {
    public static List<Path> getPaths(Element wrapperElement, List<Element> elements) {
        List<Path> linkPath = new ArrayList<>();

        for (Element element : elements) {
            Path path = getPath(element, wrapperElement, new Path());
            linkPath.add(path);
        }
        return linkPath;
    }


    public static Path getPath(Element startElement, Element basicElement, Path path) {
        Element parent = startElement.parent();
        if (parent != null) {
            if (parent == basicElement) {
                path.addParent(parent);
            } else {
                path.addParent(parent);
                getPath(parent, basicElement, path);
            }
        }
        return path;
    }

    public static Path getCommonPath(Path... linkedLists) {
        return getCommonPath(Arrays.asList(linkedLists));
    }

    private static List<Path> getCompletePaths(Element basicElement, List<Element> links) {
        List<Path> paths = new ArrayList<>();
        links.forEach(element -> paths.add(getCompletePath(element, basicElement)));
        return paths;
    }

    public static Path getCommonPath(List<Path> linkedLists) {
        int minSize = linkedLists.get(0).getPathSize();

        for (Path linkedList : linkedLists) {
            if (linkedList.getPathSize() < minSize) {
                minSize = linkedList.getPathSize();
            }
        }

        Path path = new Path();

        for (int i = 0; i < minSize; i++) {
            Element element = linkedLists.get(0).get(i);
            if (elementPositionCheck(linkedLists, element, path, i)) break;
        }
        return path;
    }

    private static boolean elementPositionCheck(List<Path> linkedLists, Element element, Path path, int i) {
        if (linkedLists.stream().allMatch(list -> element == list.get(i))) {
            path.addChild(element);
        } else {
            return true;
        }
        return false;
    }


    static void resolve(Path basePath, Path... linkedLists) {
        for (Path linkedList : linkedLists) {
            linkedList.resolve(basePath);
        }
    }

    static void resolve(Path basePath, List<Path> linkedLists) {
        for (Path linkedList : linkedLists) {
            linkedList.resolve(basePath);
        }
    }

    public static Map<Path, List<Path>> groupToPaths(Element basicElement, List<Element> elements) {
        List<Path> paths = getCompletePaths(basicElement, elements);
        Path commonPath = getCommonPath(paths);
        paths.forEach(path -> path.resolve(commonPath));

        paths.forEach(path -> System.out.println(path.getFirst().elementSiblingIndex()));
        return new HashMap<>();
    }

    public static Map<Element, List<Element>> group(Element basicElement, List<Element> links) {
        List<Path> paths = PathFinder.getCompletePaths(basicElement, links);
        paths.forEach(path -> path.resolve(new Path().addParent(basicElement)));

        Map<Element, List<Element>> listMap = new HashMap<>();

        for (Path path : paths) {
            Element lastParent = path.getFirst();

            if (!listMap.containsKey(lastParent)) {
                listMap.put(lastParent, new ArrayList<>());
            }

            listMap.get(lastParent).add(path.getLast());
        }
        return listMap.entrySet().size() == 1 ? new HashMap<>() : listMap;
    }

    public static Map<Path, List<Path>> groupPaths(List<Path> linkedLists) {
        return new HashMap<>();
    }

    private static Path getCompletePath(Element startElement, Element basicElement) {
        return getPath(startElement, basicElement, new Path()).addChild(startElement);
    }
}
