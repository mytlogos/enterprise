package Enterprise.gui.controller;

import Enterprise.ControlComm;
import Enterprise.data.Default;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.impl.CreationEntryImpl;
import Enterprise.data.impl.CreationImpl;
import Enterprise.data.impl.CreatorImpl;
import Enterprise.data.impl.SimpleUser;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.User;
import Enterprise.gui.controller.content.SeriesController;
import Enterprise.gui.general.BasicModes;
import Enterprise.modules.Module;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * This class holds common functionality and fields of the several Controllers of Mode {@code ADD}.
 */
public abstract class AddController<E extends Enum<E> & Module> extends ModifyEntry<E, BasicModes> implements InputLimiter {
    @FXML
    protected Button addBtn;

    protected CreationEntry getCreationEntry(Module module) {
        Creator creator = null;
        Creation creation = null;
        User user = null;
        try {
            creator = getCreator();
            creation = getCreation();
            user = getUser();
        } catch (IllegalArgumentException e) {
            // TODO: 24.08.2017 show error message
            e.printStackTrace();
        }

        return new CreationEntryImpl(user, creation, creator, module);
    }

    @Override
    protected final void setMode() {
        mode = BasicModes.ADD;
    }

    /**
     * Creates a {@link Enterprise.data.intface.CreationEntry} and adds it to the
     * {@link javafx.scene.control.TableView} of the Content Controller and their
     * corresponding {@link Enterprise.modules.Module} entry list.
     * Makes the {@code CreationEntry} available for adding it to the database.
     */
    @FXML
    protected void add() {
        CreationEntry entry = getCreationEntry(module);

        //add if this entry is not contained in the entry list of module
        if (module.addEntry(entry)) {

            //add it to the TableView of the Content Display
            SeriesController controller = (SeriesController) ControlComm.getInstance().getController(module, BasicModes.CONTENT);
            controller.addEntry(entry);

            //ready for adding it to the database
            OpEntryCarrier.getInstance().addNewEntry(entry);
        } else {
            //show alert for trying to add an already existing entry
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eintrag existiert schon!");
            alert.show();
        }
        //close this window either way
        Stage stage = (Stage) addBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * Constructs an {@code User} from the data input.
     *
     * @return user
     */
    protected User getUser() {
        //parameter for User
        String kyWords = validateStringInput(keyWords);
        int readChapter = validateIntInput(processedCreations);
        int ratng = validateIntInput(rating);
        String wnStatus = validateStringInput(ownStatus);
        String comment = validateStringInput(commentArea);

        return new SimpleUser(wnStatus, comment, ratng, Default.LIST, readChapter, kyWords);
    }

    /**
     * Constructs a {@code Creation} from the data input.
     *
     * @return creation
     */
    protected Creation getCreation() {
        //parameter for Creation
        String srs = validateStringInput(collection);
        String ttl = validateStringInput(title);
        int numberChapters = validateIntInput(presentCreations);
        String dtLstChapter = validateStringInput(dateLastCreation);
        String workStat = validateStringInput(workStatus);

        Creation creation;
        CreationImpl.CreationImplBuilder builder = new CreationImpl.CreationImplBuilder(ttl).
                setSeries(srs).
                setNumPortion(numberChapters).
                setDateLastPortion(dtLstChapter).
                setWorkStatus(workStat);

        if (coverPath != null && !coverPath.isEmpty()) {
            creation = builder.setCoverPath(coverPath).build();
        } else {
            creation = builder.build();
        }
        return creation;
    }

    /**
     * Constructs an {@code Sourceable} from the data input.
     *
     * @return creator
     */
    protected Creator getCreator() {
        //parameter for Creator
        String author = validateStringInput(creator);
        String authorSort = validateStringInput(creatorSort);
        return new CreatorImpl.CreatorBuilder(author).setSortName(authorSort).build();
    }
}
