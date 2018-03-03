package enterprise.data.intface;

import enterprise.data.Person;
import enterprise.data.dataAccess.gorgon.daos.CreatorDao;
import gorgon.external.DataAccess;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * Represents the {@code Creator} of a {@link Creation}.
 */
@DataAccess(CreatorDao.class)
public interface Creator extends DataEntry {

    /**
     * Getter of the personalInfo of this {@code Creator}.
     *
     * @return personalInfo - returns a {@link Person}, is not {@code null}, returns an 'empty' Person
     * if no Person was specified, before
     */
    Person getPersonalInfo();

    /**
     * Setter of the personalInfo of this {@code Creator}.
     *
     * @param personalInfo {@code Person} to be set to this {@code Creator}.
     * @throws IllegalArgumentException if the parameter is {@code null}.
     */
    void setPersonalInfo(Person personalInfo) throws IllegalArgumentException;

    /**
     * Gets an unmodifiable list of Creations, Works, of this {@code Creator}.
     *
     * @return creatorWorks - a list of the {@link Creation}s of this {@code Creator}, is not {@code null}.
     */
    List<Creation> getCreatorWorks();

    /**
     * Gets the {@code name} of this {@code Creator}.
     *
     * @return name - String of the Property
     */
    String getName();

    /**
     * Setter of {@code name}.
     *
     * @param name - name of this {@code Creator}.
     */
    void setName(String name);

    /**
     * Gets the {@code StringProperty} of the {@code name}.
     *
     * @return name
     */
    StringProperty nameProperty();

    /**
     * Gets the {@code sortName} of this {@code Creator}.
     *
     * @return sortName - String of the Property
     */
    String getSortName();

    /**
     * Setter of {@code sortName}.
     *
     * @param sortName - name with which this {@code Creator} should be sorted after.
     */
    void setSortName(String sortName);

    /**
     * Gets the {@code StringProperty} of the {@code name}.
     *
     * @return name
     */
    StringProperty sortNameProperty();

    /**
     * Gets the {@code status} of this {@code Creator}.
     *
     * @return status - String of the Property
     */
    String getStatus();

    /**
     * Setter of {@code status}.
     *
     * @param status - status of this {@code Creator}.
     */
    void setStatus(String status);

    /**
     * Gets the {@code StringProperty} of the {@code name}.
     *
     * @return name
     */
    StringProperty statusProperty();

    /**
     * Adds a {@code Creation} to the Works of this {@code Creator}.
     *
     * @param creation work of this {@code Creator}
     */
    void addWork(Creation creation);
}
