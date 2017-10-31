package Enterprise.data.impl;

import Enterprise.data.intface.*;
import Enterprise.modules.Module;
import scrape.sources.posts.PostManager;

/**
 * Simple Implementation of {@code SourceableEntry}.
 *
 * @see SourceableEntry
 */
public class SourceableEntryImpl extends AbstractCreationEntry implements SourceableEntry {
    private Sourceable sourceable;

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

        sourceable.getSourceList().forEach(this::incrementReferences);

        this.creation.setCreator(creator);
        this.sourceable.setUser(user);
    }

    @Override
    public boolean readySourceableRemoval() {
        boolean onlyReference = checkOnlyReference(sourceable);
        decrementReferences(sourceable);

        PostManager.getInstance().removeSearchEntries(this);

        sourceable.getSourceList().forEach(source -> {
            if (checkOnlyReference(source)) {
                source.setDead();
            }
        });
        sourceable.getSourceList().forEach(this::decrementReferences);

        return onlyReference;
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
    public void fromDataBase() {
        super.fromDataBase();
        sourceable.fromDataBase();
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
            if (compared == 0) {
                compared = module.toString().compareTo(getModule().toString());
            }
            if (compared == 0) {
                compared = user.compareTo(o.getUser());
            }
            if (o instanceof SourceableEntryImpl) {
                if (compared == 0) {
                    compared = sourceable.compareTo(((SourceableEntryImpl) o).getSourceable());
                }
            }
        }
        return compared;
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
