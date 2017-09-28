package Enterprise;

import Enterprise.data.concurrent.GetCall;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.SourceableEntry;
import Enterprise.gui.general.GuiPaths;
import Enterprise.misc.Log;
import Enterprise.modules.BasicModules;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scrape.PostManager;
import scrape.concurrent.PostCall;
import scrape.concurrent.ScheduledPostScraper;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import static Enterprise.modules.BasicModules.ANIME;
import static Enterprise.modules.BasicModules.NOVEL;
import static scrape.concurrent.PostCall.Action.GET_ENTRIES;

public class
EnterpriseStart extends Application {

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    @Override
    public void start(Stage primaryStage) throws Exception{
        getEntryData();
        getSavedPosts();
        executor.shutdown();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(GuiPaths.getMainPath()));
        Parent root = loader.load();

        primaryStage.setTitle("Enterprise");
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * Starts the {@link GetCall} to get all {@link CreationEntry}s from the source of data.
     * Separates the Entries according to their {@link BasicModules}s.
     * Makes the {@link Enterprise.data.intface.Sourceable}s available for the {@link ScheduledPostScraper}.
     */
    private void getEntryData() {
        Future<List<? extends CreationEntry>> future = executor.submit(new GetCall());

        List<? extends CreationEntry> futureEntries;
        try {
            futureEntries = future.get();

            for (CreationEntry creationEntry : futureEntries) {
                creationEntry.getModule().addEntry(creationEntry);
            }

            ANIME.getEntries().forEach(novelEntry -> PostManager.getInstance().addSearchEntries(((SourceableEntry) novelEntry)));
            NOVEL.getEntries().forEach(animeEntry -> PostManager.getInstance().addSearchEntries(((SourceableEntry) animeEntry)));

        } catch (InterruptedException | ExecutionException e) {
            Log.packageLogger(this).log(Level.SEVERE, "error occurred while getting data from the database", e);
        }
    }

    private void getSavedPosts() {
        executor.submit(new PostCall(GET_ENTRIES));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
