package Enterprise.data.intface;

import Enterprise.modules.BasicModules;
import Enterprise.modules.Module;

/**
 * Container class which represents an Entity of an Entry, with Creation, Creator and user procession of said Creation
 */
public interface CreationEntry extends Comparable<CreationEntry>, ContainerEntry {

    /**
     * Getter of the {@link User}-Field.
     *
     * @return user - returns a User, is not {@code null}.
     */
    User getUser();

    /**
     * Getter of the {@link Creation}-Field.
     *
     * @return creation - returns a Creation, is not {@code null}.
     */
    Creation getCreation();

    /**
     * Getter of the {@link Creator}-Field.
     *
     * @return creator - returns a Creator, is not {@code null}.
     */
    Creator getCreator();

    /**
     * Getter of the {@link BasicModules}-Field.
     *
     * @return module - returns the {@link BasicModules} of this {@code CreationEntry}, is not {@code null}.
     */
    Module getModule();

    /**
     * Implementation of {@link Comparable}
     * <p>
     * Compares the dataFields of this and that {@code CreationEntry}.
     *
     * @param o - {@code CreationEntry} to be compared with this {@code CreationEntry}
     * @return compared - the value of the comparison between the corresponding dataFields
     *
     * @see Comparable
     */
    int compareTo(CreationEntry o);

    /**
     * // TODO: 27.08.2017 do the doc
     * @return
     */
    boolean readyUserRemoval();

    /**
     * // TODO: 27.08.2017 do the doc
     * @return
     */
    boolean readyCreationRemoval();

    /**
     * // TODO: 27.08.2017 do the doc
     * @return
     */
    boolean readyCreatorRemoval();
}

