package scrape.sources.posts.strategies.intface;

import org.jsoup.nodes.Element;

import java.util.function.Function;

/**
 *
 */
public interface FilterElement extends Function<Element, Element>, Filter {
}
