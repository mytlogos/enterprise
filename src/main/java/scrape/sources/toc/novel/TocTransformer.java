package scrape.sources.toc.novel;

import org.jsoup.nodes.Document;
import scrape.sources.toc.CreationRoot;
import scrape.sources.toc.Leaf;

/**
 * Transforms the Table of Contents from {@link CreationRoot}
 * into a valid html Table of Contents for the
 * epub-converter tool of Calibre.
 * <p>
 * The {@link Leaf} Nodes of the Table of Contents
 * need to have an valid local path to the Files, else
 * it will be skipped.
 */
public class TocTransformer {

    public Document transform(CreationRoot root) {
        // TODO: 03.11.2017 implement
        return new Document("");
    }
}
