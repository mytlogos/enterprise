package scrape.concurrent;

import scrape.Post;
import scrape.PostManager;
import scrape.PostTable;
import scrape.sources.Source;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 */
public class PostCall implements Callable<Boolean> {

    private Action action;

    public PostCall(Action action) {
        this.action = action;
    }

    @Override
    public Boolean call() throws Exception {
        return action.doAction();
    }

    public enum Action {
        GET_ENTRIES {
            @Override
            boolean doAction() {
                List<Post> posts = PostTable.getInstance().getEntries();

                if (!posts.isEmpty()) {
                    PostManager.getInstance().getPosts().addAll(posts);
                    Map<Source, List<Post>> postMap = mapWithSource(posts);

                    for (Source source : postMap.keySet()) {
                        Post post = postMap.get(source).
                                stream().
                                max(Comparator.comparing(Post::getTimeStamp)).
                                orElse(null);
                        if (post != null) {
                            source.putPost(post.getCreation(), post);
                        } else {
                            source.putPost(null, null);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        },
        ADD_ENTRIES {
            @Override
            boolean doAction() {
                List<Post> posts = PostManager.getInstance().getPosts();
                List<Post> filtered = filterPosts(posts);
                return PostTable.getInstance().insert(filtered);
            }
        },
        DELETE_ENTRIES {
            @Override
            boolean doAction() {
                List<Post> oldPosts = PostManager.
                        getInstance().
                        getPosts().
                        stream().
                        filter(timeFilter().negate()).
                        collect(Collectors.toList());
                return PostTable.getInstance().delete(oldPosts);
            }
        };

        private static Map<Source, List<Post>> mapWithSource(List<Post> posts) {
            return posts.stream().collect(Collectors.groupingBy(Post::getSource));
        }

        private static List<Post> filterPosts(List<Post> posts) {
            return posts.stream().filter(timeFilter()).collect(Collectors.toList());
        }

        private static Predicate<Post> timeFilter() {
            return post -> {
                if (post.isSticky()) {
                    LocalDateTime limit = LocalDateTime.now().minusYears(1);
                    return limit.isBefore(post.getTimeStamp());
                } else {
                    LocalDateTime limit = LocalDateTime.now().minusWeeks(1);
                    return limit.isBefore(post.getTimeStamp());
                }
            };
        }

        abstract boolean doAction();
    }

}
