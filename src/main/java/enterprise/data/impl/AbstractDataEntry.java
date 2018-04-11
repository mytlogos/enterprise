package enterprise.data.impl;

import enterprise.data.intface.DataEntry;

/**
 *
 */
public abstract class AbstractDataEntry implements DataEntry {
    private int id;

    protected AbstractDataEntry() {

    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
