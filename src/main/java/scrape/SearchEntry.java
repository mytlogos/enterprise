package scrape;

import Enterprise.data.intface.Creation;
import scrape.sources.Source;

/**
 *
 */
public class SearchEntry {
    protected Creation creationKey;
    protected Source source;

    public SearchEntry(Creation creation, Source source) {
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
