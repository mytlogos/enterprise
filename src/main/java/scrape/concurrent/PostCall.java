package scrape.concurrent;

import enterprise.data.Default;
import enterprise.data.dataAccess.DataAccessManager;
import scrape.sources.Source;
import scrape.sources.posts.Post;
import scrape.sources.posts.PostManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 */
public class PostCall implements Callable<Boolean> {

    private final Action action;

    public PostCall(Action action) {
        this.action = action;
    }

    @Override
    public Boolean call() {
        return action.doAction();
    }

    public enum Action {
        GET_ENTRIES {
            @Override
            boolean doAction() {
//                List<Post> posts = PostTable.get().getCreationEntries();
                List<Post> posts = new ArrayList<>();

                if (!posts.isEmpty()) {
                    PostManager.getInstance().addPosts(posts);
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
//                boolean insert = PostTable.get().insert(filtered);
                return false;
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

                oldPosts.addAll(posts);
                try {
                    DataAccessManager.manager.delete(new ArrayList<>(oldPosts));
                    return true;
                } catch (Exception e) {
                    Default.LOGGER.log(Level.SEVERE, "exception occurred while deleting old posts", e);
                    return false;
                }
            }

            final Set<Post> posts = new HashSet<>();
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            boolean started = false;

            public void startTimer() {
                if (!executor.isShutdown()) {
                    executor.submit(() -> {
                        if (!started) {
                            started = true;
                            executor.shutdown();

                            LocalDateTime now = LocalDateTime.now().plusSeconds(4);
                            while (now.isAfter(LocalDateTime.now())) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                            }
                            doAction();
                        }
                    });
                }
            }

            public void queueEntry(Post post) {
                posts.add(post);
            }
        },;

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

        public void queueEntry(Post post) {

        }

        public void startTimer() {

        }

        abstract boolean doAction();
    }

}
