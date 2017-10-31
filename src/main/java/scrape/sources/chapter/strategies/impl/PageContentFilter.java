package scrape.sources.chapter.strategies.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.chapter.strategies.intface.PageContentElement;
import scrape.sources.posts.strategies.intface.ElementFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class PageContentFilter implements ElementFilter<PageContentElement> {
    @Override
    public Collection<PageContentElement> getFilter() {
        return new ArrayList<>(Arrays.asList(ChapterContents.values()));
    }

    private enum ChapterContents implements PageContentElement {
        CHAPTER_CONTENT("#chapterContent"),
        ARTICLE_BODY(".article-body"),
        BOX(".box"),
        CHA_CONTENT(".cha-content"),
        ENTRY(".entry"),
        ENTRY_CONTENT(".entry-content"),
        POST_CONTENT(".post-content"),
        SINGE_CONTENT(".single-content"),
        TEXT_POST_BODY(".textpostbody"),;

        private final String selector;

        ChapterContents(String selector) {
            this.selector = selector;
        }

        @Override
        public Element apply(Element element) {
            Elements selected = element.select(selector);
            if (selected.size() != 1) {
                if (selected.size() == 2) {
                    // FIXME: 10.10.2017 a possible bug could be hiding here
                    Element first = selected.get(0);
                    Element second = selected.get(1);

                    if (second.text().isEmpty()) {
                        return first.text().isEmpty() ? null : first;
                    } else {
                        if (first.children().contains(second)) {
                            return first;
                        } else if (first.text().length() > second.text().length()) {
                            return first;
                        } else {
                            return second;
                        }
                    }
                } else {
                    return null;
                }
            } else {
                return selected.get(0);
            }
        }
    }

}
