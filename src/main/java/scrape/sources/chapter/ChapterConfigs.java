package scrape.sources.chapter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import scrape.ScrapeConfigs;
import scrape.sources.chapter.strategies.intface.PageContentElement;
import scrape.sources.chapter.strategies.intface.PaginationElement;
import scrape.sources.posts.strategies.ContentWrapper;
import scrape.sources.posts.strategies.intface.TitleElement;

/**
 *
 */
public class ChapterConfigs implements ScrapeConfigs {
    private ObjectProperty<ContentWrapper> wrapper = new SimpleObjectProperty<>();
    private ObjectProperty<PageContentElement> content = new SimpleObjectProperty<>();
    private ObjectProperty<TitleElement> title = new SimpleObjectProperty<>();
    private ObjectProperty<PaginationElement> pagination = new SimpleObjectProperty<>();
    private boolean init = false;

    public ContentWrapper getWrapper() {
        return wrapper.get();
    }

    public void setWrapper(ContentWrapper wrapper) {
        this.wrapper.set(wrapper);
    }

    public PageContentElement getContent() {
        return content.get();
    }

    public void setContent(PageContentElement content) {
        this.content.set(content);
    }

    public TitleElement getTitle() {
        return title.get();
    }

    public void setTitle(TitleElement title) {
        this.title.set(title);
    }

    public PaginationElement getPagination() {
        return pagination.get();
    }

    public void setPagination(PaginationElement pagination) {
        this.pagination.set(pagination);
    }

    public boolean isInit() {
        return init;
    }

    @Override
    public void setInit() {
        init = true;
    }

    @Override
    public String toString() {
        return "ChapterConfigs:\nWrapper: " + wrapper.get() + "\nContent: " + content.get() + "\nTitle: " + title.get() + "\nPagination: " + pagination.get();
    }
}
