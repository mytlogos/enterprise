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

        PostManager.getInstance().putPost(entry.getSource(), post);
        return posts;
    }

    private Post getPost(PostSearchEntry searchEntry, Element postElement) {
        PostFormat format = new PostFormat();
        String link = format.getLink(postElement);
        String title = format.getTitle(postElement);

        //todo getAll content?
        String time = format.getTime(postElement);
        LocalDateTime dateTime = ParseTime.parseTime(time);
        boolean isSticky = postElement.hasClass("sticky");

        return new Post(searchEntry.getSource(), title, dateTime, link, searchEntry.getCreation(), isSticky);
    }

}
