package scrape.sources.chapter;

import org.jsoup.nodes.Element;

import java.util.LinkedList;

/**
 *
 */
public class Path implements Cloneable, Comparable<Path> {
    private LinkedList<Element> path;

    public Path() {
        path = new LinkedList<>();
    }

    public Path(LinkedList path) {
        this.path = new LinkedList<>(path);
    }

    public int getPathSize() {
        return path.size();
    }

    public Element get(int index) {
        return path.get(index);
    }

    public LinkedList<Element> getPath() {
        return path;
    }

    public void addParent(Element element) {
        path.addFirst(element);
    }

    public void addChild(Element element) {
        path.addLast(element);
    }

    public Element getLast() {
        return !path.isEmpty() ? path.getLast() : null;
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

    public void resolve(Path basePath) {
        int min = Math.min(basePath.path.size(), path.size());
        Path thisClone = this.clone();
        for (int i = 0; i < min; i++) {
            if (thisClone.get(i) == basePath.path.get(i)) {
                path.removeFirst();
            }
        }
    }

    public Path clone() {
        // TODO: 16.10.2017 maybe do sth else
        return new Path(path);
    }

    String getTagSelector() {
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
}
