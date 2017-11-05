package scrape.sources.chapter;

import scrape.sources.toc.NodeType;

/**
 *
 */
public enum NodeTypes implements NodeType {
    VOLUME("Abschnitt"),
    CHAPTER("Kapitel"),
    SUBCHAPTER("Part"),;

    private final String text;

    NodeTypes(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
