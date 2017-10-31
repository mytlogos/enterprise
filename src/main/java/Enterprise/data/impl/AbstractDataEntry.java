package Enterprise.data.impl;

import Enterprise.data.EnterpriseEntry;
import Enterprise.data.intface.DataEntry;
import Enterprise.data.intface.DataTable;
import Enterprise.data.intface.Table;

/**
 *
 */
public abstract class AbstractDataEntry extends EnterpriseEntry implements DataEntry {
    private int dataId;

    public AbstractDataEntry(int id) {
        dataId = id;
    }

    protected AbstractDataEntry() {

    }

    @Override
    public int getId() {
        return dataId;
    }

    @Override
    final public void setId(int id, Table table) {
        if (!(table instanceof DataTable)) {
            throw new IllegalAccessError();
        }
        if (id <= 0) {
            throw new IllegalArgumentException("should not be smaller than 1: " + id);
        }
        this.dataId = id;
    }
}
