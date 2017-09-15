package scrape.sources.novels.strategies.intface;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.function.Function;

/**
 *
 */
public interface PostElement extends Function<Element, Elements> {
}
