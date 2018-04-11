package scrape.sources.novel.toc.intface;

import scrape.Reader;
import scrape.sources.novel.toc.structure.TableOfContent;

/**
 *
 */
public interface TocReader extends Reader<TableOfContent> {
    <E> TableOfContent read(E toc);
}
