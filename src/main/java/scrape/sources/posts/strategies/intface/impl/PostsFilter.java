package scrape.sources.posts.strategies.intface.impl;

import Enterprise.misc.TimeMeasure;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.posts.strategies.intface.ElementFilter;
import scrape.sources.posts.strategies.intface.PostElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class PostsFilter implements ElementFilter {

    @Override
    public List<PostElement> getFilter() {
        return new ArrayList<>(Arrays.asList(Posts.values()));
    }

    /**
     *
     */
    public enum Posts implements PostElement {
        LIST("ul.new-list > li > a") {
        },
        TABLE("table[class*=update] > tbody > tr") {
        },
        SHADOW("[class=shadow]") {
        },
        ROW(".chapter, .announcements") {
        },
        ARTICLE("article") {
        },
        HENTRY(".hentry") {
        },
        POST(".post") {
        },
        POST_EX_ASIDE(".post:not(.format-aside)") {
        },
        ROW_NOVEL(Type.LESYT);

        private String selector;
        private Type type;

        Posts(String s) {
            selector = s;
        }


        Posts(Type lesyt) {
            this.type = lesyt;
        }


        @Override
        public Elements apply(Element body) {
            if (type == null) {
                return body.select(selector);
            } else {
                return type.apply(body);
            }
        }

    }

    private enum Type {
        LESYT {
            @Override
            Elements apply(Element body) {
                TimeMeasure measure = TimeMeasure.start();
                Elements selected = body.select(".row-novel");
                Elements result = new Elements();
                for (Element element : selected) {
                    Elements button = element.select("button.novel-border");

                    if (button.size() == 1) {
                        String postSelector = button.get(0).attr("data-target");
                        Elements posts = body.select(postSelector);

                        if (posts.size() == 1) {

                            Elements postBodies = posts.select(".row");
                            postBodies.forEach(chapterPost -> {
                                chapterPost.select("a").forEach(linkElement -> {
                                    String novelName = button.get(0).text();

                                    if (!linkElement.text().contains(novelName)) {
                                        linkElement.prependText(novelName.concat(" "));
                                    }
                                });
                                chapterPost.select("span").remove();
                            });
                            result.addAll(postBodies);
                        }
                    }
                }
                measure.finish();
                if (!result.isEmpty()) {
                    return result;
                }
                return null;
            }
        };

        abstract Elements apply(Element body);
    }

}
