package scrape.sources.chapter.strategies.impl;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.chapter.strategies.ChapterFormat;
import scrape.sources.posts.strategies.intface.ElementFilter;
import scrape.sources.posts.strategies.intface.TitleElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class ChapterTitleFilter implements ElementFilter<TitleElement> {
    @Override
    public Collection<TitleElement> getFilter() {
        return new ArrayList<>(Arrays.asList(Titles.values()));
    }

    public enum Titles implements TitleElement {
        CAT_LINK(".cat-links", ".entry-title", Type.DOUBLE),
        BREADCRUMPS(".breadcrumps-ancestor", ".breadcrumps-current", Type.DOUBLE),
        CHAPTER_TITLE(".chapter-title", ".entry-title", Type.DOUBLE),
        CHA_TIT(".cha-tit", Type.SINGLE),
        ENTRY_TITLE(".entry-title", Type.SINGLE),
        PAGE_TITLE(".page-title", Type.SINGLE),
        POST_TITLE(".post-title", Type.SINGLE),
        TITLE("h2.title", Type.SINGLE),
        PARENTAL("h1 > strong", Type.SINGLE),
        H_CONTAINS("h3:contains(chapter)", Type.SINGLE),
        DOUBLE_PARENTAL("p > b > i", Type.SINGLE),
        P_CONTAINS("p:contains(chapter)", Type.SINGLE),;

        String novel;
        String chapter;
        String title;
        Type type;

        Titles(String novel, String chapter, Type type) {
            this.novel = novel;
            this.chapter = chapter;
            this.type = type;

        }

        Titles(String title, Type type) {
            this.title = title;
            this.type = type;
        }


        @Override
        public Element apply(Element element) {
            return this.type.get(this, element);
        }
    }

    private enum Type {
        DOUBLE {
            @Override
            Element get(Titles titles, Element element) {
                Elements novelSelected = element.select(titles.novel);
                Elements chapterSelected = element.select(titles.chapter);

                if (novelSelected.size() == 1 && chapterSelected.size() == 1) {
                    String novel = novelSelected.get(0).text();
                    String chapter = chapterSelected.get(0).text();
                    return new ChapterFormat().getTitleElement(novel + " " + chapter);
                } else {
                    return null;
                }
            }
        },
        SINGLE {
            @Override
            Element get(Titles titles, Element element) {
                Elements elements = element.select(titles.title);
                if (elements.size() == 1) {
                    return new ChapterFormat().getTitleElement(elements.get(0).text());
                } else {
                    Element sibling = element.previousElementSibling();
                    if (sibling != null) {
                        Elements siblingElements = sibling.select(titles.title);
                        if (siblingElements.size() == 1) {
                            return new ChapterFormat().getTitleElement(siblingElements.get(0).text());
                        }
                    }
                }
                return null;
            }
        };

        abstract Element get(Titles titles, Element element);
    }

}
