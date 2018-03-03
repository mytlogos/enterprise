package scrape.sources.toc.novel;

import org.jsoup.nodes.Element;
import scrape.sources.toc.intface.TocBuilder;
import scrape.sources.toc.structure.CreationRoot;

/**
 *
 */
abstract class Searcher {
    public abstract CreationRoot search(Element wrapperElement, TocBuilder builder);
}
