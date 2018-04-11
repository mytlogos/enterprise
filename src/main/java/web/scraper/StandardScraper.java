package web.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import scrape.pages.PageConfig;
import scrape.sources.Source;
import web.PreProcessor;
import web.Scraper;
import web.finder.ScorerUser;
import web.scorer.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 *
 */
public abstract class StandardScraper<E> implements Scraper<E> {
    protected final Element root;
    protected final Source source;
    private final boolean initPage;
    protected ScorerUser user;

    public StandardScraper(Element root, Source source, boolean clean) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(root);
        this.source = source;

        initPage = !source.getConfig(new PageConfig()).isInit();
        if (!clean) {
            this.root = new PreProcessor().clean(root);
        } else {
            this.root = root;
        }
    }

    public StandardScraper(Source source) throws IOException {
        Objects.requireNonNull(source, "source is null!");
        this.root = new PreProcessor().cleanWhole(Jsoup.connect(source.getUrl()).get());
        this.source = source;
        initPage = !source.getConfig(new PageConfig()).isInit();
    }

    @Override
    public E scrape() {
        return null;
    }

    public abstract Scorer getScorer();

    protected void initiate() {
        if (initPage) {
            user = new ScorerUser(root);

            final NavigationScorer navigationScorer = new NavigationScorer();
            final MainScorer mainScorer = new MainScorer();
            final SideBarScorer sideBarScorer = new SideBarScorer();

            user.addScorers(navigationScorer, mainScorer, sideBarScorer).execute();

            final Map<String, TreeMap<Double, List<ElementWrapper>>> scoredMaps = user.getScoredMaps();
        }
    }
}
