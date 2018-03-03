package scrape.sources.toc.strategies.intface;

import scrape.sources.toc.structure.CreationRoot;

/**
 *
 */
public interface TocProcessor {
    CreationRoot process(String link);
}
