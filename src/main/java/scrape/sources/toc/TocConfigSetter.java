package scrape.sources.toc;

import org.jsoup.nodes.Document;
import scrape.sources.ConfigSetter;

/**
 *
 */
public class TocConfigSetter extends ConfigSetter {

    private final TocConfig config;
    private final Document document;

    public TocConfigSetter(TocConfig config, Document document) {
        this.config = config;
        this.document = document;
    }

    @Override
    public boolean setConfigs() {
        return false;
    }
}
