package enterprise.data.impl;

import enterprise.data.Default;
import enterprise.data.intface.Entry;
import enterprise.data.intface.Sourceable;
import enterprise.data.intface.User;
import gorgon.external.GorgonEntry;
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
public class SourceableImpl extends AbstractDataEntry implements Sourceable {
    private final StringProperty translator = new SimpleStringProperty();
    private final BooleanProperty sourceListChanged = new SimpleBooleanProperty(false);
    private SourceList sourceList;
    private User user;

    SourceableImpl() {

    }

    /**
     * The constructor of {@code SourceableImpl}
     *
     * @param sourceList list of sources for this {@code SourceableImpl}
     * @param translator translator of this {@code SourceableImpl}
     */
    private SourceableImpl(SourceList sourceList, String translator) {
        this.sourceList = sourceList;
        this.translator.set(translator);

        validateState();
    }

    /**
     * validates the State of this {@code SourceableImpl}
     *
     * @throws IllegalArgumentException if any fields are {@code null} or invalid
     */
    private void validateState() {
        String message = "";
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

    public static SourceableImpl get() {
        SourceableImpl sourceable = get(new SourceList(), Default.STRING);
        // TODO: 22.10.2017 check this ?
        sourceable.user = new UserImpl();
        return sourceable;
    }

    public static SourceableImpl get(SourceList sourceList, String translator) {
        return new SourceableImpl(sourceList, translator);
    }

    @Override
    public void fromDataBase() {
        super.fromDataBase();
        sourceList.forEach(Entry::setEntryOld);
        sourceList.setUpdated();
    }

    @Override
    public int hashCode() {
        int result = getSourceList() != null ? getSourceList().hashCode() : 0;
        result = 31 * result + (translator != null ? translator.hashCode() : 0);
        return result;
    }

    @Override
    public SourceList getSourceList() {
        return sourceList;
    }

    @Override
    public String getTranslator() {
        return translator.get();
    }

    public void setTranslator(String translator) {
        this.translator.set(translator);
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
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof Sourceable)) return -1;

        Sourceable o = (Sourceable) gorgonEntry;
        int compare = getTranslator().compareTo(o.getTranslator());
        if (compare == 0) {
            compare = getSourceList().compareTo(o.getSourceList());
        }
        return compare;
    }
}
