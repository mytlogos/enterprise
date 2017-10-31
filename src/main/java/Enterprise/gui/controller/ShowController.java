package Enterprise.gui.controller;

import Enterprise.ControlComm;
import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.general.BasicModes;
import Enterprise.gui.general.GuiPaths;
import Enterprise.gui.general.Mode;
import Enterprise.misc.EntrySingleton;
import Enterprise.misc.Log;
import Enterprise.modules.Module;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The basic {@link Controller} of all {@code Controller}  with {@link BasicModes#SHOW}.
 * Provides common fields and functionality.
 */
public abstract class ShowController<E extends CreationEntry, R extends Enum<R> & Module> extends AbstractController<R, BasicModes> implements Controller {

    protected Logger logger = Log.packageLogger(this);
    @FXML
    protected TextArea commentArea;
    @FXML
    protected ImageView coverImage;
    @FXML
    protected Button editBtn;
    @FXML
    protected Text title;
    @FXML
    protected Text collection;
    @FXML
    protected Text creator;
    @FXML
    protected Text creatorSort;
    @FXML
    protected Text processedCreation;
    @FXML
    protected Text presentCreation;
    @FXML
    protected Text dateLastCreation;
    @FXML
    protected Text ownStatus;
    @FXML
    protected Text workStatus;
    @FXML
    protected Text rating;
    @FXML
    protected Text keyWords;
    protected E entryData;

    @Override
    final protected void setMode() {
        mode = BasicModes.SHOW;
    }

    /**
     * Creates the Window with the content specified by the
     * {@link Mode} and {@link Module} of each Controller.
     */
    protected Stage loadStage() {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(GuiPaths.getPath(module, mode)));
        loader.setController(this);
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "could not load file", e);
            e.printStackTrace();
        }

        Stage stage = new Stage();
        if (root != null) {
            stage.setScene(new Scene(root));
        }
        return stage;
    }

    /**
     * Binds the {@code property} to the {@link Text#textProperty()}
     * of the given {@code Text} unidirectional.
     *
     * @param text     text to bindByOwn to
     * @param property property which provides the content
     */
    void bindToText(Text text, StringProperty property) {
        text.textProperty().bind(property);
    }

    /**
     * Opens a new Window of {@link Module} of this instance
     * and in {@link BasicModes#EDIT}.
     */
    @FXML
    protected void openEdit() {
        EntrySingleton.getInstance().setEntry(entryData);
        ControlComm.getInstance().getController(module, BasicModes.EDIT).open();
    }

    /**
     * Binds the entry to the nodes of this instance.
     */
    protected void bindEntry() {
        //bindByOwn properties to Text nodes
        bindToText(workStatus, entryData.getCreation().workStatusProperty());
        bindToText(creatorSort, entryData.getCreator().sortNameProperty());
        bindToText(collection, entryData.getCreation().seriesProperty());
        bindToText(ownStatus, entryData.getUser().ownStatusProperty());
        bindToText(creator, entryData.getCreator().nameProperty());
        bindToText(title, entryData.getCreation().titleProperty());
        bindToText(dateLastCreation, entryData.getCreation().dateLastPortionProperty());
        bindToText(keyWords, entryData.getUser().keyWordsProperty());

        //binds integer properties to nodes
        processedCreation.textProperty().bindBidirectional(entryData.getUser().processedPortionProperty(), new NumberStringConverter(new DecimalFormat("")));
        rating.textProperty().bindBidirectional(entryData.getUser().ratingProperty(), new NumberStringConverter(new DecimalFormat("")));
        presentCreation.textProperty().bindBidirectional(entryData.getCreation().numPortionProperty(), new NumberStringConverter(new DecimalFormat("")));

        //binds comment to the TextArea
        commentArea.textProperty().bindBidirectional(entryData.getUser().commentProperty());

        //sets the CoverImage
        File imageFile = new File(entryData.getCreation().getCoverPath());
        Image image = new Image(imageFile.toURI().toString());
        coverImage.setImage(image);

    }

    /**
     * Binds the loaded entry to the Nodes of this instance,
     * if it is not null.
     * Sets every Text instance to 'N/A' if it is null.
     */
    protected void loadEntry() {
        if (entryData == null) {
            for (Node node : editBtn.getParent().getChildrenUnmodifiable()) {
                if (node instanceof Text) {
                    ((Text) node).setText("N/A");
                }
            }
        } else {
            bindEntry();
        }
    }
}
