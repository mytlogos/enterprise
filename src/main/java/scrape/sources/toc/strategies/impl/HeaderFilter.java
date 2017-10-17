package scrape.sources.toc.strategies.impl;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.toc.strategies.intface.HeaderElement;

import java.util.Collection;

/**
 *
 */
public class HeaderFilter {

    public static HeaderElement tryAll(Document document) {
        for (Headers header : Headers.values()) {
            Element wrapper = header.apply(document);

            if (wrapper != null) {
                return header;
            }
        }

        return null;
    }

    public Collection<HeaderElement> getFilter() {
        return null;
    }

    public enum Headers implements HeaderElement {
        ICBABDROP("#icbabdrop"),
        MENU("#menu"),
        ACCESS("#access"),
        NAVNBT("#top-navnbt"),
        MAIN_NAVIGATION(".main-navigation"),
        SITE_NAVIGATION("#site-navigation"),
        NAV("#nav, .nav"),
        NAVBAR(".navbar-nav:not(.pull-right)"),
        NAVIGATION(".navigation"),
        WIDGET_PAGES(".widget_pages"),
        ROLE_NAVIGATION("[role=navigation]"),
        PAGELIST(".PageList"),
        SITE_NAV(".site-nav"),
        FUSION_MENU("nav.fusion-sticky-menu"),
        SHADOW_NAV(".shadow:has(.nav)")
        ;


        private String selector;

        Headers(String selector) {
            this.selector = selector;
        }

        @Override
        public Element apply(Document document) {
            final Elements select = document.select(selector);
            int size = select.size();

            if (size == 1 || (size == 2 && select.get(1).id().contains("mobile"))) {
                System.out.println("Size: " + size + " for " + document.baseUri());
                System.out.println(this);
                return select.get(0);
            }
            return null;
        }
    }

}
