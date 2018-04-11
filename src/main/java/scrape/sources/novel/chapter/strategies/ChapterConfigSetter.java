package scrape.sources.novel.chapter.strategies;

import enterprise.data.Default;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.sources.ConfigSetter;
import scrape.sources.novel.chapter.ChapterConfigs;
import scrape.sources.novel.chapter.strategies.impl.ChapterPagination;
import scrape.sources.novel.chapter.strategies.impl.ChapterTitleFilter;
import scrape.sources.novel.chapter.strategies.impl.PageContentFilter;
import scrape.sources.novel.chapter.strategies.intface.PageContentElement;
import scrape.sources.novel.chapter.strategies.intface.PaginationElement;
import scrape.sources.posts.strategies.PostWrapper;
import scrape.sources.posts.strategies.intface.ElementFilter;
import scrape.sources.posts.strategies.intface.FilterElement;
import scrape.sources.posts.strategies.intface.TitleElement;

import java.util.logging.Level;

/**
 *
 */
public class ChapterConfigSetter extends ConfigSetter {

    private ChapterConfigs configs;
    private Document document;

    public ChapterConfigSetter(ChapterConfigs configs, Document document) {
        this.configs = configs;
        this.document = document;
    }

    public ChapterConfigSetter() {

    }

    @Override
    public void setConfigs() {
        PostWrapper wrapper = PostWrapper.tryAll(document);
        configs.setWrapper(wrapper);

        if (wrapper != null) {
            Element contentWrapper = wrapper.apply(document);

            PageContentElement chapterElement = tryAllPageContent(contentWrapper);
            TitleElement titleElement = tryAllTitles(contentWrapper);
            PaginationElement paginationElement = tryAllPagination(contentWrapper);

            if (chapterElement != null && titleElement != null && paginationElement != null) {
                configs.setContent(chapterElement);
                configs.setTitle(titleElement);
                configs.setPagination(paginationElement);

                configs.setInit();
            } else {
                Default.LOGGER.log(Level.WARNING, document.baseUri() + " is not supported");
            }
        } else {
        }
    }

    public PageContentElement tryAllPageContent(Element body) {
        return tryAll(body, new PageContentFilter());
    }

    private TitleElement tryAllTitles(Element body) {
        return tryAll(body, new ChapterTitleFilter());
    }

    public PaginationElement tryAllPagination(Element body) {
        return tryAll(body, new ChapterPagination());
    }

    private <E extends FilterElement> E tryAll(Element body, ElementFilter<E> filter) {
        return getFirstFilter(body, filter.getFilter());
    }
}
