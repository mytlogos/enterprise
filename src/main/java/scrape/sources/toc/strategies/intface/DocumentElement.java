package scrape.sources.toc.strategies.intface;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.function.Function;

/**
 *
 */
public interface DocumentElement extends Function<Document, Element> {

}
