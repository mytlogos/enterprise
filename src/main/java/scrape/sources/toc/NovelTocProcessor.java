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
import scrape.sources.toc.structure.CreationRoot;
import scrape.sources.toc.structure.xmlToc.XmlTocBuilder;

import java.io.IOException;

/**
 *
 */
public class NovelTocProcessor implements TocProcessor {

    @Override
    public CreationRoot process(String link) {
        CreationRoot root = null;
        if (link.contains("gravitytales")) {
            root = GravityNovel.lookUpToc(link);
            // TODO: 21.10.2017 figure that out
        } else {
            Element contentElement = getContentElement(link);

            if (contentElement != null) {
                // TODO: 05.11.2017 do the title thing
                root = new VolumeSearcher().search(contentElement, new XmlTocBuilder(""));

                if (root == null) {
                    // TODO: 05.11.2017 do the title thing
                    root = new ChapterSearcher().search(contentElement, new XmlTocBuilder(""));
                }
            }
        }
        checkForFurtherPortions(root);
        return root;
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

    private void checkForFurtherPortions(CreationRoot toc) {
        // TODO: 21.10.2017 check for further chapters with chapterpagination
    }
}
