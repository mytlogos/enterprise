package scrape.sources.toc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.Scraper;
import scrape.sources.chapter.GravityNovel;
import scrape.sources.chapter.strategies.ChapterConfigSetter;
import scrape.sources.chapter.strategies.intface.PageContentElement;
import scrape.sources.posts.strategies.ContentWrapper;
import scrape.sources.toc.novel.ChapterSearcher;
import scrape.sources.toc.novel.VolumeSearcher;
import scrape.sources.toc.strategies.intface.TocProcessor;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class Processor implements TocProcessor {

    @Override
    public Element process(String link) {
        Element toc = null;
        if (link.contains("gravitytales")) {
            List<Element> elements = GravityNovel.lookUpToc(link);
            // TODO: 21.10.2017 figure that out
        } else {
            Element contentElement = getContentElement(link);

            if (contentElement != null) {
                VolumeSearcher volumeSearcher = new VolumeSearcher(link, contentElement);
                List<Element> volumeTocs = volumeSearcher.getVolumeTocs();


                if (volumeTocs.isEmpty()) {
                    ChapterSearcher chapterSearcher = new ChapterSearcher();
                    toc = chapterSearcher.getChapterToc(contentElement);
                } else {
                    toc = volumeTocs.stream().max(Comparator.comparingInt(element -> element.children().size())).orElse(null);
                }
            }
        }
        checkForFurtherChapters(toc);
        return toc;
    }

    private void checkForFurtherChapters(Element toc) {
        // TODO: 21.10.2017 check for further chapters with chapterpagination
    }

    private Element getContentElement(String link) {
        Document document = null;
        try {
            document = Scraper.getCleanDocument(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (document == null) {
            return null;
        }

        ContentWrapper contentWrapper = ContentWrapper.tryAll(document);

        if (contentWrapper == null) {
            return null;
        } else {
            Element wrapperElement = contentWrapper.apply(document);
            PageContentElement pageContent = new ChapterConfigSetter().tryAllPageContent(wrapperElement);

            return pageContent != null ? pageContent.apply(wrapperElement) : wrapperElement;
        }


    }
}
