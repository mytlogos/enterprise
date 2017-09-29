package Enterprise.data.impl;

import Enterprise.data.EnterpriseEntry;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.DataEntry;
import Enterprise.data.intface.User;
import Enterprise.modules.Module;

import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 */
abstract class AbstractCreationEntry extends EnterpriseEntry implements CreationEntry {
    User user;
    Creation creation;
    Module module;

    private static Map<DataEntry, Integer> references = new WeakHashMap<>();

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

    void decrementReferences(DataEntry... bases) {
        for (DataEntry dataEntry : bases) {
            checkSupport(dataEntry);

            if (references.containsKey(dataEntry)) {
                int count = references.get(dataEntry);
                references.put(dataEntry, --count);
                if (count == 0) {
                    references.remove(dataEntry);
                    dataEntry.setDead();
                }
            }
        }
    }

    boolean checkOnlyReference(DataEntry dataEntry) {
        boolean checked = false;

        if (references.containsKey(dataEntry) && references.get(dataEntry) == 1) {
            checked = true;
        }
        return checked;
    }

    private void checkSupport(DataEntry dataEntry) {
        if (dataEntry instanceof CreationEntry) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void setUpdated() {
        user.setUpdated();
        creation.setUpdated();
        getCreator().setUpdated();
    }

    @Override
    public void fromDataBase() {
        user.fromDataBase();
        creation.fromDataBase();
        getCreator().fromDataBase();

        setUpdated();
        setEntryOld();
    }
}
