package scrape.sources.novels;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scrape.Post;
import scrape.PostList;
import scrape.SearchEntry;
import scrape.sources.novels.strategies.PostFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class parses {@link Elements} to {@link Post}.
 */
class PostParser {
    List<Post> toPosts(Elements postElements, SearchEntry entry) {
        List<Post> posts = new PostList();
        for (Element postElement : postElements) {

            Post post = getPost(entry, postElement);
            posts.add(post);
        }

        Post post = posts.stream().max(Post::compareTo).orElse(null);
        System.out.println("Newest Post: " + post);

        entry.getSource().putPost(entry.getCreationKey(), post);
        return posts;
    }

    private Post getPost(SearchEntry searchEntry, Element postElement) {
        String link = PostFormat.getLink(postElement);
        String title = PostFormat.getTitle(postElement);

        String time = PostFormat.getTime(postElement);
        LocalDateTime dateTime = ParseTime.parseTime(time);
        boolean isSticky = postElement.hasClass("sticky");

        return new Post(searchEntry.getSource(), title, dateTime, link, searchEntry.getCreationKey(), isSticky);
    }

}
