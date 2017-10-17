package scrape.sources.chapter;

import org.jsoup.nodes.Element;

import java.util.*;

/**
 *
 */
public class PathFinder {
    static List<Path> getPaths(Element wrapperElement, List<Element> elements) {
        List<Path> linkPath = new ArrayList<>();

        for (Element element : elements) {
            Path list = getCommonPath(element, wrapperElement, new Path());
            linkPath.add(list);
        }
        return linkPath;
    }


    static Path getCommonPath(Element startElement, Element basicElement, Path list) {
        Element parent = startElement.parent();
        if (parent != null) {
            if (parent == basicElement) {
                list.addParent(parent);
            } else {
                list.addParent(parent);
                getCommonPath(parent, basicElement, list);
            }
        }
        return list;
    }

    static Path getCommonPath(List<Path> linkedLists) {
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

    static Path getCommonPath(Path... linkedLists) {
        return getCommonPath(Arrays.asList(linkedLists));
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


    public static Map<Path, List<Path>> groupPaths(List<Path> linkedLists) {
        return new HashMap<>();
    }
}
