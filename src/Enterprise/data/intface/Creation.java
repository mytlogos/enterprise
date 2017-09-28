package Enterprise.data.intface;

import com.sun.istack.internal.NotNull;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 * <p>
 * Represents an Object like a Book, a Series and similar created Works.
 * </p>
 * <p> This Class is not Thread-safe</p>
 */
public interface Creation extends DataEntry, Comparable<Creation> {
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
     * The corresponding stateChanged-Getter
     * <p>
     * Queries if the State of the Field "workStatus" has changed
     * </p>
     *
     * @return workStatusChanged returns true if the state of the field "workStatus" has been changed
     */
    boolean isWorkStatusChanged();

    /**
     * The corresponding stateChanged-Getter
     * <p>
     * Queries if the State of the Field "title" has changed
     * </p>
     *
     * @return titleChanged returns true if the state of the field "title" has been changed
     */
    boolean isTitleChanged();

    /**
     * The corresponding stateChanged-Getter
     * <p>
     * Queries if the State of the Field "series" has changed
     * </p>
     *
     * @return workStatusChanged returns true if the state of the field "series" has been changed
     */
    boolean isSeriesChanged();

    /**
     * The corresponding stateChanged-Getter
     * <p>
     * Queries if the State of the Field "dateLastPortion" has changed
     * </p>
     *
     * @return dateLastPortionChanged returns true if the state of the field "dateLastPortion" has been changed
     */
    boolean isDateLastPortionChanged();

    /**
     * The corresponding stateChanged-Getter
     * <p>
     * Queries the invalidationState of the Field "numPortion"
     * </p>
     *
     * @return numPortionChanged returns true if the state of the field "numPortion" has been changed
     */
    boolean isNumPortionChanged();

    /**
     * The corresponding stateChanged-Getter
     * <p>
     * Queries if the State of the Field "coverPath" has changed
     * </p>
     *
     * @return coverPathChanged returns true if the state of the field "coverPath" has been changed
     */
    boolean isCoverPathChanged();

    /**
     * The corresponding Getter-Method of the PropertyField "workStatus".
     * <p>
     * Gets the String of the {@code Property}
     * </p>
     *
     * @return workStatus - status of this Work, mostly defined through user input,
     *         returns an empty {@code String} if no Data was input before
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
     *         (for example a Episode of a Series) was published  of this Work,
     *         returns an empty {@code String} if no Data was input before
     */
    String getDateLastPortion();

    /**
     * StringProperty-Getter of "dateLastPortion".
     * <p>
     * required for the User Interface Representation and editing
     * </p>
     *
     * @return dateLastPortion - returns the Object of the Field, is not {@code null}
     *
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
     *         returns zero if no Data was input before
     */
    int getNumPortion();

    /**
     * StringProperty-Getter of "workStatus".
     * Required for the User Interface Representation and editing
     *
     * @return workStatus - returns the Object of the Field, is not {@code null}
     *
     * @see #getNumPortion()
     */
    IntegerProperty numPortionProperty();

    /**
     * Gets the String Representation of Path for the Cover{@code Image} of this Creation.
     *
     * @return coverPath - returns the String of the Path, if not set,
     *         it returns by default the StringPath of the {@link Enterprise.data.Default} Image
     */
    String getCoverPath();

    /**
     * Sets the Path of Cover{@code Image} through a String
     *
     * @param coverPath - String representation of the Path
     * @throws IllegalArgumentException if the String is no valid Path representation for {@link java.net.URI}
     */
    void setCoverPath(@NotNull String coverPath) throws IllegalArgumentException;

    /**
     * StringProperty-Getter of the "coverPath"-Field
     *
     * @return coverPath - returns the Object of that Field, is not {@code null}.
     */
    StringProperty coverPathProperty();

    /**
     * Implementation of {@link Comparable}
     * <p>
     * Compares the dataFields of this and that Creation
     * </p>
     *
     * @param creation - {@code Creation} to be compared with this {@code Creation}
     * @return compared - the value of the comparison between the corresponding dataFields
     *
     *@see Comparable
     */
    int compareTo(Creation creation);
}
