package enterprise.gui.enterprise;

import enterprise.ControlComm;
import enterprise.data.Default;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.StatusBar;
import scrape.sources.posts.Post;
import scrape.sources.posts.PostManager;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is for presenting the scraped Posts in a new Window.
 * // TODO: 24.08.2017 do the javadoc
 */
public class PostView {

    private static final PostView INSTANCE = new PostView();
    private static Stage stage = null;

    private final Logger logger = Default.LOGGER;
    public SortingBy SORTED_BY;
    @FXML
    private ToggleButton allToggle;
    @FXML
    private ToggleButton newToggle;
    @FXML
    private VBox root;
    @FXML
    private ListView<Post> listView;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ComboBox<SortingBy> sortBy;

    private PostView() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        SORTED_BY = SortingBy.DATEDESC;
    }

    public static PostView getInstance() {
        return INSTANCE;
    }

    /**
     * Opens an independent window of {@code PostView}.
     */
    public void open() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/postView.fxml"));
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

    public void openNew() {
        Stage stage = ControlComm.get().getEnterpriseController().getStage();
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
     * @throws IOException if fxml could not be loaded
     */
    Stage open(Window window) throws IOException {
        if (stage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/postView.fxml"));
            loader.setController(this);
            root = loader.load();

            stage = new Stage();
            stage.initOwner(window);
            stage.setTitle("Posts");
            stage.setScene(new Scene(root));

            final int closeWindowGap = -0;

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

    @FXML
    private void showNew() {
        PostManager.getInstance().getNewPosts().bindToList(listView.itemsProperty());
    }

    @FXML
    private void showAll() {
        PostManager.getInstance().getPosts().bindToList(listView.itemsProperty());
    }

    public void initialize() {

        setListView();
        setStatusBar();
        sortBy.getItems().addAll(SortingBy.values());
        sortBy.getSelectionModel().select(SortingBy.DATEDESC);
    }

    private void setListView() {
        listView.setCellFactory(param ->
                new ListCell<Post>() {
                    @FXML
                    HBox postPane;
                    @FXML
                    private Text title;
                    @FXML
                    private Text date;

                    {
                        prefWidthProperty().bind(root.widthProperty().subtract(30));
                    }

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

                            this.setOnMouseClicked(event -> {
                                try {
                                    URI uri = URI.create(item.getFollowLink());
                                    Desktop.getDesktop().browse(uri);
                                } catch (IOException | IllegalArgumentException e) {
                                    Notifications.create().title(item.getTitle() + " has invalid Url" + item.getFollowLink()).show();
                                }
                            });

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

    private void setStatusBar() {
        // FIXME: 24.09.2017 statusbar does not display progress or message or it will be displayed too short
        PostManager manager = PostManager.getInstance();

        statusBar.progressProperty().bind(manager.progressProperty());
        statusBar.textProperty().bind(manager.messageProperty());
    }

    @FXML
    void sort() {
        SORTED_BY = sortBy.getSelectionModel().getSelectedItem();
        Comparator<Post> comparator = SORTED_BY.getComparator();
        listView.getItems().sort(comparator);
    }

    @FXML
    void emptyNew() {
        PostManager.getInstance().clearNew();
    }

    public enum SortingBy {
        DATEDESC("Neueste zuerst") {
            @Override
            public Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getTimeStamp).reversed();
            }
        },
        DATEASC("Älteste zuerst") {
            @Override
            public Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getTimeStamp);
            }
        },
        TITLEAZ("Alphabetisch aufsteigend") {
            @Override
            public Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getTitle);
            }
        },
        TITLEZA("Alphabetisch absteigend") {
            @Override
            public Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getTitle).reversed();
            }
        },
        SOURCEASC("Nach Quellen A-Z") {
            @Override
            public Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getSource);
            }
        },
        SOURCEDES("Nach Quellen Z-A") {
            @Override
            public Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getSource).reversed();
            }
        },;

        final String text;

        SortingBy(String s) {
            text = s;
        }

        public abstract Comparator<Post> getComparator();

        @Override
        public String toString() {
            return text;
        }
    }

}
