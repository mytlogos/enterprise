package scrape.sources.novel.toc.novel;

import org.jsoup.nodes.Element;
import scrape.sources.novel.toc.intface.TocBuilder;
import scrape.sources.novel.toc.structure.TableOfContent;

/**
 *
 */
abstract class Searcher {
    public abstract TableOfContent search(Element wrapperElement, TocBuilder builder);
}
