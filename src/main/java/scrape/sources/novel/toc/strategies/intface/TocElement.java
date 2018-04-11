package scrape.sources.novel.toc.strategies.intface;

import scrape.sources.posts.strategies.intface.FilterElement;

/**
 *
 */
public interface TocElement extends FilterElement {
    void setKeyWord(String keyWord);
}
