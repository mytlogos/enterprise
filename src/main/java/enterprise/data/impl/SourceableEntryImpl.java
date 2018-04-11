package enterprise.data.impl;

import enterprise.data.intface.*;
import enterprise.modules.Module;
import gorgon.external.GorgonEntry;
import scrape.sources.posts.PostManager;

/**
 * Simple Implementation of {@code SourceableEntry}.
 *
 * @see SourceableEntry
 */
public class SourceableEntryImpl extends AbstractCreationEntry implements SourceableEntry {
    private Sourceable sourceable;

    SourceableEntryImpl() {

    }

    /**
     * The constructor of {@code SourceableEntryImpl}
     *
     * @param user       user to be set to this entry
     * @param creation   creation to be set to this entry
     * @param creator    creator to be set to this entry
     * @param sourceable sourceable to be set to this entry
     * @param module     module to be set to this entry
     */
    public SourceableEntryImpl(User user, Creation creation, Creator creator, Sourceable sourceable, Module module) {
        this.user = user;
        this.creation = creation;
        this.module = module;
        this.sourceable = sourceable;
        validateState();

        incrementReferences(user, creation, creator, sourceable);

        sourceable.getSources().forEach(this::incrementReferences);

        this.creation.setCreator(creator);
        this.sourceable.setUser(user);
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + creation.hashCode();
        return result;
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof SourceableEntry)) return -1;

        SourceableEntry o = (SourceableEntry) gorgonEntry;
        int compared = getCreation().compareTo(o.getCreation());

        if (compared == 0) {
            compared = getModule().toString().compareTo(getModule().toString());
        }
        if (compared == 0) {
            compared = getUser().compareTo(o.getUser());
        }
        if (o instanceof SourceableEntryImpl) {
            if (compared == 0) {
                compared = getSourceable().compareTo(o.getSourceable());
            }
        }
        return compared;
    }

    @Override
    public String toString() {
        return "SourceableEntryImpl{" +
                "sourceable=" + sourceable +
                ", user=" + user +
                ", creation=" + creation +
                ", module=" + module +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SourceableEntryImpl)) return false;

        SourceableEntryImpl that = (SourceableEntryImpl) o;

        return creation.equals(that.creation)
                && module == that.module
                && user.equals(that.user)
                && sourceable.equals(that.sourceable);
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public boolean readySourceableRemoval() {
        boolean onlyReference = checkOnlyReference(sourceable);
        decrementReferences(sourceable);

        PostManager.getInstance().removeSearchEntries(this);
        sourceable.getSources().forEach(this::decrementReferences);
        return onlyReference;
    }

    @Override
    void setUser(User user) {
        super.setUser(user);

        if (sourceable != null) {
            sourceable.setUser(user);
        }
    }

    @Override
    public Creation getCreation() {
        return creation;
    }

    @Override
    public Creator getCreator() {
        return creation.getCreator();
    }

    void setSourceable(Sourceable sourceable) {
        this.sourceable = sourceable;

        if (sourceable != null) {
            sourceable.setUser(user);
        }
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public Sourceable getSourceable() {
        return sourceable;
    }

    /**
     * validates the State of this {@code SourceableEntryImpl}
     *
     * @throws IllegalArgumentException if any dataField is invalid
     */
    private void validateState() {
        String message = "";
        if (user == null) {
            message = message + "user is null, ";
        }
        if (creation == null) {
            message = message + "creation is null, ";
        }
        if (sourceable == null) {
            message = message + "sourceable is null";
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
