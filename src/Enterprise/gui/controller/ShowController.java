package Enterprise.gui.controller;

import Enterprise.modules.EnterpriseSegments;
import Enterprise.data.intface.CreationEntry;
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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The basic {@link Controller} of all {@code Controller}  with {@link Enterprise.gui.general.Mode#SHOW}.
 * Provides common fields and functionality.
 */
public abstract class ShowController<E extends CreationEntry, R extends EnterpriseSegments> implements Controller {

    protected Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

    {
        try {
            //creates a FileHandler for this Package and adds it to this logger
            FileHandler fileHandler = new FileHandler("log\\" + this.getClass().getPackage().getName() + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    protected R moduleEntry;

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

    /**
     * Creates the Window with the Content from the given {@code loader}.
     *
     * @param loader {@link FXMLLoader} which will load the content
     */
    protected Stage setWindow(FXMLLoader loader) {
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
     * @param text text to bind to
     * @param property property which provides the content
     */
    protected void bindToText(Text text, StringProperty property) {
        text.textProperty().bind(property);
    }

    /**
     * Opens a new Window of {@link Enterprise.modules.Module} of this instance
     * and in {@link Enterprise.gui.general.Mode#EDIT}.
     */
    protected abstract void openEdit();

    /**
     * Binds the entry to the nodes of this instance.
     */
    protected void bindEntry() {
        //bind properties to Text nodes
        bindToText(workStatus,entryData.getCreation().workStatusProperty());
        bindToText(creatorSort,entryData.getCreator().sortNameProperty());
        bindToText(collection,entryData.getCreation().seriesProperty());
        bindToText(ownStatus, entryData.getUser().ownStatusProperty());
        bindToText(creator, entryData.getCreator().nameProperty() );
        bindToText(title,entryData.getCreation().titleProperty());
        bindToText(dateLastCreation,entryData.getCreation().dateLastPortionProperty());

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
