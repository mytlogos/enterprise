package Enterprise.data.impl;

import Enterprise.data.Default;
import Enterprise.data.OpEntryCarrier;
import Enterprise.data.intface.Entry;
import Enterprise.data.intface.Sourceable;
import Enterprise.data.intface.User;
import Enterprise.misc.DataAccess;
import Enterprise.misc.SQLUpdate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scrape.sources.SourceList;

import java.util.List;

/**
 * Implementation of Sourceable
 *
 * @see Sourceable
 */
@DataAccess(daoClass = "SourceableTable")
public class SourceableImpl extends AbstractDataEntry implements Sourceable {
    private SourceList sourceList = new SourceList();

    private User user;

    @SQLUpdate(stateGet = "isTranslatorChanged", valueGet = "getTranslator", columnField = "translatorC")
    private StringProperty translator = new SimpleStringProperty();

    private BooleanProperty sourceListChanged = new SimpleBooleanProperty(false);
    private BooleanProperty translatorChanged = new SimpleBooleanProperty(false);

    /**
     * The constructor of {@code SourceableImpl}
     */
    public SourceableImpl() {
        this(Default.VALUE, new SourceList(), Default.STRING);
        user = new SimpleUser();
    }

    /**
     * The constructor of {@code SourceableImpl}
     *
     * @param sourceList list of sources for this {@code SourceableImpl}
     * @param translator translator of this {@code SourceableImpl}
     */
    public SourceableImpl(SourceList sourceList, String translator) {
        this(Default.VALUE, sourceList, translator);
    }

    /**
     * The constructor of {@code SourceableImpl}
     *
     * @param id         database id of this {@code SourceableImpl}
     * @param sourceList list of sources for this {@code SourceableImpl}
     * @param translator translator of this {@code SourceableImpl}
     */
    public SourceableImpl(int id, SourceList sourceList, String translator) {
        super(id);
        this.sourceList = sourceList;
        this.translator.set(translator);

        validateState();
        bindUpdated();
    }

    /**
     * validates the State of this {@code SourceableImpl}
     *
     * @throws IllegalArgumentException if any fields are {@code null} or invalid
     */
    private void validateState() {
        String message = "";
        if (getId() < 0) {
            message = message + "Id is invalid: " + getId() + ", ";
        }
        if (sourceList == null) {
            message = message + "sourceList is null, ";
        }
        if (translator.get() == null) {
            message = message + "translator is null";
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
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
    public String getTranslator() {
        return translator.get();
    }

    @Override
    public void fromDataBase() {
        super.fromDataBase();
        sourceList.forEach(Entry::setEntryOld);
        sourceList.setUpdated();
    }

    @Override
    public void setUpdated() {
        sourceList.setUpdated();
        translatorChanged.set(false);
    }

    @Override
    protected void bindUpdated() {
        updated.addListener((observable, oldValue, newValue) -> {
            if (newValue && !newEntry) {
                OpEntryCarrier.getInstance().addUpdate(this);
            }
        });
        translator.addListener(observable -> translatorChanged.set(true));
        updated.bind(sourceList.listChangedProperty().or(translatorChanged));
    }

    @Override
    public boolean isUpdated() {
        return updated.get();
    }

    @Override
    public BooleanProperty updatedProperty() {
        return updated;
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
    public List<String> getKeyWordList() {
        return user.getKeyWordList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SourceableImpl)) return false;

        SourceableImpl that = (SourceableImpl) o;

        return user.equals(that.user) && translator.get().equals(that.translator.get());
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