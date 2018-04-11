package web.finder;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import web.PreProcessor;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
abstract class AbstractFinder implements Finder {
    private static final List<String> structureBlockTags = Arrays.asList(
            "title", "section", "nav", "aside", "hgroup", "header", "footer",
            "pre", "div", "figure", "ul", "ol", "li", "body",
            "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th",
            "td", "details", "plaintext", "template", "article", "main"
    );

    final Element start;

    AbstractFinder(Document document) {
        this(new PreProcessor(), document);
    }

    AbstractFinder(PreProcessor preProcessor, Document document) {
        this.start = preProcessor.clean(document).body();
    }

    AbstractFinder(Element start) {
        this.start = start;
    }

    AbstractFinder(Element start, PreProcessor preProcessor) {
        this.start = preProcessor.clean(start);
    }

    boolean isStructureBlock(Element element) {
        return structureBlockTags.contains(element.tagName());
    }
}
