package enterprise.data.intface;

import enterprise.data.dataAccess.gorgon.daos.CreationEntryDao;
import enterprise.modules.BasicModule;
import enterprise.modules.Module;
import gorgon.external.DataAccess;

/**
 * Container class which represents an Entity of an Entry, with Creation, Creator and user procession of said Creation
 */
@DataAccess(CreationEntryDao.class)
public interface CreationEntry extends DataEntry, ContainerEntry {

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
     * Getter of the {@link BasicModule}-Field.
     *
     * @return module - returns the {@link BasicModule} of this {@code CreationEntry}, is not {@code null}.
     */
    Module getModule();

    /**
     * // TODO: 27.08.2017 do the doc
     *
     * @return
     */
    boolean readyUserRemoval();

    /**
     * // TODO: 27.08.2017 do the doc
     *
     * @return
     */
    boolean readyCreationRemoval();

    /**
     * // TODO: 27.08.2017 do the doc
     *
     * @return
     */
    boolean readyCreatorRemoval();
}

