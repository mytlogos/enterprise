package Enterprise.test;

import Enterprise.gui.general.PostList;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.jsoup.select.Elements;
import scrape.sources.novels.NovelPosts;
import scrape.sources.Post;
import scrape.sources.Source;
import scrape.sources.SourceList;

import java.util.*;

/**
 * Created by Dominik on 22.07.2017.
 * Part of OgameBot.
 */
public class ServiceClass extends Service<List<Post>> {
    private SourceList list = new SourceList();

    public void setList(ObservableList<Source> list) {
        this.list.set(list);
    }

    ServiceClass() {
    }

    @Override
    protected Task<List<Post>> createTask() {
        return new Task<List<Post>>() {
            @Override
            protected List<Post> call() throws Exception {
                Thread.currentThread().setName("ScrapePost-Thread");

                updateMessage("LadeService wird durchgeführt...");

                updateTitle("ScrapePost");

                PostList postList = new PostList();


                int maxWork = 0;
                int progress = 0;

                maxWork += list.size();

                updateProgress(-1, maxWork);
                updateProgress(progress, maxWork);

                NovelPosts host = new NovelPosts();

                for (Source source : list) {
                    updateMessage("Lade Posts von " + source.getSourceName());
                    try {
                        List<Post> list = host.getPosts(source.getUrl());
                        System.out.println("Anzahl Posts: " + list.size());
                        postList.addAll(list);

                    } catch (Exception e) {
                        // TODO: 31.07.2017 logg error or print msg
                        System.out.println(e.getMessage());
                    }
                    progress++;
                    System.out.println("Fortschritt " + progress);
                    updateProgress(progress, maxWork);
                }

                updateMessage("Fertig");
                return postList;
            }
        };
    }

}
