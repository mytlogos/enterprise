package scrape.sources.toc.structure;

import scrape.sources.toc.structure.intface.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class CreationRoot extends NodeImpl implements Node, Portionable {
    private final Map<String, String> map = new HashMap<>();
    private final String location;

    private Node mode;

    public CreationRoot(String title, String location) {
        super(title, "root");
        this.location = location;
        setRoot(this);
    }

    @Override
    public void add(Portion portion) {
        if (mode == null) {
            mode = portion;
        }
        if (mode instanceof Portion) {
            super.addChild(portion);
        } else {
            throw new IllegalStateException();
        }
    }

    public void add(Section section) {
        if (mode == null) {
            mode = section;
        }
        if (mode instanceof Section) {
            super.addChild(section);
        } else {
            throw new IllegalStateException();
        }
    }

    public void addAllPortions(List<? extends Portion> portions) {
        if (!portions.isEmpty()) {
            if (mode == null) {
                mode = portions.get(0);
            }
            if (mode instanceof Section) {
                super.addChildren(portions);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    public void addAllSections(List<? extends Section> sections) {
        if (!sections.isEmpty()) {
            if (mode == null) {
                mode = sections.get(0);
            }
            if (mode instanceof Section) {
                super.addChildren(sections);
            } else {
                throw new IllegalStateException();
            }
        }
    }

    public void remove(Portion portion) {
        super.removeChild(portion);
    }

    public void remove(Section section) {
        super.removeChild(section);
    }

    public boolean hasSection() {
        return getSectionType() != null;
    }

    public String getSectionType() {
        return map.get(Section.class.getSimpleName());
    }

    public boolean hasPortion() {
        return getPortionType() != null;
    }

    public String getPortionType() {
        return map.get(Portion.class.getSimpleName());
    }

    public boolean hasSubPortion() {
        return getSubPortionType() != null;
    }

    public String getSubPortionType() {
        return map.get(SubPortion.class.getSimpleName());
    }

    public boolean contains(Node node) {
        Objects.requireNonNull(node);

        Class<? extends Node> clazz = getInterface(node);
        return map.containsKey(clazz.getSimpleName());
    }

    public String getLocation() {
        return location;
    }

    public String get(Node node) {
        Objects.requireNonNull(node);

        Class<? extends Node> clazz = getInterface(node);
        return map.get(clazz.getSimpleName());
    }

    public void put(Node node) {
        Objects.requireNonNull(node);

        Class<? extends Node> clazz = getInterface(node);
        map.put(clazz.getSimpleName(), node.getType());
    }

    private Class<? extends Node> getInterface(Node node) {
        Class<? extends Node> clazz = null;
        for (Class<?> aClass : node.getClass().getInterfaces()) {
            if (Node.class.isAssignableFrom(aClass)) {
                clazz = (Class<? extends Node>) aClass;
                break;
            }
        }
        return clazz;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("root: ");
        builder.append(super.toString());

        if (mode instanceof Section) {
            List<Section> children = (List<Section>) getChildren();
            for (Section child : children) {
                builder.append("\n\t").append("section: ").append(child.toString());
                printPortions((List<Portion>) child.getChildren(), builder);
            }
        } else {
            printPortions((List<Portion>) getChildren(), builder);
        }
        return builder.toString();
    }

    private void printPortions(List<Portion> portions, StringBuilder builder) {
        for (Portion child : portions) {
            builder.append("\n\t\t").append("portion: ").append(child.toString());

            for (SubPortion subChapter : child.getChildren()) {
                builder.append("\n\t\t\t").append("subPortion: ").append(subChapter.toString());
            }
        }
    }

}
