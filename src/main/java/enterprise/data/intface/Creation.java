package enterprise.data.intface;

import enterprise.data.dataAccess.gorgon.daos.CreationDao;
import gorgon.external.DataAccess;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * <p>
 * Represents an Object like a Book, a Series and similar created Works.
 * </p>
 * <p> This Class is not Thread-safe</p>
 */
@DataAccess(CreationDao.class)
public interface Creation extends DataEntry {
    /**
     * Gets the {@code Creator} of this Work
     *
     * @return creator - returns null if no creator is available
     */
    Creator getCreator();

    /**
     * Sets the {@link Creator} of this {@code Creation}
     * and adds this {@code Creation} to the {@code Works}
     * of the given {@code Creator}.
     *
     * @param creator {@code Creator} who created this Work
     * @throws IllegalArgumentException thrown if creator is null
     */
    void setCreator(Creator creator) throws IllegalArgumentException;

    /**
     * The corresponding Getter-Method of the PropertyField "workStatus".
     * <p>
     * Gets the String of the {@code Property}
     * </p>
     *
     * @return workStatus - status of this Work, mostly defined through user input,
     * returns an empty {@code String} if no Data was input before
     */
    String getWorkStatus();

    /**
     * StringProperty-Getter of "workStatus".
     * <p>
     * Required for the User Interface Representation and editing
     * </p>
     *
     * @return workStatus - returns the Object of the Field, is not {@code null}
     * @see #getWorkStatus()
     */
    StringProperty workStatusProperty();

    /**
     * The corresponding Getter-Method of the PropertyField "series".
     * <p>
     * Gets the String of the {@code Property}
     * </p>
     *
     * @return series - series (like a Set, Collection, not the something like Shows) of this Work,
     * returns an empty {@code String} if no Data was input before
     */
    String getSeries();

    /**
     * StringProperty-Getter of "series".
     * <p>
     * Required for the User Interface Representation and editing
     * </p>
     *
     * @return series - returns the Object of the Field, is not {@code null}
     * @see #getSeries()
     */
    StringProperty seriesProperty();

    /**
     * The corresponding Getter-Method of the PropertyField "title".
     * <p>
     * Gets the String of the {@code Property}
     * </p>
     *
     * @return title - title of this Work, returns an empty {@code String} if no Data was input before
     */
    String getTitle();

    /**
     * StringProperty-Getter of "title".
     * <p>
     * Required for the User Interface Representation and editing
     * </p>
     *
     * @return title - returns the Object of the Field, is not {@code null}
     * @see #getTitle()
     */
    StringProperty titleProperty();

    /**
     * The corresponding Getter-Method of the PropertyField "dateLastPortion".
     * <p>
     * Gets the String of the {@code Property}
     * </p>
     *
     * @return dateLastPortion - String representation of the last Date, where a Portion
     * (for example a Episode of a Series) was published  of this Work,
     * returns an empty {@code String} if no Data was input before
     */
    String getDateLastPortion();

    /**
     * StringProperty-Getter of "dateLastPortion".
     * <p>
     * required for the User Interface Representation and editing
     * </p>
     *
     * @return dateLastPortion - returns the Object of the Field, is not {@code null}
     * @see #getDateLastPortion()
     */
    StringProperty dateLastPortionProperty();

    /**
     * the corresponding Getter-Method of the PropertyField "numPortion".
     * <p>
     * Gets the {@code int} of the {@code Property}
     * </p>
     *
     * @return numPortion - number of Portions(like a Episode of an Series) of this Work,
     * returns zero if no Data was input before
     */
    int getNumPortion();

    /**
     * StringProperty-Getter of "workStatus".
     * Required for the User Interface Representation and editing
     *
     * @return workStatus - returns the Object of the Field, is not {@code null}
     * @see #getNumPortion()
     */
    IntegerProperty numPortionProperty();

    /**
     * Gets the String Representation of Path for the Cover{@code Image} of this Creation.
     *
     * @return coverPath - returns the String of the Path, if not set,
     * it returns by default the StringPath of the {@link enterprise.data.Default} Image
     */
    String getCoverPath();

    /**
     * Sets the Path of Cover{@code Image} through a String
     *
     * @param coverPath - String representation of the Path
     * @throws IllegalArgumentException if the String is no valid Path representation for {@link java.net.URI}
     */
    void setCoverPath(String coverPath) throws IllegalArgumentException;

    /**
     * StringProperty-Getter of the "coverPath"-Field
     *
     * @return coverPath - returns the Object of that Field, is not {@code null}.
     */
    StringProperty coverPathProperty();

    /**
     * Gets the String Representation of the Path for the ToC of this Creation.
     *
     * @return tocPath - returns the string representation of the uri
     * or null if not set
     */
    String getTocLocation();
}
