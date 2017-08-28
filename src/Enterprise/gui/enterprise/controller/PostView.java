package Enterprise.gui.enterprise.controller;

import scrape.concurrent.ScheduledScraper;
import Enterprise.gui.general.PostSingleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import scrape.sources.Post;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This class is for presenting the scraped Posts in a new Window.
 * // TODO: 24.08.2017 do the javadoc
 */
public class PostView implements Initializable{

    @FXML
    private VBox root;

    @FXML
    private ListView<Post> listView;

    @FXML
    private StatusBar statusBar;


    private void setListView() {
        listView.setCellFactory(param ->
            new ListCell<Post>(){
                {
                    prefWidthProperty().bind(root.widthProperty().subtract(30));
                }
                @FXML
                AnchorPane postPane;

                @FXML
                private Text title;

                @FXML
                private Text date;

                @FXML
                private Label content;

                @FXML
                private Text footer;

                private FXMLLoader mLLoader;

                @Override
                protected void updateItem(Post item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        if (mLLoader == null) {
                            mLLoader = new FXMLLoader(getClass().getResource("../fxml/PostCell.fxml"));
                            mLLoader.setController(this);
                            try {
                                mLLoader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (title == null ||  content == null) {
                            System.out.println(title);
                            System.out.println(content);
                            System.out.println(footer);
                        } else {
                            title.setText(item.getTitle());
                            content.setText(item.getContent().toString());
                            date.setText(item.getTime());

                            title.setWrappingWidth(440);
                            date.setWrappingWidth(120);
                            content.setWrapText(true);
                            content.prefWidthProperty().bind(widthProperty().subtract(30));

                            setGraphic(postPane);
                        }
                    }
                }
            }
        );
    }

    public void add(Post post) {
        if (post != null) {
            listView.getItems().add(post);
        } else {
            System.out.println("Post ist null!");
        }
    }

    public void open() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/PostView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Posts");
        if (root != null) {
            stage.setScene(new Scene(root));
        }
        stage.show();

    }

    private void setStatusBar() {
        ScheduledScraper scraper = PostSingleton.getInstance().getScheduledScraper();

        statusBar.progressProperty().bind(scraper.progressProperty());
        statusBar.textProperty().bind(scraper.messageProperty());
    }


    private void setData() {
        listView.itemsProperty().bind(PostSingleton.getInstance().getPosts());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setListView();
        setStatusBar();
        setData();
        root.setPrefWidth(600);
    }
}
