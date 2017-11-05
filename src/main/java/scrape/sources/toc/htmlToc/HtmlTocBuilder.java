package scrape.sources.toc.htmlToc;

import Enterprise.misc.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import scrape.sources.toc.intface.TocBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


/**
 *
 */
public class HtmlTocBuilder extends HtmlAbstractTocs implements TocBuilder {
    private final Element toc;
    private final String title;
    private Element volume;
    private Element chapters;

    private int volumeCount = 0;

    private int totalChapterCount = 0;
    private int chapterCount = 0;

    private int subChapterCount = 0;

    public HtmlTocBuilder() {
        this("");
    }

    public HtmlTocBuilder(String title) {
        this("", title);
    }

    public HtmlTocBuilder(String baseUri, String title) {
        toc = new Element(Tag.valueOf(tocTag), baseUri);
        this.title = title;
        addTocTitle(title, toc);
        setId(toc, tocId);
    }


    @Override
    public void addSection(String type, boolean isExtra, String title, double index) {
        chapterCount = 0;

        volume = new Element(childrenContainerTag);

        Element volumeTitleElement = new Element(volumeTag).text(title);

        volumeTitleElement.attr(volumeId, String.valueOf(++volumeCount));

        toc.appendChild(volumeTitleElement);
        volumeTitleElement.appendChild(volume);
    }

    @Override
    public void addChapter(String title, String globalSrc, double index, boolean isExtra) {
        addChapter(title, globalSrc, "", index, isExtra);
    }

    @Override
    public void addChapter(String title, String globalSrc, String localSrc, double index, boolean isExtra) {
        Element chapterTitleElement = new Element(chapterTag);

        chapterTitleElement.text(title);

        String chapterElementTitle = getChapterElementTitle(++chapterCount);
        setTitle(chapterTitleElement, chapterElementTitle);

        chapterTitleElement.attr(chapterIdAttr, String.valueOf(++totalChapterCount));

        chapters = new Element(childrenContainerTag);
        subChapterCount = 0;

        setGlobalLink(globalSrc, chapterTitleElement);
        setLocalLink(localSrc, chapterTitleElement);

        if (volume == null) {
            toc.appendChild(chapterTitleElement);
            chapterTitleElement.appendChild(chapters);
        } else {
            volume.appendChild(chapterTitleElement);
            chapterTitleElement.appendChild(chapters);
        }
    }


    @Override
    public void addSubChapter(String title, String globalSrc, String localSrc, double index, boolean isExtra) {
        Objects.requireNonNull(chapters);
        Element subChapter = new Element(subChapterTag);

        subChapter.attr(subChapterIdAttr, String.valueOf(++subChapterCount)).text(title);
        setLocalLink(localSrc, subChapter);
        setGlobalLink(globalSrc, subChapter);

        subChapter.attr("", String.valueOf(index));
        subChapter.attr("", String.valueOf(isExtra));

        chapters.appendChild(subChapter);
    }

    @Override
    public void addSubChapter(String title, String globalSrc, double index, boolean isExtra) {
        addSubChapter(title, globalSrc, "", index, isExtra);
    }

    @Override
    public String build() {
        if (!toc.children().isEmpty()) {
            toc.select(":empty").remove();
        }
        Document document = Jsoup.parse(toc.outerHtml(), toc.baseUri());
        String workDir = FileUtils.workDirectory();
        String tocDir = workDir + "/novel/toc";

        String[] split = document.outerHtml().split("\n");
        String location;
        try {
            location = tocDir + "/" + title + ".html";
            org.apache.commons.io.FileUtils.writeLines(new File(location), "UTF-8", Arrays.asList(split));
            for (String s : split) {
                System.out.println(s);
            }
        } catch (IOException e) {
            return "";
        }
        return location;
    }

    private void setGlobalLink(String src, Element element) {

    }

    private void setLocalLink(String src, Element element) {
    }

    private void addTocTitle(String baseUri, Element toc) {
        Element title = new Element(creationTitleTag);
        title.attr("id", creationTitleId);
        title.text(baseUri).attr("href", baseUri);
        toc.appendChild(title);
    }

    private void setId(Element element, String id) {
        element.attr("id", id);
    }

    private void setTitle(Element element, String title) {
        element.attr("title", title);
    }

    private void setLink(String link, Element element) {
        element.attr("href", link);
    }
}
