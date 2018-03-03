package scrape;

import enterprise.data.intface.Creation;
import scrape.sources.Source;

/**
 *
 */
public class SearchEntry {
    protected final Source source;
    final Creation creationKey;

    protected SearchEntry(Creation creation, Source source) {
        this.creationKey = creation;
        this.source = source;
    }

    public Creation getCreation() {
        return creationKey;
    }

    public Source getSource() {
        return source;
    }
}
