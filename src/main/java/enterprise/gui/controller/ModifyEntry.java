package enterprise.gui.controller;

import enterprise.data.Default;
import enterprise.gui.general.BasicMode;
import enterprise.gui.general.GlobalItemValues;
import enterprise.gui.general.GuiPaths;
import enterprise.gui.general.Mode;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a paramount parent of all {@code Controller},
 * who modify (/create) {@link enterprise.data.intface.CreationEntry}s,
 * also {@code Controller}s with {@link BasicMode#EDIT}
 * or {@link BasicMode#ADD}.
 * Provides shared functionality.
 */
abstract class ModifyEntry implements InputLimiter, OpenAble {

    private final Logger logger = Default.LOGGER;
    @FXML
    protected Text label;
    @FXML
    Pane root;
    @FXML
    ComboBox<String> collection;
    @FXML
    ComboBox<String> creator;
    @FXML
    ComboBox<String> creatorSort;
    @FXML
    ComboBox<String> workStatus;
    @FXML
    ComboBox<String> ownStatus;
    @FXML
    TextField title;
    @FXML
    TextField rating;
    @FXML
    TextArea commentArea;
    @FXML
    TextField presentCreations;
    @FXML
    TextField dateLastCreation;
    @FXML
    TextField processedCreations;
    @FXML
    ImageView coverImage;
    @FXML
    TextField keyWords;
    String coverPath;
    @FXML
    private TextField coverURI;

    public void paneFocus() {
        for (Node node : root.getChildren()) {
            if (node instanceof Pane) {
                node.setOnMouseClicked(event -> node.requestFocus());
            }
        }
        root.setOnMouseClicked(event -> root.requestFocus());
        // FIXME: 27.07.2017 the panes don't get any on click focus
    }

    /**
     * Gets the input of a {@code TextField} which holds {@code Integer}.
     *
     * @param field field to getAll the input from
     * @return int - returns {@link Default#VALUE} if the field was empty
     */
    int validateIntInput(TextField field) {
        int input;
        if (field.getText().isEmpty()) {
            input = Default.VALUE;
        } else {
            input = Integer.parseInt(field.getText());
        }
        return input;
    }

    /**
     * Gets the input of a {@code ComboBox} which holds {@code Strings}.
     *
     * @param box {@code ComboBox} to getAll the item from
     * @return string - returns {@link Default#STRING} if no item was selected
     */
    String validateStringInput(ComboBox<String> box) {
        String input;
        if (box.getSelectionModel().getSelectedItem() == null || box.getSelectionModel().getSelectedItem().isEmpty()) {
            input = Default.STRING;
        } else {
            input = box.getSelectionModel().getSelectedItem();
        }
        return input;
    }

    /**
     * Gets the input of a {@code TextField} which holds {@code String}.
     *
     * @param field field to getAll the input from
     * @return string - returns {@link Default#STRING} if field was empty
     */
    String validateStringInput(TextField field) {
        String input;
        if (field.getText().isEmpty()) {
            input = Default.STRING;
        } else {
            input = field.getText();
        }
        return input;
    }

    /**
     * Gets the input of a {@code TextArea} which holds {@code String}.
     *
     * @param field field to getAll the input from
     * @return string - returns {@link Default#STRING} if field was empty
     */
    String validateStringInput(TextArea field) {
        String input;
        if (field.getText().isEmpty()) {
            input = Default.STRING;
        } else {
            input = field.getText();
        }
        return input;
    }

    /**
     * Readies the {@link ComboBox} {@code Nodes} of this instance.
     */
    void readyComboBoxes() {
        //sets the Items of the ComboBoxes
        setCombo(creator, GlobalItemValues.getInstance().getCreatorNames());
        setCombo(creatorSort, GlobalItemValues.getInstance().getSortCreators());
        setCombo(workStatus, GlobalItemValues.getInstance().getWorkStatus());
        setCombo(ownStatus, GlobalItemValues.getInstance().getOwnStatus());
        setCombo(collection, GlobalItemValues.getInstance().getCollections());

        //adds behaviour on an ActionEvent
        comboAdd(creator);
        comboAdd(creatorSort);
        comboAdd(workStatus);
        comboAdd(ownStatus);
        comboAdd(collection);

        //sets onCloseEvent behaviour on the ComboBoxes
        getComboOnClose(creator, GlobalItemValues.getInstance().getCreatorNames());
        getComboOnClose(creatorSort, GlobalItemValues.getInstance().getSortCreators());
        getComboOnClose(workStatus, GlobalItemValues.getInstance().getWorkStatus());
        getComboOnClose(ownStatus, GlobalItemValues.getInstance().getOwnStatus());
        getComboOnClose(collection, GlobalItemValues.getInstance().getCollections());
    }

    /**
     * Sets provided the {@code list} as the list of the
     * provided {@link ComboBox#itemsProperty()} in an
     * observable ArrayList by {@link FXCollections}.
     *
     * @param combo box to add the items to
     * @param list  list of items to add
     */
    void setCombo(ComboBox<String> combo, List<String> list) {
        combo.itemsProperty().set(FXCollections.observableArrayList(list));
    }

    /**
     * Adds a listener, which adds items not in {@link ComboBox#items},
     * if an {@code ActionEvent} was fired from the {@code ComboBox}.
     *
     * @param combo box to add the the user input to
     */
    void comboAdd(ComboBox<String> combo) {
        combo.setOnAction(event -> {
            String string = combo.getEditor().getText();
            if (!combo.getItems().contains(string)) {
                combo.getItems().add(string);
            }
        });
    }

    /**
     * Adds an {@code EventHandler} on a {@link javafx.stage.WindowEvent#WINDOW_CLOSE_REQUEST},
     * which adds all {@code Items} the provided {@link ComboBox} to the
     * provided {@code strings}.
     *
     * @param combo   box to getAll the items from
     * @param strings list to add the items to
     */
    void getComboOnClose(ComboBox<String> combo, List<String> strings) {
        Platform.runLater(() -> {
            Stage stage = (Stage) combo.getScene().getWindow();
            stage.setOnCloseRequest(event -> strings.addAll(combo.getItems()));
        });
    }

    /**
     * Restrains the input on several Nodes of this class.
     */
    void readyInput() {
        limitToLength(title, 200);
        limitToLength(collection.getEditor(), 100);
        limitToLength(creator.getEditor(), 100);
        limitToLength(creatorSort.getEditor(), 100);
        limitToLength(workStatus.getEditor(), 100);
        limitToLength(ownStatus.getEditor(), 100);
        limitToLength(commentArea, 3000);
        inputLimitToInt(rating, 2, 10);
        limitToLength(dateLastCreation, 100);
        inputLimitToInt(presentCreations, 10);
        inputLimitToInt(processedCreations, 10);
        limitToLength(keyWords, 100);

    }

    /**
     * Creates the Window with the content specified by the
     * {@link Mode} and {@link Module} of each Controller.
     */
    protected Stage loadStage() {
        String location = GuiPaths.getPath(getModule(), getMode());

        FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "could not load file", e);
        }

        Stage stage = new Stage();
        if (root != null) {
            stage.setScene(new Scene(root));
        }
        ModifyEntry modifyEntry = loader.getController();
        modifyEntry.setData(stage);
        return stage;
    }

    /**
     * Sets visible text to several Nodes depending on the {@link BasicModule}.
     */
    protected abstract void setData(Stage stage);

    /**
     * Opens a {@link FileChooser} to select a File from the disk,
     * save the Path in a String and display the {@link Image} in an {@link javafx.scene.image.ImageView}.
     */
    @FXML
    protected void addLocalImage() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        fileChooser.setInitialDirectory(new File("img"));


        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image img = new Image(file.toURI().toString());
            coverImage.setImage(img);
            //relativize path against home directory
            coverPath = new File("").toURI().relativize(file.toURI()).toString();
            System.out.println(coverPath);
        }
    }

    /**
     * Downloads the image specified by user input in the {@code coverURI} TextField
     * and saves it on the disk in the 'img' directory of the working directory of this project.
     */
    @FXML
    protected void addNetImage() {
        try {
            URI uri = new URI(coverURI.getText());
            Connection.Response resultImageResponse = Jsoup.connect(uri.toString()).ignoreContentType(true).execute();

            String path = uri.getPath();

            int slash = path.lastIndexOf("/");

            String file = path.substring(slash, path.length());

            File imageFile = new File("img\\" + file);

            FileOutputStream out = (new FileOutputStream(imageFile));
            out.write(resultImageResponse.bodyAsBytes());  // resultImageResponse.body() is where the image's contents are.
            out.close();

            Image img = new Image(imageFile.toURI().toString());

            coverImage.setImage(img);
            //relativize path against home directory
            coverPath = new File("").toURI().relativize(imageFile.toURI()).toString();
            System.out.println(coverPath);
        } catch (URISyntaxException | IOException e) {
            // TODO: 24.08.2017 show alert, could not download image: invalid URL or a connection problem
            e.printStackTrace();
        }
    }
}
