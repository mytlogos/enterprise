package scrape.sources.toc.novel;

import scrape.sources.toc.Path;
import scrape.sources.toc.PathFinder;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
class VolumeManager {
    private Map<String, List<Path>> mappedPaths = new HashMap<>();
    private boolean resolved = false;
    private Map<String, List<Path>> resolvedMappedPaths = new HashMap<>();
    private Map<String, Set<String>> selectorMap = new HashMap<>();
    private Path mostCommonPath = null;
    private Map<String, Path> linkedListMap = null;

    VolumeManager(Map<String, List<Path>> mappedPaths) {
        this.mappedPaths = mappedPaths;
    }

    Map<String, List<Path>> getMappedPaths() {
        return mappedPaths;
    }

    void resolve(Path path) {
        for (String s : mappedPaths.keySet()) {
            List<Path> paths = new ArrayList<>();
            mappedPaths.get(s).forEach(elementPath -> {
                Path clone = elementPath.clone();
                clone.resolve(path);
                paths.add(clone);
            });
            resolvedMappedPaths.put(s, paths);
        }
        resolved = true;
    }

    public boolean isResolved() {
        return resolved;
    }

    public Map<String, Set<String>> getSelectors() {
        if (!selectorMap.isEmpty()) {
            return selectorMap;
        } else {
            readySelectorMap();
            return selectorMap;
        }
    }

    public Map<String, Set<String>> getResolvedSelectors() {
        if (!selectorMap.isEmpty()) {
            return selectorMap;
        } else {
            readySelectorMap();
            return selectorMap;
        }
    }

    Map<String, List<Path>> getResolvedMappedPaths() {
        return resolvedMappedPaths;
    }

    Path getCommonPath() {
        if (!mappedPaths.isEmpty()) {
            if (mostCommonPath == null) {
                Map<String, Path> commonMappedPath = getCommonMappedPath();

                if (!commonMappedPath.isEmpty()) {
                    mostCommonPath = PathFinder.getCommonPath(new ArrayList<>(linkedListMap.values()));
                } else {
                    return new Path();
                }
            }
        } else {
            return new Path();
        }
        return mostCommonPath;
    }

    private void produceCommonMappedPath() {
        linkedListMap = new HashMap<>();

        for (String s : mappedPaths.keySet()) {
            List<Path> linkedLists = mappedPaths.get(s);
            if (linkedLists != null && !linkedLists.isEmpty()) {

                Path path = PathFinder.getCommonPath(linkedLists);
                linkedListMap.put(s, path);
            }
        }
    }

    /**
     * Adds Paths to the value list of the corresponding keys,
     * if they are new.
     *
     * @param similarVolumes a map, not null
     */
    void addNewVolumes(Map<String, List<Path>> similarVolumes) {
        Objects.requireNonNull(similarVolumes);

        for (String key : similarVolumes.keySet()) {
            if (mappedPaths.containsKey(key)) {
                for (Path path : similarVolumes.get(key)) {
                    if (!mappedPaths.get(key).contains(path)) {
                        mappedPaths.get(key).add(path);
                    }
                }
            }
        }
    }

    private Map<String, Path> getCommonMappedPath() {
        if (!mappedPaths.isEmpty()) {
            if (linkedListMap == null) {
                produceCommonMappedPath();
            }
            return linkedListMap;
        } else {
            return new HashMap<>();
        }
    }

    private void readySelectorMap() {
        Map<String, List<Path>> map;
        if (resolvedMappedPaths.isEmpty()) {
            map = mappedPaths;
        } else {
            map = resolvedMappedPaths;
        }

        for (String key : map.keySet()) {
            Set<String> selector = map.get(key).stream().map(Path::getTagSelector).filter(s -> !s.matches(".*> a$")).collect(Collectors.toSet());

            if (!selector.isEmpty()) {
                selectorMap.put(key, selector);
            }
        }
    }
}
