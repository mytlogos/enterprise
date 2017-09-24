package Enterprise.gui.enterprise.controller;

import Enterprise.ControlComm;
import Enterprise.gui.general.PostManager;
import Enterprise.misc.Log;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
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
                HBox postPane;

                @FXML
                private Text title;

                @FXML
                private Text date;

                @Override
                protected void updateItem(Post item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        postPane = new HBox();
                        postPane.setSpacing(5);

                        title = new Text(item.getTitle());
                        date = new Text(item.getTime());
                        configureWidth();

                        postPane.getChildren().add(title);
                        postPane.getChildren().add(date);

                        setGraphic(postPane);
                    }
                }

                /**
                 * Binds the value of the {@link Text#wrappingWidthProperty()}
                 * of the title.
                 * Calculated, so it will be wrapped, if no space is available for
                 * the title, after subtracting the spacing the the width of the date
                 * of the cell´s prefWidth.
                 */
                private void configureWidth() {
                    DoubleBinding rest = prefWidthProperty().
                            subtract(date.layoutBoundsProperty().get().getWidth()).
                            subtract(postPane.spacingProperty());

                    title.wrappingWidthProperty().bind(rest);
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
     * @return the stage of this {@code PostView}
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
        // FIXME: 24.09.2017 statusbar does not display progress or message or it will be displayed too short
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
