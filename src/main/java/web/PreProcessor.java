package web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class PreProcessor {
    private Whitelist whitelist;
    private List<String> removeSelector = new ArrayList<>();

    public PreProcessor() {
        removeSelector.add("[class~=comment|share], [id~=comment|share]");
    }

    public PreProcessor addRemoveSelector(String cssSelector) {
        Objects.requireNonNull(cssSelector);

        if (cssSelector.isEmpty()) {
            throw new IllegalArgumentException("empty selector");
        }
        removeSelector.add(cssSelector);
        return this;
    }

    public Document clean(Document document) {
        return clean(document.body());
    }

    public Document clean(Element element) {
        Whitelist whitelist = getWhiteList();

        StringBuilder builder = new StringBuilder();

        for (Iterator<String> iterator = removeSelector.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        element.select(builder.toString()).remove();

        Elements select = getEmptyElements(element);

        while (!select.isEmpty()) {
            select.remove();
            select = getEmptyElements(element);
        }

        String cleaned = Jsoup.clean(element.outerHtml(), element.baseUri(), whitelist);
        return Jsoup.parse(cleaned, element.baseUri());
    }

    private Whitelist getWhiteList() {
        if (whitelist == null) {
            whitelist = Whitelist
                    .relaxed()
                    .addAttributes(":all", "id", "class", "datetime",
                            "rel", "data-toggle", "data-timestamp",
                            "datetime", "title", "role", "data-target"
                    )
                    .addProtocols("a", "href", "#")

                    .addTags("time", "main", "article", "section",
                            "footer", "aside", "figure", "header",
                            "nav", "details", "summary"
                    );
        }
        return whitelist;
    }

    private Elements getEmptyElements(Element element) {
        return element.select(":empty:not(body), :not(:matches(\\S+)):not(body)");
    }

    public Document cleanWhole(Document document) {
        Whitelist whitelist = getWhiteList();

        StringBuilder builder = new StringBuilder();

        for (Iterator<String> iterator = removeSelector.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        document.select(builder.toString()).remove();

        Elements select = getEmptyElements(document);

        while (!select.isEmpty()) {
            select.remove();
            select = getEmptyElements(document.body());
        }

        String cleaned = Jsoup.clean(document.outerHtml(), document.baseUri(), whitelist);
        return Jsoup.parse(cleaned, document.baseUri());
    }

}
