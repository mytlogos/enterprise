package scrape.sources.novel.toc;

import gorgon.external.GorgonEntry;
import scrape.Config;
import scrape.sources.novel.toc.strategies.intface.HeaderElement;
import scrape.sources.novel.toc.strategies.intface.TocElement;

/**
 *
 */
public class TocConfig extends Config {
    private HeaderElement headerElement;
    private TocElement tocElement;

    public HeaderElement getHeaderElement() {
        return headerElement;
    }

    public void setHeaderElement(HeaderElement headerElement) {
        this.headerElement = headerElement;
    }

    public TocElement getTocElement() {
        return tocElement;
    }

    public void setTocElement(TocElement tocElement) {
        this.tocElement = tocElement;
    }

    @Override
    public String getKey() {
        return "toc";
    }

    @Override
    public int compareTo(GorgonEntry o) {
        return 0;
    }
}
