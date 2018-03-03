package scrape;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import scrape.sources.Source;

import java.io.IOException;

/**
 *
 */
public abstract class Scraper<E extends ScrapeConfigs, R extends SearchEntry> {
    protected E configs;
    protected R search;
    protected Source source;
    protected Document document;

    protected static Document getDocument(String uri) throws IOException {
        return Jsoup.connect(uri).get();
    }

    public static Document getCleanDocument(String uri) throws IOException {
        return cleanDoc(Jsoup.connect(uri).maxBodySize(0).get());
    }

    public static Document cleanDoc(Document document) {
        Whitelist whitelist = Whitelist.relaxed();
        whitelist.addAttributes(":all", "id", "class", "style");
        whitelist.addAttributes("div", "class", "id");
        whitelist.addAttributes("a", "id", "datetime", "rel", "data-toggle");
        whitelist.addAttributes("p", "data-timestamp");
        whitelist.addAttributes("time", "datetime");
        whitelist.addAttributes("abbr", "title");
        whitelist.addAttributes("span", "title");
        whitelist.addAttributes("em", "data-timestamp");
        whitelist.addAttributes("nav", "role");
        whitelist.addTags("time");
        whitelist.addTags("style");
        whitelist.addTags("main");
        whitelist.addTags("article");
        whitelist.addTags("section");
        whitelist.addAttributes("button", "data-target");
        document.getElementsByAttributeValueContaining("class", "comment").remove();
        document.getElementsByAttributeValueContaining("class", "share").remove();
        document.getElementsByAttributeValueContaining("id", "comment").remove();
        document.getElementsByAttributeValueContaining("id", "share").remove();

        String cleaned = Jsoup.clean(document.outerHtml(), document.baseUri(), whitelist);
        return Jsoup.parse(cleaned, document.baseUri());
    }
}
