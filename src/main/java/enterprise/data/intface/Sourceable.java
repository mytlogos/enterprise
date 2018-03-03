package enterprise.data.intface;

import enterprise.data.dataAccess.gorgon.daos.SourceableDao;
import gorgon.external.DataAccess;
import javafx.beans.property.StringProperty;
import scrape.sources.SourceList;

import java.util.List;

/**
 * Represents an Entry which is Sourceable, meaning that this entry is accessible through the internet
 * (optional?: and has been translated from one Language, into another)
 */
@DataAccess(SourceableDao.class)
public interface Sourceable extends DataEntry {
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
     *
     * @param user {@code User} to be set to this {@code Sourceable}
     */
    void setUser(User user);

    List<String> getKeyWordList();
}
