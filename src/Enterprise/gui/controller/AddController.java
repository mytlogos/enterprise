package Enterprise.gui.controller;

import Enterprise.data.Default;
import Enterprise.data.impl.SimpleCreation;
import Enterprise.data.impl.SimpleCreationEntry;
import Enterprise.data.impl.SimpleCreator;
import Enterprise.data.impl.SimpleUser;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.User;
import Enterprise.gui.general.BasicModes;
import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * This class holds common functionality and fields of the several Controllers of Mode {@code ADD}.
 */
public abstract class AddController<E extends Enum<E> & Module> extends ModifyEntry<E, BasicModes> implements InputLimiter {
    @FXML
    protected Button addBtn;

    protected CreationEntry getCreationEntry(BasicModules module) {
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

        return new SimpleCreationEntry(user, creation, creator, module);
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
    protected abstract void add();

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

        return new SimpleUser(wnStatus,comment,ratng, Default.STRING,readChapter,kyWords);
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
        if(coverPath != null && !coverPath.isEmpty()){
            creation = new SimpleCreation(srs, ttl, coverPath, numberChapters, dtLstChapter, workStat);
        }else{
            creation = new SimpleCreation(srs, ttl, Default.IMAGE, numberChapters, dtLstChapter, workStat);
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
        return new SimpleCreator(author, authorSort);
    }
}
