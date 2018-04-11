package enterprise.gui.controller;

import enterprise.data.intface.CreationEntry;
import enterprise.gui.general.BasicMode;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.NumberStringConverter;

import java.io.File;
import java.text.DecimalFormat;

/**
 * This class holds the common functionality and shareable fields
 * of all Controllers of the {@link BasicMode} {@code EDIT}.
 */
public abstract class Edit<E extends CreationEntry> extends ModifyEntry<E> {

    @FXML
    Button endEditBtn;

    @Override
    public final BasicMode getMode() {
        return BasicMode.EDIT;
    }

    /**
     * Binds the content of the editing {@link CreationEntry} to the corresponding
     * nodes.
     */
    void bindEntry() {
        bindToComboBox(workStatus, entryData.getCreation().workStatusProperty());
        bindToComboBox(creatorSort, entryData.getCreator().sortNameProperty());
        bindToComboBox(collection, entryData.getCreation().seriesProperty());
        bindToComboBox(ownStatus, entryData.getUser().ownStatusProperty());
        bindToComboBox(creator, entryData.getCreator().nameProperty());

        dateLastCreation.textProperty().bindBidirectional(entryData.getCreation().dateLastPortionProperty());

        title.textProperty().bindBidirectional(entryData.getCreation().titleProperty());

        processedCreations.textProperty().bindBidirectional(entryData.getUser().processedPortionProperty(), new NumberStringConverter(new DecimalFormat("")));

        rating.textProperty().bindBidirectional(entryData.getUser().ratingProperty(), new NumberStringConverter(new DecimalFormat("")));

        presentCreations.textProperty().bindBidirectional(entryData.getCreation().numPortionProperty(), new NumberStringConverter(new DecimalFormat("")));

        commentArea.textProperty().bindBidirectional(entryData.getUser().commentProperty());

        coverImage.setImage(new Image(new File(entryData.getCreation().getCoverPath()).toURI().toString()));

        keyWords.textProperty().bindBidirectional(entryData.getUser().keyWordsProperty());
    }

    /**
     * Binds a {@link StringProperty} bidirectional to the
     * {@link ComboBox#valueProperty()} of the provided {@code ComboBox}.
     *
     * @param box      {@code ComboBox} to bindByOwn
     * @param property {@code Property} to bindByOwn
     */
    void bindToComboBox(ComboBox<String> box, StringProperty property) {
        box.valueProperty().bindBidirectional(property);
    }

    /**
     * Unbind the {@link CreationEntry} from the {@link javafx.scene.Node}s of this {@code Edit}.
     */
    void unBindEntry() {
        //unbind from the multiple comboBoxes
        unbindFromComboBox(workStatus, entryData.getCreator().nameProperty());
        unbindFromComboBox(creatorSort, entryData.getCreator().sortNameProperty());
        unbindFromComboBox(collection, entryData.getCreation().seriesProperty());
        unbindFromComboBox(ownStatus, entryData.getUser().ownStatusProperty());
        unbindFromComboBox(creator, entryData.getCreator().nameProperty());

        //unbind the bidirectional bindByOwn
        title.textProperty().unbindBidirectional(entryData.getCreation().titleProperty());
        presentCreations.textProperty().unbindBidirectional(entryData.getCreation().numPortionProperty());

        dateLastCreation.textProperty().unbindBidirectional(entryData.getCreation().dateLastPortionProperty());
        processedCreations.textProperty().unbindBidirectional(entryData.getUser().processedPortionProperty());

        rating.textProperty().unbindBidirectional(entryData.getUser().ratingProperty());
        commentArea.textProperty().unbindBidirectional(entryData.getUser().commentProperty());

        //unbind the unidirectional bindByOwn
        entryData.getCreator().nameProperty().unbind();
        entryData.getCreator().sortNameProperty().unbind();
        entryData.getCreation().seriesProperty().unbind();

        entryData.getUser().ownStatusProperty().unbind();
        entryData.getCreator().statusProperty().unbind();

        if (coverPath != null) {
            if (!entryData.getCreation().getCoverPath().equalsIgnoreCase(coverPath)) {
                entryData.getCreation().setCoverPath(coverPath);
            }
        }

        keyWords.textProperty().unbindBidirectional(entryData.getUser().keyWordsProperty());
    }

    /**
     * Unbinds a bidirectional Bind between the {@link StringProperty} and the
     * {@link ComboBox#valueProperty()} of the provided {@code ComboBox}.
     *
     * @param box      {@code ComboBox} to unbind
     * @param property {@code Property} to unbind
     */
    void unbindFromComboBox(ComboBox<String> box, StringProperty property) {
        box.valueProperty().unbindBidirectional(property);
    }

    /**
     * Fires an {@link WindowEvent#WINDOW_CLOSE_REQUEST} event for this stage.
     */
    @FXML
    protected void endEdit() {
        Stage stage = (Stage) title.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
