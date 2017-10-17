package scrape.sources.chapter;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PathManager {
    private final Element basicElement;
    private final Elements elements;
    private Map<Path, List<Path>> groupedPaths = new HashMap<>();

    private List<Path> paths = new ArrayList<>();
    private List<Path> resolvedPaths = new ArrayList<>();

    private Path basicPath;

    PathManager(Element basicElement, Elements elements) {
        this.basicElement = basicElement;
        this.elements = elements;
        fillPaths();
    }

    void resolvePaths(Path basicPath) {
        this.basicPath = basicPath;
        resolvedPaths.clear();

        for (Path path : paths) {
            Path clone = path.clone();
            clone.resolve(basicPath);
            resolvedPaths.add(clone);
        }
    }

    public Map<Path, List<Path>> getGroupedPaths() {
        return groupedPaths;
    }

    List<Path> getPaths() {
        return paths;
    }

    List<Path> getResolvedPaths() {
        return resolvedPaths;
    }

    public Path getBasicPath() {
        return basicPath;
    }

    Path getCommonPaths() {
        if (paths.isEmpty() && !elements.isEmpty() && basicElement != null) {
            fillPaths();
        }
        return PathFinder.getCommonPath(paths);
    }

    private void fillPaths() {
        paths = PathFinder.getPaths(basicElement, elements);
    }
}
