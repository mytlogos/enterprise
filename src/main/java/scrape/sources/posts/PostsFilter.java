package scrape.sources.posts;

import enterprise.data.intface.Creation;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 */
public class PostsFilter {
    private final PostSearchEntry search;

    public PostsFilter(PostSearchEntry entry) {
        this.search = entry;
    }

    public Elements filter(Elements posts) {
        if (search == null) {
            throw new IllegalStateException();
        }

        Elements elements = new Elements();
        for (Element post : posts) {
            if (inText(post)) {
                elements.add(post);
            } else {
                post.attributes().forEach(attribute -> searchAttribute(elements, post, attribute));
                post.children().forEach(node -> node.attributes().forEach(attribute -> searchAttribute(elements, post, attribute)));
            }
        }
        return elements;
    }

    private boolean inText(Element post) {
        boolean contains = search.getKeyWords().stream().anyMatch(s -> post.text().contains(s));
        Creation creation = search.getCreation();
        boolean containsTitle = false;

        if (creation != null) {
            containsTitle = searchForTitle(post, creation);
        }

        return contains || containsTitle;
    }

    private void searchAttribute(Elements elements, Element post, Attribute attribute) {
        if (search.getKeyWords().stream().anyMatch(s -> attribute.getValue().contains(s))) {
            elements.add(post);
        }
    }

    private boolean searchForTitle(Element post, Creation creation) {
        boolean contains = false;

        String abbreviation = abbreviate(creation.getTitle());
        if (!abbreviation.isEmpty() && abbreviation.length() > 1) {
            contains = post.text().contains(abbreviation);
        }
        if (!contains) {
            contains = post.text().contains(search.getCreation().getTitle());
        }
        return contains;
    }

    private String abbreviate(String s) {
        StringBuilder builder = new StringBuilder();
        for (String s1 : s.split("[^a-zA-Z0-9öäü]")) {
            if (!s1.isEmpty()) {
                builder.append(s1.substring(0, 1).toUpperCase());
            }
        }
        return builder.toString();
    }
}
