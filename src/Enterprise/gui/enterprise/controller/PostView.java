package Enterprise.gui.enterprise.controller;

import Enterprise.ControlComm;
import Enterprise.gui.general.PostManager;
import Enterprise.misc.Log;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.StatusBar;
import scrape.concurrent.ScheduledScraper;
import scrape.sources.Post;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is for presenting the scraped Posts in a new Window.
 * // TODO: 24.08.2017 do the javadoc
 */
public class PostView implements Initializable{

    private static PostView INSTANCE = new PostView();
    @FXML
    private ToggleButton allToggle;
    @FXML
    private ToggleButton newToggle;

    private static Stage stage = null;
    private Logger logger = Log.classLogger(this);

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

    private PostView() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
    }

    public static PostView getInstance() {
        return INSTANCE;
    }

    /**
     * Opens an independent window of {@code PostView}.
     */
    public void open() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/PostView.fxml"));
        try {
            Parent root;
            loader.setController(this);
            root = loader.load();
            Stage stage = new Stage();

            stage.setTitle("Posts");
            stage.setScene(new Scene(root));

            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "could not open postView", e);
        }
    }

    /**
     * Opens a owned Window of {@code PostView}.
     * This Window appears right beside the Owner {@code window},
     * with Width set to 400, making the width not changeable
     * through User interactions.
     * The height is always the same as the Owner {@code window}.
     * This window follows the Owner {@code window}, always maintaining
     * the same relative position.
     *
     * @param window window which will own the window of this {@code PostView}.
     * @return
     * @throws IOException
     */
    Stage open(Window window) throws IOException {
        if (stage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/PostView.fxml"));
            loader.setController(this);
            root = loader.load();

            stage = new Stage();
            stage.initOwner(window);
            stage.setTitle("Posts");
            stage.setScene(new Scene(root));

            int closeWindowGap = -0;

            window.xProperty().addListener((observable, oldValue, newValue) -> stage.setX(newValue.doubleValue() + window.getWidth() + closeWindowGap));
            window.yProperty().addListener((observable, oldValue, newValue) -> stage.setY(newValue.doubleValue()));

            window.widthProperty().addListener((observable, oldValue, newValue) -> stage.setX(window.getX() + newValue.doubleValue() + closeWindowGap));

            stage.minHeightProperty().bind(window.heightProperty());
            stage.maxHeightProperty().bind(window.heightProperty());

            stage.setX(window.getX() + window.getWidth() + closeWindowGap);
            stage.setHeight(window.getHeight());
            stage.setY(window.getY());
            stage.setMaxWidth(400);
            stage.setMinWidth(400);

            allToggle.setSelected(true);
            showAll();

            stage.show();
        } else {
            stage.close();
            stage = null;
        }
        return stage;
    }

    private void setStatusBar() {
        ScheduledScraper scraper = PostManager.getInstance().getScheduledScraper();

        statusBar.progressProperty().bind(scraper.progressProperty());
        statusBar.textProperty().bind(scraper.messageProperty());
    }

    @FXML
    void emptyNew() {
        PostManager.getInstance().clearNew();
    }

    @FXML
    private void showAll() {
        listView.itemsProperty().bind(PostManager.getInstance().getPosts());
    }

    @FXML
    private void showNew() {
        listView.itemsProperty().bind(PostManager.getInstance().getNewPosts());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setListView();
        setStatusBar();
//        root.setPrefWidth(600);
    }

    public void openNew() {
        Stage stage = ControlComm.getInstance().getEnterpriseController().getStage();
        try {
            if (stage != null) {
                open(stage);
            }
            newToggle.setSelected(true);
            showNew();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "could not open postView", e);
        }
    }
}
