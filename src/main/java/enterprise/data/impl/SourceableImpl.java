package enterprise.data.impl;

import enterprise.data.Default;
import enterprise.data.intface.Sourceable;
import enterprise.data.intface.User;
import gorgon.external.GorgonEntry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scrape.sources.Source;
import tools.SetList;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of Sourceable
 *
 * @see Sourceable
 */
public class SourceableImpl extends AbstractDataEntry implements Sourceable {
    private final StringProperty translator = new SimpleStringProperty();
    private ObservableList<Source> sources;
    private User user;

    public SourceableImpl() {
        sources = FXCollections.observableArrayList(new SetList<>());
        setTranslator("");
    }

    /**
     * The constructor of {@code SourceableImpl}
     *
     * @param sourceList list of sources for this {@code SourceableImpl}
     * @param translator translator of this {@code SourceableImpl}
     */
    private SourceableImpl(ObservableList<Source> sourceList, String translator) {
        this.sources = sourceList;
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
        if (sources == null) {
            message = message + "sources is null, ";
        }
        if (translator.get() == null) {
            message = message + "translator is null";
        }
        if (!message.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static SourceableImpl get() {
        SourceableImpl sourceable = get(FXCollections.observableArrayList(new SetList<>()), Default.STRING);
        // TODO: 22.10.2017 check this ?
        sourceable.user = new UserImpl();
        return sourceable;
    }

    public static SourceableImpl get(ObservableList<Source> sourceList, String translator) {
        return new SourceableImpl(sourceList, translator);
    }

    @Override
    public int hashCode() {
        int result = getSources() != null ? getSources().hashCode() : 0;
        result = 31 * result + (translator != null ? translator.hashCode() : 0);
        return result;
    }

    @Override
    public ObservableList<Source> getSources() {
        return sources;
    }

    void setSources(Collection<Source> sources) {
        this.sources = FXCollections.observableArrayList(new SetList<>(sources));
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

        return Objects.equals(user, that.user) && Objects.equals(getTranslator(), that.getTranslator());
    }

    @Override
    public String toString() {
        return "SourceableImpl{" +
                "translator=" + translator.get() +
                ", sources=" + sources +
                ", user=" + user +
                '}';
    }

    @Override
    public int compareTo(GorgonEntry gorgonEntry) {
        if (gorgonEntry == null) return -1;
        if (gorgonEntry == this) return 0;
        if (!(gorgonEntry instanceof Sourceable)) return -1;

        SourceableImpl o = (SourceableImpl) gorgonEntry;
        int compare = getTranslator().compareTo(o.getTranslator());

        if (compare == 0) {
            compare = user.compareTo(o.user);
        }
        return compare;
    }
}
