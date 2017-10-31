package scrape.sources.toc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class TocReader extends AbstractTocs {
    private final Element toc;

    public TocReader(Element toc) {
        this.toc = toc;
    }

    public TocReader(String url) throws IOException {
        Document parse = Jsoup.parse(new File(url), "UTF-8");
        Element body = parse.body();
        if (body != null) {
            this.toc = body;
        } else {
            throw new IOException();
        }
    }

    public Node read() {
        Elements select = toc.select(creationTitleTag + "#" + creationTitleId);
        Node result;
        if (!select.isEmpty()) {
            result = new Node(select.get(0).text());
        } else {
            result = new Node("");
        }

        if (hasVolumes()) {
            result.addChildren(getVolumeNodes());
        } else if (hasChapters()) {
            result.addChildren(getChapterNodes(toc));
        }
        return result;
    }

    private List<Node> getVolumeNodes() {
        Elements volumes = getVolumes();
        return volumes.stream().map(this::createVolumeNode).collect(Collectors.toList());
    }

    private Node createVolumeNode(Element volume) {
        Elements select = volume.select(childrenContainerTag + ":not(:empty)");
        Node node = new Node(volume.ownText());

        if (!select.isEmpty()) {
            node.addChildren(getChapterNodes(select.first()));
        }
        return node;
    }

    private List<Node> getChapterNodes(Element element) {
        Elements select = element.select("[" + chapterIdAttr + "]");
        return select.stream().map(this::createChapterNode).collect(Collectors.toList());
    }

    private Node createChapterNode(Element chapter) {
        Node node = new Node(chapter.ownText());

        if (has(chapter, subChapterIdAttr)) {
            node.addChildren(getSubChapterNodes(chapter));
        }
        return node;
    }

    private List<Node> getSubChapterNodes(Element chapter) {
        Elements select = chapter.select("[" + subChapterIdAttr + "]");
        return select.stream().map(element -> new Node(element.ownText())).collect(Collectors.toList());
    }


    public boolean hasVolumes() {
        return has(volumeTag);
    }

    public boolean hasChapters() {
        return has(chapterTag);
    }

    public boolean hasSubChapters() {
        return has(subChapterTag);
    }

    public Elements getVolumes() {
        return getVolumes(toc);
    }

    private Elements getVolumes(Element element) {
        return getSelect(element, volumeTag);
    }

    public Elements getChapters() {
        return getChapters(toc);
    }

    private Elements getChapters(Element volume) {
        return getSelect(volume, chapterTag);
    }

    public Elements getSubChapters() {
        return getSubChapters(toc);
    }

    public Elements getSubChapters(Element chapter) {
        return getSelect(chapter, subChapterTag);
    }

    private Elements getSelect(Element chapter, String query) {
        return chapter.select(query);
    }

    private boolean has(Element element, String cssQuery) {
        return element.is(":has(" + cssQuery + ")");
    }

    private boolean has(String cssQuery) {
        return has(toc, cssQuery);
    }
}
