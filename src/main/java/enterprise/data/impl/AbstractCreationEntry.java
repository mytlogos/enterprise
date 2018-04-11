package enterprise.data.impl;

import enterprise.data.intface.Creation;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.DataEntry;
import enterprise.data.intface.User;
import enterprise.modules.Module;

import java.util.WeakHashMap;

/**
 *
 */
public abstract class AbstractCreationEntry extends AbstractDataEntry implements CreationEntry {
    private static final WeakHashMap<DataEntry, Integer> references = new WeakHashMap<>();
    User user;
    Creation creation;
    Module module;

    @Override
    public boolean readyUserRemoval() {
        boolean onlyReference = checkOnlyReference(user);
        decrementReferences();
        return onlyReference;
    }

    @Override
    public boolean readyCreationRemoval() {
        boolean onlyReference = checkOnlyReference(creation);
        decrementReferences();
        return onlyReference;
    }

    @Override
    public boolean readyCreatorRemoval() {
        boolean onlyReference = checkOnlyReference(getCreator());
        decrementReferences();
        return onlyReference;
    }

    boolean checkOnlyReference(DataEntry dataEntry) {
        boolean checked = false;

        if (references.containsKey(dataEntry) && references.get(dataEntry) == 1) {
            checked = true;
        }
        return checked;
    }

    void decrementReferences(DataEntry... bases) {
        for (DataEntry dataEntry : bases) {
            checkSupport(dataEntry);

            if (references.containsKey(dataEntry)) {
                int count = references.get(dataEntry);
                references.put(dataEntry, --count);
                if (count == 0) {
                    references.remove(dataEntry);
                }
            }
        }
    }

    void setUser(User user) {
        this.user = user;
    }

    private void checkSupport(DataEntry dataEntry) {
        if (dataEntry instanceof CreationEntry) {
            throw new UnsupportedOperationException();
        }
    }

    void incrementReferences(DataEntry... dataEntries) {
        for (DataEntry dataEntry : dataEntries) {
            checkSupport(dataEntry);
            incCreationRef(dataEntry);
        }
    }

    private void incCreationRef(DataEntry base) {
        if (references.containsKey(base)) {
            int count = references.get(base);
            references.put(base, ++count);
        } else {
            references.put(base, 1);
        }
    }


}
