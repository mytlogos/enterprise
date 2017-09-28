package Enterprise.data.impl;

import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.Creator;
import Enterprise.data.intface.User;
import Enterprise.modules.BasicModules;

/**
 * Simple container class, representing an Entry of an Entertainment-element like a book, with user interactions,
 * meaning, in the context of the user processing the creation.
 * @see CreationEntry
 */
public class CreationEntryImpl extends AbstractCreationEntry implements CreationEntry {

    /**
     * The constructor of {@code CreationEntryImpl}.
     *
     * @param user {@link User} which processes said {@link Creation}
     * @param creation Creation to be processed
     * @param creator {@link Creator} of said Creation
     * @param module {@link BasicModules} of this {@code CreationEntryImpl}
     */
    public CreationEntryImpl(User user, Creation creation, Creator creator, BasicModules module) {
        this.user = user;
        this.creation = creation;
        this.creation.setCreator(creator);
        this.module = module;

        validateState();
        incrementReferences(user,creation,creator);
        bindUpdated();
    }

    /**
     * Validates the State of this {@code CreationEntryImpl}
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
            throw new IllegalArgumentException(message);
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
    public boolean isUpdated() {
        return updated.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreationEntryImpl)) return false;

        CreationEntryImpl that = (CreationEntryImpl) o;
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

}
