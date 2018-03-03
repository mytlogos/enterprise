package scrape.sources.toc;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import scrape.ScrapeConfigs;
import scrape.sources.toc.strategies.intface.HeaderElement;
import scrape.sources.toc.strategies.intface.TocElement;

/**
 *
 */
public class TocConfig implements ScrapeConfigs {
    private final ObjectProperty<HeaderElement> headerElement = new SimpleObjectProperty<>();
    private final ObjectProperty<TocElement> tocElement = new SimpleObjectProperty<>();

    private boolean init = false;

    public HeaderElement getHeaderElement() {
        return headerElement.get();
    }

    public void setHeaderElement(HeaderElement headerElement) {
        this.headerElement.set(headerElement);
    }

    public TocElement getTocElement() {
        return tocElement.get();
    }

    public void setTocElement(TocElement tocElement) {
        this.tocElement.set(tocElement);
    }

    @Override
    public boolean isInit() {
        return init;
    }

    @Override
    public void setInit() {
        init = true;
    }
}
