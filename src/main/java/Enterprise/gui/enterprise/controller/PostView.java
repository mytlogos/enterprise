package Enterprise.gui.enterprise.controller;

import Enterprise.ControlComm;
import Enterprise.misc.Log;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import scrape.Post;
import scrape.PostManager;
import scrape.concurrent.ScheduledPostScraper;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is for presenting the scraped Posts in a new Window.
 * // TODO: 24.08.2017 do the javadoc
 */
public class PostView implements Initializable {

    private static PostView INSTANCE = new PostView();
    private static Stage stage = null;
    @FXML
    private ToggleButton allToggle;
    @FXML
    private ToggleButton newToggle;
    private Logger logger = Log.classLogger(this);

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setListView();
        setStatusBar();
        sortBy.getItems().addAll(SortingBy.values());
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

    @FXML
    void sort() {
        Comparator<Post> comparator = sortBy.getSelectionModel().getSelectedItem().getComparator();
        listView.getItems().sort(comparator);
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
    void emptyNew() {
        PostManager.getInstance().clearNew();
    }

    @FXML
    private void showAll() {
        PostManager.getInstance().getPosts().bindToList(listView.itemsProperty());
    }

    @FXML
    private void showNew() {
        PostManager.getInstance().getNewPosts().bindToList(listView.itemsProperty());
    }

    private void setStatusBar() {
        // FIXME: 24.09.2017 statusbar does not display progress or message or it will be displayed too short
        ScheduledPostScraper scraper = PostManager.getInstance().getScheduledScraper();

        statusBar.progressProperty().bind(scraper.progressProperty());
        statusBar.textProperty().bind(scraper.messageProperty());
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

    private enum SortingBy {
        TITLEAZ("Alphabetisch aufsteigend") {
            @Override
            Comparator<Post> getComparator() {
                return (o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle());
            }
        },
        TITLEZA("Alphabetisch absteigend") {
            @Override
            Comparator<Post> getComparator() {
                return (o1, o2) -> o2.getTitle().compareToIgnoreCase(o1.getTitle());
            }
        },
        DATEASC("Älteste zuerst") {
            @Override
            Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getTimeStamp);
            }
        },
        DATEDESC("Neueste zuerst") {
            @Override
            Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getTimeStamp).reversed();
            }
        },
        SOURCEASC("Nach Quellen A-Z") {
            @Override
            Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getSource);
            }
        },
        SOURCEDES("Nach Quellen Z-A") {
            @Override
            Comparator<Post> getComparator() {
                return Comparator.comparing(Post::getSource).reversed();
            }
        },;

        final String text;

        SortingBy(String s) {
            text = s;
        }

        abstract Comparator<Post> getComparator();

        @Override
        public String toString() {
            return text;
        }
    }

}