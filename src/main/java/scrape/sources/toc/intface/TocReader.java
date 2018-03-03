package scrape.sources.toc.intface;

import scrape.Reader;
import scrape.sources.toc.structure.CreationRoot;

/**
 *
 */
public interface TocReader extends Reader<CreationRoot> {
    <E> CreationRoot read(E toc);
}
