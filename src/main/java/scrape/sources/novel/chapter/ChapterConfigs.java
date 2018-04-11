package scrape.sources.novel.chapter;

import gorgon.external.GorgonEntry;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import scrape.Config;
import scrape.sources.novel.chapter.strategies.intface.PageContentElement;
import scrape.sources.novel.chapter.strategies.intface.PaginationElement;
import scrape.sources.posts.strategies.PostWrapper;
import scrape.sources.posts.strategies.intface.TitleElement;

/**
 *
 */
public class ChapterConfigs extends Config {
    private final ObjectProperty<PostWrapper> wrapper = new SimpleObjectProperty<>();
    private final ObjectProperty<PageContentElement> content = new SimpleObjectProperty<>();
    private final ObjectProperty<TitleElement> title = new SimpleObjectProperty<>();
    private final ObjectProperty<PaginationElement> pagination = new SimpleObjectProperty<>();

    public PostWrapper getWrapper() {
        return wrapper.get();
    }

    public void setWrapper(PostWrapper wrapper) {
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

    @Override
    public String getKey() {
        return "novel-chapter";
    }

    @Override
    public String toString() {
        return "ChapterConfigs:\nWrapper: " + wrapper.get() + "\nContent: " + content.get() + "\nTitle: " + title.get() + "\nPagination: " + pagination.get();
    }

    @Override
    public int compareTo(GorgonEntry o) {
        return 0;
    }
}
