package scrape.sources.toc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;


/**
 *
 */
public class TocBuilder extends AbstractTocs {
    private Element toc;
    private Element volume;
    private Element chapters;

    private int volumeCount = 0;
    private int totalChapterCount = 0;
    private int chapterCount = 0;
    private int subChapterCount = 0;

    public TocBuilder() {
        this("");
    }

    public TocBuilder(String baseUri) {
        toc = new Element(Tag.valueOf(tocTag), baseUri);
        addTocTitle(baseUri, toc);
        setId(toc, tocId);
    }

    private void addTocTitle(String baseUri, Element toc) {
        // TODO: 31.10.2017 add title
        Element title = new Element(creationTitleTag);
        title.attr("id", creationTitleId);
        title.text(baseUri).attr("href", baseUri);
        toc.appendChild(title);
    }


    public void addVolume(String volumeTitle) {
        chapterCount = 0;

        volume = new Element(childrenContainerTag);

        Element volumeTitleElement = new Element(volumeTag).text(volumeTitle);

        volumeTitleElement.attr(volumeId, String.valueOf(++volumeCount));

        toc.appendChild(volumeTitleElement);
        volumeTitleElement.appendChild(volume);
    }

    public void addChapter(String chapterTitle) {
        addChapter(chapterTitle, "");
    }

    public void addChapter(String chapterTitle, String link) {
        Element chapterTitleElement = new Element(chapterTag)
                .text(chapterTitle);

        String chapterElementTitle = getChapterElementTitle(++chapterCount);
        setTitle(chapterTitleElement, chapterElementTitle);

        chapterTitleElement.attr(chapterIdAttr, String.valueOf(++totalChapterCount));

        chapters = new Element(childrenContainerTag);
        subChapterCount = 0;

        if (!(link == null || link.isEmpty())) {
            setLink(link, chapterTitleElement);
        }

        if (volume == null) {
            toc.appendChild(chapterTitleElement);
            chapterTitleElement.appendChild(chapters);
        } else {
            volume.appendChild(chapterTitleElement);
            chapterTitleElement.appendChild(chapters);
        }
    }


    public void addSubChapter(String subChapterTitle, String link) {
        Element subChapter = new Element(subChapterTag);

        subChapter.attr(subChapterIdAttr, String.valueOf(++subChapterCount)).text(subChapterTitle);
        setLink(link, subChapter);

        if (chapters == null) {
            throw new IllegalStateException();
        } else {
            chapters.appendChild(subChapter);
        }
    }

    public void addExtra() {

    }

    public void addInterlude() {

    }

    public Element getToc() {
        if (!toc.children().isEmpty()) {
            toc.select(":empty").remove();
        }
        return Jsoup.parse(toc.outerHtml(), toc.baseUri());
    }

    private Element setId(Element element, String id) {
        return element.attr("id", id);
    }

    private void setTitle(Element element, String title) {
        element.attr("title", title);
    }

    private void setLink(String link, Element element) {
        element.attr("href", link);
    }
}
