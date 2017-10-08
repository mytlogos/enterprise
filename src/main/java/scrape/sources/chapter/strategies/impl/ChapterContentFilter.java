package scrape.sources.chapter.strategies.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.chapter.strategies.intface.ChapterElement;
import scrape.sources.posts.strategies.intface.ElementFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class ChapterContentFilter implements ElementFilter<ChapterElement> {
    @Override
    public Collection<ChapterElement> getFilter() {
        return new ArrayList<>(Arrays.asList(ChapterContents.values()));
    }

    private enum ChapterContents implements ChapterElement {
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
                    return selected.get(1).text().isEmpty() ? selected.get(0) : apply(selected.get(0));
                } else {
                    return null;
                }
            } else {
                return selected.get(0);
            }
        }
    }

}
