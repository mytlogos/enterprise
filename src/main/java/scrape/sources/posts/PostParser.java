package scrape.sources.posts;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.sources.posts.strategies.PostFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class parses {@link Elements} to {@link Post}.
 */
class PostParser {
    List<Post> toPosts(Elements postElements, PostSearchEntry entry) {
        List<Post> posts = new PostList();
        for (Element postElement : postElements) {

            Post post = getPost(entry, postElement);
            posts.add(post);
        }

        Post post = posts.stream().max(Post::compareTo).orElse(null);
        System.out.println("Newest Post: " + post);

        entry.getSource().putPost(entry.getCreation(), post);
        return posts;
    }

    private Post getPost(PostSearchEntry searchEntry, Element postElement) {
        String link = new PostFormat().getLink(postElement);
        String title = new PostFormat().getTitle(postElement);

        String time = new PostFormat().getTime(postElement);
        LocalDateTime dateTime = ParseTime.parseTime(time);
        boolean isSticky = postElement.hasClass("sticky");

        return new Post(searchEntry.getSource(), title, dateTime, link, searchEntry.getCreation(), isSticky);
    }

}
