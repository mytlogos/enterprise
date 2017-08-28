package Enterprise.data;

import Enterprise.data.intface.Entry;
import javafx.beans.property.*;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Basic implementation of {@code Entry}.
 * Any class which implements an Interface extending from {@code Entry},
 * should extend this class.
 * Basically is this class the basic of all Data Model Classes for user input
 * data.
 */
public abstract class EnterpriseEntry implements Entry {

    protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    {
        try {
            //handler for this class and all subclasses
            FileHandler fileHandler = new FileHandler(
                    "log\\" + this.getClass().getSimpleName() + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    private boolean dead = false;
    protected boolean newEntry = false;
    protected BooleanProperty updated = new SimpleBooleanProperty(false);


    /**
     * The constructor of this {@code EnterpriseEntry}.
     */
    protected EnterpriseEntry() {
        setEntryNew();
    }

    @Override
    public void setDead() {
        this.dead = true;
    }

    @Override
    public void setEntryNew() {
        this.newEntry = true;
    }

    @Override
    public void setEntryOld() {
        this.newEntry = false;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public boolean isNewEntry() {
        return newEntry;
    }

    @Override
    public void setAlive() {
        dead = false;
    }

    /**
     * binds an BooleanProperty field named "updated".
     * It is important to call this method in the constructors
     * of the varying implementations, because the update function
     * of the DAO classes need an up-to-date indicator of the inner state.
     */
    protected abstract void bindUpdated();
}
