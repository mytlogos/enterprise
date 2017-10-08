package scrape;

import org.jsoup.nodes.Element;
import scrape.sources.posts.strategies.intface.FilterElement;

/**
 *
 */
public abstract class Formatter {
    protected void setPart(Element element, Element post, FilterElement filterElement) {
        if (filterElement != null) {
            Element content = filterElement.apply(element);
            if (content != null) {
                post.appendChild(content);
            }
        }
    }

    protected String stringByIdAttr(Element element, String id, String attr) {
        Element selected = element.getElementById(id);
        if (selected == null) {
            return "";
        } else {
            return selected.attr(attr);
        }
    }

    protected String stringByIdText(Element element, String id) {
        Element selected = element.getElementById(id);
        if (selected == null) {
            return "";
        } else {
            return selected.text();
        }
    }

    protected Element elementById(Element element, String id) {
        Element selected = element.getElementById(id);
        if (selected == null) {
            return null;
        } else {
            return selected;
        }
    }
}
