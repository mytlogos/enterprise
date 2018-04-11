package scrape.sources.novel.toc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.Scraper;
import scrape.sources.novel.GravityAdapter;
import scrape.sources.novel.chapter.strategies.ChapterConfigSetter;
import scrape.sources.novel.chapter.strategies.intface.PageContentElement;
import scrape.sources.novel.toc.novel.ChapterSearcher;
import scrape.sources.novel.toc.novel.VolumeSearcher;
import scrape.sources.novel.toc.strategies.intface.TocProcessor;
import scrape.sources.novel.toc.structure.TableOfContent;
import scrape.sources.novel.toc.structure.xmlToc.XmlTocBuilder;
import scrape.sources.posts.strategies.PostWrapper;

import java.io.IOException;

/**
 *
 */
public class NovelTocProcessor implements TocProcessor {

    @Override
    public TableOfContent process(String link) {
        TableOfContent root = null;
        if (link.contains("gravitytales")) {
            root = GravityAdapter.lookUpToc(link);
            // TODO: 21.10.2017 figure that out
        } else {
            Element contentElement = getContentElement(link);

            if (contentElement != null) {
                // TODO: 05.11.2017 do the title thing
                root = new VolumeSearcher().search(contentElement, new XmlTocBuilder("test"));

                if (root == null) {
                    // TODO: 05.11.2017 do the title thing
                    root = new ChapterSearcher().search(contentElement, new XmlTocBuilder("test"));
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

        PostWrapper contentWrapper = PostWrapper.tryAll(document);

        if (contentWrapper == null) {
            return null;
        } else {
            Element wrapperElement = contentWrapper.apply(document);
            PageContentElement pageContent = new ChapterConfigSetter().tryAllPageContent(wrapperElement);

            return pageContent != null ? pageContent.apply(wrapperElement) : wrapperElement;
        }


    }

    private void checkForFurtherPortions(TableOfContent toc) {
        // TODO: 21.10.2017 check for further chapters with chapterpagination
    }
}
