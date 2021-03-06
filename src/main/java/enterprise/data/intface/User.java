package enterprise.data.intface;

import enterprise.data.dataAccess.gorgon.daos.UserDao;
import gorgon.external.DataAccess;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * This class represents a user who processes a {@link Creation} and gives personal Information, like comments about it.
 */
@DataAccess(UserDao.class)
public interface User extends DataEntry {

    /**
     * Gets the Value of the {@code ownStatus}-{@code StringProperty}.
     *
     * @return ownStatus - personal Status of the User about the {@link Creation}. Is not {@code null}.
     * Is by default an empty String.
     */
    String getOwnStatus();

    /**
     * Gets the {@code StringProperty}-Object of the {@code ownStatus}-Field.
     *
     * @return ownStatusProperty - Is not {@code null}.
     */
    StringProperty ownStatusProperty();

    /**
     * Gets the Value of the {@code comment}-{@code StringProperty}.
     *
     * @return comment - comment of the User about the {@link Creation}. Is not {@code null}.
     * Is by default an empty String.
     */
    String getComment();

    /**
     * Gets the {@code StringProperty}-Object of the {@code comment}-Field.
     *
     * @return commentProperty - Is not {@code null}.
     */
    StringProperty commentProperty();

    /**
     * Gets the Value of the {@code rating}-{@code IntegerProperty}.
     *
     * @return rating - personal Rating of the User about the {@link Creation}. It is by default zero.
     */
    int getRating();

    /**
     * Gets the {@code IntegerProperty}-Object of the {@code rating}-Field.
     *
     * @return ratingProperty - Is not {@code null}.
     */
    IntegerProperty ratingProperty();

    /**
     * Gets the Value of the {@code processedPortion}-{@code IntegerProperty}.
     *
     * @return processedPortion - number of Portions of the {@code Creation}, which were processed by the User.
     * It is by default zero.
     */
    int getProcessedPortion();

    /**
     * Gets the {@code IntegerProperty}-Object of the {@code processedPortion}-Field.
     *
     * @return processedPortionProperty - Is not {@code null}.
     */
    IntegerProperty processedPortionProperty();

    /**
     * Gets the Value of the {@code list}-{@code StringProperty}.
     *
     * @return list - name of the List which the User assigned this {@link Creation} to. Is not {@code null}.
     * Is by default an empty String.
     */
    String getListName();

    /**
     * Sets the name of the list, which the entry will set in.
     *
     * @param listName name of the "list"
     */
    void setListName(String listName);

    /**
     * Gets the Value of the {@code keyWords}-{@code StringProperty}.
     *
     * @return keyWords - keyWords for searching an Entry, be it in the application or in the internet.
     * Is not {@code null}. Is by default an empty String.
     */
    String getKeyWords();

    /**
     * Gets the {@code StringProperty}-Object of the {@code keyWords}-Field.
     *
     * @return keyWordsProperty - Is not {@code null}.
     */
    StringProperty keyWordsProperty();

    /**
     * Gets the {@code StringProperty}-Object of the {@code list}-Field.
     *
     * @return listNameProperty - Is not {@code null}.
     */
    StringProperty listNameProperty();

    List<String> getKeyWordList();
}
