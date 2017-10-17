package scrape.sources.toc.strategies.intface;

import scrape.sources.posts.strategies.intface.FilterElement;

/**
 *
 */
public interface TocElement extends FilterElement {
    void setKeyWord(String keyWord);
}
