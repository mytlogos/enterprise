package Enterprise.data.impl;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.*;
import Enterprise.modules.BasicModules;
import javafx.beans.property.BooleanProperty;

import java.util.logging.Level;

/**
 * Simple container class, representing an Entry of an Entertainment-element like a book, with user interactions,
 * meaning, in the context of the user processing the creation.
 * @see CreationEntry
 */
public class SimpleCreationEntry extends AbstractCreationEntry implements CreationEntry {

    /**
     * The constructor of {@code SimpleCreationEntry}.
     *
     * @param user {@link User} which processes said {@link Creation}
     * @param creation Creation to be processed
     * @param creator {@link Creator} of said Creation
     * @param module {@link BasicModules} of this {@code SimpleCreationEntry}
     */
    public SimpleCreationEntry(User user, Creation creation, Creator creator, BasicModules module) {
        this.user = user;
        this.creation = creation;
        this.creation.setCreator(creator);
        this.module = module;

        validateState();
        incrementReferences(user,creation,creator);
        bindUpdated();
    }

    /**
     * Validates the State of this {@code SimpleCreationEntry}
     *
     * @throws IllegalArgumentException if an argument is null or invalid
     */
    private void validateState() {
        String message = "";
        if (user == null) {
            message = message + "user is null, ";
        }
        if (creation == null) {
            message = message + "creation is null, ";
        }
        if (!message.isEmpty()) {
            IllegalArgumentException exception = new IllegalArgumentException(message);
            logger.log(Level.WARNING, "object creation failed", exception);
            throw exception;
        }
    }

    @Override
    protected void bindUpdated() {
        updated.bind(user.updatedProperty().or(creation.updatedProperty()).or(creation.getCreator().updatedProperty()));
        updated.addListener((observable, oldValue, newValue) ->{
            if (newValue && !newEntry) {
                OpEntryCarrier.getInstance().addUpdateEntry(this);
            }
        });
    }

    @Override
    public boolean readyUserRemoval() {
        decrementReferences();
        return checkOnlyReference(user);
    }

    @Override
    public boolean readyCreationRemoval() {
        decrementReferences();
        return checkOnlyReference(creation);
    }

    @Override
    public boolean readyCreatorRemoval() {
        decrementReferences();
        return checkOnlyReference(getCreator());
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public Creation getCreation() {
        return creation;
    }

    @Override
    public Creator getCreator() {
        return creation.getCreator();
    }

    @Override
    public void setUpdated() {
        user.setUpdated();
        creation.setUpdated();
        creation.getCreator().setUpdated();
    }

    @Override
    public boolean isUpdated() {
        return updated.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleCreationEntry)) return false;

        SimpleCreationEntry that = (SimpleCreationEntry) o;

        return user.equals(that.user) && creation.equals(that.creation);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + creation.hashCode();
        return result;
    }

    @Override
    public int compareTo(CreationEntry o) {
        int compared;
        if (this == o) {
            compared = 0;
        } else {
            compared = this.creation.compareTo(o.getCreation());
        }
        return compared;
    }

    @Override
    public BasicModules getModule() {
        return module;
    }

    @Override @Deprecated
    public BooleanProperty updatedProperty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId(int id, Table table) {
        throw new UnsupportedOperationException();
    }
}
