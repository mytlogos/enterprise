package Enterprise.data.intface;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.StringProperty;
import scrape.sources.SourceList;

import java.util.List;

/**
 * Represents an Entry which is Sourceable, meaning that this entry is accessible through the internet
 * (optional?: and has been translated from one Language, into another)
 */
public interface Sourceable extends DataBase, Comparable<Sourceable> {
    /**
     * Gets the SourceList of this {@code Sourceable}.
     *
     * @return sourceList - a list of sources of this {@code Sourceable}
     */
    SourceList getSourceList();

    /**
     * Gets the translator, one or many in one String, of this {@code Sourceable}.
     *
     * @return translator - the translators in a String
     */
    String getTranslator();

    /**
     * Gets the value of the {@code translatorChanged} flag.
     *
     * @return translatorChanged - if the state of the {@code translator} changed since construction
     *         or the last call of {@link #setUpdated()}.
     */
    boolean isTranslatorChanged();

    /**
     * Gets the value of the {@code sourceListChanged} flag.
     *
     * @return sourceListChanged - if the state of the {@code sourceList} changed since construction
     *         or the last call of {@link #setUpdated()}.
     */
    boolean isSourceListChanged();

    /**
     * Gets the translator-{@code StringProperty}.
     *
     * @return translator - a StringProperty for the User Interface, is not{@code null}.
     */
    StringProperty translatorProperty();

    /**
     * Gets the {@code keyWords} of this {@code Sourceable}.
     *
     * @return keyWords - keyWords separated through commas, is not {@code null}.
     */
    String getKeyWords();

    /**
     * Sets the {@link User} of this {@code Sourceable}.
     * @param user {@code User} to be set to this {@code Sourceable}
     */
    void setUser(@NotNull User user);

    /**
     * Implementation of {@link Comparable}
     * <p>
     * Compares the dataFields of this and that {@code Sourceable}.
     *
     * @param o - {@code Sourceable} to be compared with this {@code Sourceable}
     * @return compared - the value of the comparison between the corresponding dataFields
     *
     * @see Comparable
     */
    int compareTo(Sourceable o);

    List<String> getKeyWordList();
}
