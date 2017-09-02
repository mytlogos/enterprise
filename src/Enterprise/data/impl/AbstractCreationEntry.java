package Enterprise.data.impl;

import Enterprise.data.EnterpriseEntry;
import Enterprise.data.intface.Creation;
import Enterprise.data.intface.CreationEntry;
import Enterprise.data.intface.DataBase;
import Enterprise.data.intface.User;
import Enterprise.modules.BasicModules;

import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 */
abstract class AbstractCreationEntry extends EnterpriseEntry {
    User user;
    Creation creation;
    BasicModules module;

    private static Map<DataBase, Integer> references = new WeakHashMap<>();

    void incrementReferences(DataBase... dataBases) {
        for (DataBase dataBase : dataBases) {
            checkSupport(dataBase);
            incCreationRef(dataBase);
        }
    }

    private void incCreationRef(DataBase base) {
        if (references.containsKey(base)) {
            int count = references.get(base);
            references.put(base, ++count);
        } else {
            references.put(base, 1);
        }
    }

    void decrementReferences(DataBase... bases) {
        for (DataBase dataBase : bases) {
            checkSupport(dataBase);

            if (references.containsKey(dataBase)) {
                int count = references.get(dataBase);
                references.put(dataBase, --count);
                if (count == 0) {
                    references.remove(dataBase);
                    dataBase.setDead();
                }
            }
        }
    }

    boolean checkOnlyReference(DataBase dataBase) {
        boolean checked = false;

        if (references.containsKey(dataBase) && references.get(dataBase) == 1) {
            checked = true;
        }
        return checked;
    }

    private void checkSupport(DataBase dataBase) {
        if (dataBase instanceof CreationEntry) {
            throw new UnsupportedOperationException();
        }
    }
}
