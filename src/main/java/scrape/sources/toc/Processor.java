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
import java.util.List;

/**
 *
 */
public class Processor implements TocProcessor {

    @Override
    public String process(String link) {
        String location = null;
        if (link.contains("gravitytales")) {
            location = GravityNovel.lookUpToc(link);
            // TODO: 21.10.2017 figure that out
        } else {
            Element contentElement = getContentElement(link);

            if (contentElement != null) {
                VolumeSearcher volumeSearcher = new VolumeSearcher(link, contentElement);
                List<String> volumeTocs = volumeSearcher.getVolumeTocs();

                if (volumeTocs.isEmpty()) {
                    ChapterSearcher chapterSearcher = new ChapterSearcher();
                    location = chapterSearcher.getChapterToc(contentElement);
                } else {
//                    Element maxToc = volumeTocs.stream().max(Comparator.comparingInt(element -> element.children().size())).orElse(null);
                    // TODO: 02.11.2017 save toc
                    // TODO: 02.11.2017 return location
                    location = "";
                }
            }
        }
        checkForFurtherChapters(location);
        return location;
    }

    private void checkForFurtherChapters(String tocLocation) {
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
