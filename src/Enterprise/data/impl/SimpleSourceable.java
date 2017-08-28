package Enterprise.data.impl;

import Enterprise.data.Default;
import Enterprise.data.EnterpriseEntry;
import Enterprise.data.intface.DataTable;
import Enterprise.data.intface.Sourceable;
import Enterprise.data.intface.Table;
import Enterprise.data.intface.User;
import Enterprise.misc.SQL;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scrape.sources.SourceList;

import java.util.logging.Level;

/**
 * Implementation of Sourceable
 * @see Sourceable
 */
public class SimpleSourceable extends EnterpriseEntry implements Sourceable{
    private int sourceableId;
    private static int idCounter = 1;

    private SourceList sourceList = new SourceList();

    private User user;
    @SQL
    private StringProperty translator = new SimpleStringProperty();

    private BooleanProperty sourceListChanged = new SimpleBooleanProperty(false);
    private BooleanProperty translatorChanged = new SimpleBooleanProperty(false);

    /**
     *The constructor of {@code SimpleSourceable}
     */
    public SimpleSourceable() {
        this(Default.VALUE,new SourceList(), Default.STRING);
        user = new SimpleUser();
    }

    /**
     * The constructor of {@code SimpleSourceable}
     *
     * @param sourceList list of sources for this {@code SimpleSourceable}
     * @param translator translator of this {@code SimpleSourceable}
     */
    public SimpleSourceable(SourceList sourceList, String translator) {
        this(Default.VALUE, sourceList, translator);
    }

    /**
     * The constructor of {@code SimpleSourceable}
     * @param id database id of this {@code SimpleSourceable}
     * @param sourceList list of sources for this {@code SimpleSourceable}
     * @param translator translator of this {@code SimpleSourceable}
     */
    public SimpleSourceable(int id, SourceList sourceList, String translator) {
        this.sourceList = sourceList;
        this.translator.set(translator);

        if (id == 0) {
            sourceableId = idCounter;
            idCounter++;
        } else {
            sourceableId = id;
            if (idCounter <= id) {
                idCounter = id;
                idCounter++;
            }
        }
        validateState();
        invalidListener();
        bindUpdated();
    }

    /**
     * validates the State of this {@code SimpleSourceable}
     *
     * @throws IllegalArgumentException if any fields are {@code null} or invalid
     */
    private void validateState() {
        String message = "";
        if (sourceableId < 0) {
            message = message + "Id is invalid: " + sourceableId + ", ";
        }
        if (sourceList == null) {
            message = message + "sourceList is null, ";
        }
        if (translator.get() == null) {
            message = message + "translator is null";
        }
        if (!message.isEmpty()) {
            IllegalArgumentException exception = new IllegalArgumentException(message);
            logger.log(Level.WARNING, "object creation failed", exception);
            throw exception;
        }
    }

    @Override
    public boolean isSourceListChanged() {
        return sourceListChanged.get();
    }

    @Override
    public boolean isTranslatorChanged() {
        return translatorChanged.get();
    }

    @Override
    public SourceList getSourceList() {
        return sourceList;
    }

    @Override
    public int getId() {
        return sourceableId;
    }

    @Override
    public void setId(int id, Table table) {
        if (!(table instanceof DataTable)) {
            throw new IllegalAccessError();
        }
        if (id < 1) {
            throw new IllegalArgumentException("should not be smaller than 1: " + id);
        }
        this.sourceableId = id;
    }

    @Override
    public String getTranslator() {
        return translator.get();
    }

    @Override
    public void setUpdated() {
        sourceListChanged.set(false);
        translatorChanged.set(false);
    }

    @Override
    protected void bindUpdated() {
        updated.bind(sourceListChanged.or(translatorChanged));
    }

    @Override
    public boolean isUpdated() {
        return updated.get();
    }

    @Override
    public BooleanProperty updatedProperty() {
        return updated;
    }

    /**
     * adds invalidListeners to the dataField-Properties of this {@code SimpleSourceable},
     * sets stateChanged fields to true, if state has changed
     */
    private void invalidListener() {
        sourceList.addListener((InvalidationListener) observable -> sourceListChanged.set(true));
        sourceList.listChangedProperty().addListener(observable -> sourceListChanged.set(true));
        translator.addListener(observable -> translatorChanged.set(true));
    }

    @Override
    public StringProperty translatorProperty() {
        return translator;
    }

    @Override
    public String getKeyWords() {
        if (user != null) {
            return user.getKeyWords();
        } else {
            return "";
        }
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleSourceable)) return false;

        SimpleSourceable that = (SimpleSourceable) o;

        if (!user.equals(that.user)) return false;
        return translator.get().equals(that.translator.get());
    }

    @Override
    public int hashCode() {
        int result = getSourceList() != null ? getSourceList().hashCode() : 0;
        result = 31 * result + (translator != null ? translator.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Sourceable o) {
        int compare = this.translator.get().compareTo(o.getTranslator());
        if (compare == 0) {
            compare = this.sourceList.compareTo(o.getSourceList());
        }
        return compare;
    }
}
