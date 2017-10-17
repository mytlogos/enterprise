package scrape.sources.chapter.strategies;

import Enterprise.misc.Log;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import scrape.sources.ConfigSetter;
import scrape.sources.chapter.ChapterConfigs;
import scrape.sources.chapter.strategies.impl.ChapterContentFilter;
import scrape.sources.chapter.strategies.impl.ChapterPagination;
import scrape.sources.chapter.strategies.impl.ChapterTitleFilter;
import scrape.sources.chapter.strategies.intface.ChapterElement;
import scrape.sources.chapter.strategies.intface.PaginationElement;
import scrape.sources.posts.strategies.ContentWrapper;
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

    private <E extends FilterElement> E tryAll(Element body, ElementFilter<E> filter) {
        return getFirstFilter(body, filter.getFilter());
    }

    private TitleElement tryAllTitles(Element body) {
        return tryAll(body, new ChapterTitleFilter());
    }

    private ChapterElement tryAllChapters(Element body) {
        return tryAll(body, new ChapterContentFilter());
    }

    public PaginationElement tryAllPagination(Element body) {
        return tryAll(body, new ChapterPagination());
    }

    @Override
    public boolean setConfigs() {
        ContentWrapper wrapper = ContentWrapper.tryAll(document);
        configs.setWrapper(wrapper);

        if (wrapper != null) {
            Element contentWrapper = wrapper.apply(document);

            ChapterElement chapterElement = tryAllChapters(contentWrapper);
            TitleElement titleElement = tryAllTitles(contentWrapper);
            PaginationElement paginationElement = tryAllPagination(contentWrapper);

            if (chapterElement != null && titleElement != null && paginationElement != null) {
                configs.setContent(chapterElement);
                configs.setTitle(titleElement);
                configs.setPagination(paginationElement);

                configs.setInit();
                return true;
            } else {
                Log.classLogger(this).log(Level.WARNING, document.baseUri() + " is not supported");
                return false;
            }
        } else {
            return false;
        }
    }
}