package scrape.sources.toc.htmlToc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.toc.CreationRoot;
import scrape.sources.toc.intface.Portion;
import scrape.sources.toc.intface.Section;
import scrape.sources.toc.intface.SubPortion;
import scrape.sources.toc.intface.TocReader;
import scrape.sources.toc.novel.Chapter;
import scrape.sources.toc.novel.NovelSection;
import scrape.sources.toc.novel.SubChapter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class HtmlTocReader extends HtmlAbstractTocs implements TocReader {
    private Element toc;

    private HtmlTocReader() {
    }

    public static HtmlTocReader get() {
        return new HtmlTocReader();
    }

    @Override
    public CreationRoot read(String path) {
        try {
            ready(path);
        } catch (IOException e) {
            return new CreationRoot("N/A");
        }

        return getCreationRoot();
    }

    private void ready(String url) throws IOException {
        Document parse = Jsoup.parse(new File(url), "UTF-8");
        Element body = parse.body();
        if (body != null) {
            this.toc = body;
        } else {
            throw new IOException();
        }
    }

    private CreationRoot getCreationRoot() {
        Elements select = toc.select(creationTitleTag + "#" + creationTitleId);
        CreationRoot result;
        if (!select.isEmpty()) {
            result = new CreationRoot(select.get(0).text());
        } else {
            result = new CreationRoot("N/A");
        }

        if (hasVolumes()) {
            result.addAllSections(getVolumeNodes());
        } else if (hasChapters()) {
            result.addAllPortions(getChapterNodes(toc));
        }
        return result;
    }

    private List<Section> getVolumeNodes() {
        Elements volumes = getVolumes();
        return volumes.stream().map(this::createVolumeNode).collect(Collectors.toList());
    }

    private Section createVolumeNode(Element volume) {
        Elements select = volume.select(childrenContainerTag + ":not(:empty)");
        // TODO: 01.11.2017 read exact name from toc
        Section node = new NovelSection(volume.ownText(), "Volume");

        if (!select.isEmpty()) {
            node.addAll(getChapterNodes(select.first()));
        }
        return node;
    }

    private List<Portion> getChapterNodes(Element element) {
        Elements select = element.select("[" + chapterIdAttr + "]");
        return select.stream().map(this::createChapterNode).collect(Collectors.toList());
    }

    private Portion createChapterNode(Element chapter) {
        // TODO: 01.11.2017 read exact name from toc
        Portion node = new Chapter(chapter.ownText());

        if (has(chapter, subChapterIdAttr)) {
            node.addAll(getSubChapterNodes(chapter));
        }

        return node;
    }

    private List<SubPortion> getSubChapterNodes(Element chapter) {
        Elements select = chapter.select("[" + subChapterIdAttr + "]");
        // TODO: 01.11.2017 read exact name from toc
        return select.stream().map(element -> new SubChapter(element.ownText())).collect(Collectors.toList());
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
