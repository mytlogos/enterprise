package Enterprise.data;

import Enterprise.data.intface.Entry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Basic implementation of {@code Entry}.
 * Any class which implements an Interface extending from {@code Entry},
 * should extend this class.
 * Basically is this class the basic of all Data Model Classes for user input
 * data.
 */
public abstract class EnterpriseEntry implements Entry {
    protected boolean newEntry = false;
    protected BooleanProperty updated = new SimpleBooleanProperty(false);
    private boolean dead = false;


    /**
     * The constructor of this {@code EnterpriseEntry}.
     */
    protected EnterpriseEntry() {
        setEntryNew();
    }

    @Override
    final public void setDead() {
        this.dead = true;
    }

    @Override
    final public void setEntryNew() {
        this.newEntry = true;
    }

    @Override
    final public void setEntryOld() {
        this.newEntry = false;
    }

    @Override
    final public boolean isDead() {
        return dead;
    }

    @Override
    final public boolean isNewEntry() {
        return newEntry;
    }

    @Override
    final public void setAlive() {
        dead = false;
    }

    @Override
    public void fromDataBase() {
        setEntryOld();
    }
}
