package Enterprise.gui.controller;

import Enterprise.data.intface.CreationEntry;
import Enterprise.gui.general.BasicModes;
import Enterprise.modules.Module;
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
 * of all Controllers of the {@link BasicModes} {@code EDIT}.
 */
public abstract class EditController<E extends CreationEntry, R extends Enum<R> & Module> extends ModifyEntry<R, BasicModes> {

    protected E creationEntry;

    @FXML
    protected Button endEditBtn;

    @Override
    protected final void setMode() {
        mode = BasicModes.EDIT;
    }

    /**
     * Fires an {@link WindowEvent#WINDOW_CLOSE_REQUEST} event for this stage.
     */
    @FXML
    protected void endEdit() {
        Stage stage = (Stage) title.getScene().getWindow();
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    /**
     * Binds the content of the editing {@link CreationEntry} to the corresponding
     * nodes.
     */
    protected void bindEntry() {
        bindToComboBox(workStatus, creationEntry.getCreation().workStatusProperty());
        bindToComboBox(creatorSort, creationEntry.getCreator().sortNameProperty());
        bindToComboBox(collection, creationEntry.getCreation().seriesProperty());
        bindToComboBox(ownStatus, creationEntry.getUser().ownStatusProperty());
        bindToComboBox(creator, creationEntry.getCreator().nameProperty());

        dateLastCreation.textProperty().bindBidirectional(creationEntry.getCreation().dateLastPortionProperty());

        title.textProperty().bindBidirectional(creationEntry.getCreation().titleProperty());

        processedCreations.textProperty().bindBidirectional(creationEntry.getUser().processedPortionProperty(), new NumberStringConverter(new DecimalFormat("")));

        rating.textProperty().bindBidirectional(creationEntry.getUser().ratingProperty(), new NumberStringConverter(new DecimalFormat("")));

        presentCreations.textProperty().bindBidirectional(creationEntry.getCreation().numPortionProperty(), new NumberStringConverter(new DecimalFormat("")));

        commentArea.textProperty().bindBidirectional(creationEntry.getUser().commentProperty());

        coverImage.setImage(new Image(new File(creationEntry.getCreation().getCoverPath()).toURI().toString()));

        keyWords.textProperty().bindBidirectional(creationEntry.getUser().keyWordsProperty());
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
     * Unbind the {@link CreationEntry} from the {@link javafx.scene.Node}s of this {@code EditController}.
     */
    protected void unBindEntry() {
        //unbind from the multiple comboBoxes
        unbindFromComboBox(workStatus, creationEntry.getCreator().nameProperty());
        unbindFromComboBox(creatorSort, creationEntry.getCreator().sortNameProperty());
        unbindFromComboBox(collection, creationEntry.getCreation().seriesProperty());
        unbindFromComboBox(ownStatus, creationEntry.getUser().ownStatusProperty());
        unbindFromComboBox(creator, creationEntry.getCreator().nameProperty());

        //unbind the bidirectional bindByOwn
        title.textProperty().unbindBidirectional(creationEntry.getCreation().titleProperty());
        presentCreations.textProperty().unbindBidirectional(creationEntry.getCreation().numPortionProperty());

        dateLastCreation.textProperty().unbindBidirectional(creationEntry.getCreation().dateLastPortionProperty());
        processedCreations.textProperty().unbindBidirectional(creationEntry.getUser().processedPortionProperty());

        rating.textProperty().unbindBidirectional(creationEntry.getUser().ratingProperty());
        commentArea.textProperty().unbindBidirectional(creationEntry.getUser().commentProperty());

        //unbind the unidirectional bindByOwn
        creationEntry.getCreator().nameProperty().unbind();
        creationEntry.getCreator().sortNameProperty().unbind();
        creationEntry.getCreation().seriesProperty().unbind();

        creationEntry.getUser().ownStatusProperty().unbind();
        creationEntry.getCreator().statusProperty().unbind();

        if (coverPath != null) {
            if (!creationEntry.getCreation().getCoverPath().equalsIgnoreCase(coverPath)) {
                creationEntry.getCreation().setCoverPath(coverPath);
            }
        }

        keyWords.textProperty().unbindBidirectional(creationEntry.getUser().keyWordsProperty());
    }
}
