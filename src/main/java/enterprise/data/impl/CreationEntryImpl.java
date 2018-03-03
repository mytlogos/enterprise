package enterprise.data.impl;

import enterprise.data.intface.Creation;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.Creator;
import enterprise.data.intface.User;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import gorgon.external.GorgonEntry;

/**
 * Simple container class, representing an Entry of an Entertainment-element like a book, with user interactions,
 * meaning, in the context of the user processing the creation.
 *
 * @see CreationEntry
 */
public class CreationEntryImpl extends AbstractCreationEntry implements CreationEntry {

    CreationEntryImpl() {

    }

    /**
     * The constructor of {@code CreationEntryImpl}.
     *
     * @param user     {@link User} which processes said {@link Creation}
     * @param creation Creation to be processed
     * @param creator  {@link Creator} of said Creation
     * @param module   {@link BasicModule} of this {@code CreationEntryImpl}
     */
    public CreationEntryImpl(User user, Creation creation, Creator creator, Module module) {
        this.user = user;
        this.creation = creation;
        this.creation.setCreator(creator);
        this.module = module;

        validateState();
        incrementReferences(user, creation, creator);
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
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + creation.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreationEntryImpl)) return false;

        CreationEntryImpl that = (CreationEntryImpl) o;
        return user.equals(that.user) && creation.equals(that.creation);
    }

    @Override
    public String toString() {
        return "CreationEntryImpl{" +
                "user=" + user +
                ", creation=" + creation +
                ", module=" + module +
                '}';
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof CreationEntry)) return -1;

        CreationEntry o = (CreationEntry) gorgonEntry;
        int compared = getCreation().compareTo(o.getCreation());
        if (compared == 0) {
            compared = getUser().compareTo(o.getUser());
        }
        if (compared == 0) {
            compared = getCreator().compareTo(o.getCreator());
        }
        return compared == 0 ? getModule().toString().compareTo(o.getModule().toString()) : compared;
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
    public Module getModule() {
        return module;
    }

}
