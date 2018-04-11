package scrape.sources.novel.toc.strategies.intface;

import scrape.sources.novel.toc.structure.TableOfContent;

/**
 *
 */
public interface TocProcessor {
    TableOfContent process(String link);
}
