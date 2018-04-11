package scrape.sources.novel;

import enterprise.data.intface.Creation;
import scrape.sources.Content;
import scrape.sources.novel.toc.structure.TableOfContent;
import scrape.sources.posts.Post;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface WebAdapter {
    TableOfContent getToc();

    Collection<Post> getPosts();

    Content getContent();

    void setCreation(Creation creation);

    List<String> getMedia();
}
