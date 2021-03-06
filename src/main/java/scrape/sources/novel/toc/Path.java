package scrape.sources.novel.toc;

import org.jsoup.nodes.Element;

import java.util.LinkedList;

/**
 *
 */
public class Path implements Cloneable, Comparable<Path> {
    private final LinkedList<Element> path;

    public Path() {
        path = new LinkedList<>();
    }

    private Path(LinkedList<Element> path) {
        this.path = new LinkedList<>(path);
    }

    public int getPathSize() {
        return path.size();
    }

    public Path addParent(Element element) {
        path.addFirst(element);
        return this;
    }

    public Path addChild(Element element) {
        path.addLast(element);
        return this;
    }

    public Element getLast() {
        return !path.isEmpty() ? path.getLast() : null;
    }

    public Element getFirst() {
        return !path.isEmpty() ? path.getFirst() : null;
    }

    public boolean isEmpty() {
        return path.isEmpty();
    }

    public void resolve(LinkedList<Element> basePath) {
        int min = Math.min(basePath.size(), path.size());
        Path thisClone = this.clone();
        for (int i = 0; i < min; i++) {
            if (thisClone.get(i) == basePath.get(i)) {
                path.removeFirst();
            }
        }
    }

    public Element get(int index) {
        return path.get(index);
    }

    public Path resolve(Path basePath) {
        int min = Math.min(basePath.path.size(), path.size());
        Path thisClone = this.clone();
        for (int i = 0; i < min; i++) {
            if (thisClone.get(i) == basePath.path.get(i)) {
                path.removeFirst();
            }
        }
        return this;
    }

    public String getTagSelector() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {

            builder.append(path.get(i).tagName());

            if (i < path.size() - 1) {
                builder.append(" > ");
            }
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Path o) {
        if (o == null) return -1;
        if (o == this) return 0;

        int compare = this.path.size() - o.path.size();

        if (compare == 0) {
            for (int i = 0; i < path.size(); i++) {
                if (this.path.get(i) == o.path.get(i)) {
                    compare = 0;
                } else {
                    compare = -1;
                }
            }
        }
        return compare;
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        return getPath().equals(path.getPath());
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Path clone() {
        // TODO: 16.10.2017 maybe do sth else
        return new Path(path);
    }

    public LinkedList<Element> getPath() {
        return path;
    }
}
