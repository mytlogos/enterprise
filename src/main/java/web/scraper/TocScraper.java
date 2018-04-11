package web.scraper;

import enterprise.data.intface.Creation;
import org.jsoup.nodes.Element;
import scrape.pages.PageConfig;
import scrape.sources.Source;
import scrape.sources.novel.GravityAdapter;
import scrape.sources.novel.QidianAdapter;
import scrape.sources.novel.WebAdapter;
import scrape.sources.novel.toc.TocConfig;
import scrape.sources.novel.toc.structure.TableOfContent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public abstract class TocScraper extends StandardScraper<TableOfContent> {
    private static Map<String, WebAdapter> adapterMap = new HashMap<>();

    static {
        //initiates the adapter for each possible key
        adapterMap.put("webnovel", new QidianAdapter());
        adapterMap.put("gravitynovel", new GravityAdapter());
    }

    private final Creation creation;
    private final boolean initToc;

    public TocScraper(Element root, Source source, boolean clean, Creation creation) {
        super(root, source, clean);
        Objects.requireNonNull(creation, "creation is null!");
        this.creation = creation;
        initToc = !source.getConfig(new TocConfig()).isInit();
    }

    public TocScraper(Source source, Creation creation) throws IOException {
        super(source);
        Objects.requireNonNull(creation, "creation is null!");
        this.creation = creation;
        initToc = !source.getConfig(new TocConfig()).isInit();
    }

    @Override
    public TableOfContent scrape() {
        initiate();

        final String sourceName = source.getSourceName();

        //check if an adapter is available for this source
        for (Map.Entry<String, WebAdapter> entry : adapterMap.entrySet()) {
            if (sourceName.contains(entry.getKey())) {
                final WebAdapter adapter = entry.getValue();
                adapter.setCreation(creation);
                return adapter.getToc();
            }
        }

        //assume toc link is unknown
        final String navigationQuery = source.getConfig(new PageConfig()).getNavigation();
        final TocConfig tocConfig = source.getConfig(new TocConfig());

        final Element navigation = root.selectFirst(navigationQuery);

        return null;
    }

    @Override
    protected void initiate() {
        super.initiate();

        if (initToc) {

        }
    }
}
