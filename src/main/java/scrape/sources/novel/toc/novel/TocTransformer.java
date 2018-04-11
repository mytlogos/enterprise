package scrape.sources.novel.toc.novel;

import org.jsoup.nodes.Document;
import scrape.sources.novel.toc.structure.Leaf;
import scrape.sources.novel.toc.structure.TableOfContent;

/**
 * Transforms the Table of Contents from {@link TableOfContent}
 * into a valid html Table of Contents for the
 * epub-converter tool of Calibre.
 * <p>
 * The {@link Leaf} Nodes of the Table of Contents
 * need to have an valid local path to the Files, else
 * it will be skipped.
 */
class TocTransformer {

    public Document transform(TableOfContent root) {
        // TODO: 03.11.2017 implement
        return new Document("");
    }
}
