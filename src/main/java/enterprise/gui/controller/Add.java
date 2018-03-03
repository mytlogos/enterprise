package enterprise.gui.controller;

import enterprise.ControlComm;
import enterprise.data.Default;
import enterprise.data.EntryCarrier;
import enterprise.data.impl.CreationBuilder;
import enterprise.data.impl.CreationEntryImpl;
import enterprise.data.impl.CreatorImpl;
import enterprise.data.impl.UserImpl;
import enterprise.data.intface.Creation;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.Creator;
import enterprise.data.intface.User;
import enterprise.gui.general.BasicMode;
import enterprise.modules.Module;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * This class holds common functionality and fields of the several Controllers of Mode {@code ADD}.
 */
public abstract class Add extends ModifyEntry implements InputLimiter {
    @FXML
    protected Button addBtn;

    @Override
    public final BasicMode getMode() {
        return BasicMode.ADD;
    }

    /**
     * Creates a {@link enterprise.data.intface.CreationEntry} and adds it to the
     * {@link javafx.scene.control.TableView} of the Content Controller and their
     * corresponding {@link enterprise.modules.Module} entry list.
     * Makes the {@code CreationEntry} available for adding it to the database.
     */
    @FXML
    protected void add() {
        CreationEntry entry = getCreationEntry(getModule());

        //add if this entry is not contained in the entry list of module
        if (getModule().addEntry(entry)) {

            //add it to the TableView of the Content Display
            Content<CreationEntry> controller = (Content) ControlComm.get().getController(getModule(), BasicMode.CONTENT);
            controller.addEntry(entry);

            //ready for adding it to the database
            EntryCarrier.getInstance().addNewEntry(entry);
        } else {
            //show alert for trying to add an already existing entry
            Alert alert = new Alert(Alert.AlertType.ERROR, "Eintrag existiert schon!");
            alert.show();
        }
        //close this window either way
        Stage stage = (Stage) addBtn.getScene().getWindow();
        stage.close();
    }

    CreationEntry getCreationEntry(Module module) {
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

    /**
     * Constructs an {@code Sourceable} from the data input.
     *
     * @return creator
     */
    private Creator getCreator() {
        //parameter for Creator
        String author = validateStringInput(creator);
        String authorSort = validateStringInput(creatorSort);
        return new CreatorImpl.CreatorBuilder(author).setSortName(authorSort).build();
    }

    /**
     * Constructs a {@code Creation} from the data input.
     *
     * @return creation
     */
    private Creation getCreation() {
        //parameter for Creation
        String srs = validateStringInput(collection);
        String ttl = validateStringInput(title);
        int numberChapters = validateIntInput(presentCreations);
        String dtLstChapter = validateStringInput(dateLastCreation);
        String workStat = validateStringInput(workStatus);

        Creation creation;
        CreationBuilder builder = new CreationBuilder(ttl).
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
     * Constructs an {@code User} from the data input.
     *
     * @return user
     */
    private User getUser() {
        //parameter for User
        String kyWords = validateStringInput(keyWords);
        int readChapter = validateIntInput(processedCreations);
        int ratng = validateIntInput(rating);
        String wnStatus = validateStringInput(ownStatus);
        String comment = validateStringInput(commentArea);

        return new UserImpl(wnStatus, comment, ratng, Default.LIST, readChapter, kyWords);
    }
}
