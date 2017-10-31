package Enterprise.data.intface;

import Enterprise.misc.SQLUpdate;

/**
 * Represents an Data Model Object, which will be saved into a persistent storage.
 * Classes, which will be saved into the Database, need to implement this Interface
 * Fields which will be saved into Database, need to be marked with the {@link SQLUpdate} Annotation
 * <p>
 * Fields annotated with @SQL need to have corresponding Getter Methods, and StateChanged Getter Methods,
 * because {@link Enterprise.data.database.AbstractDataTable} creates the UpdateStatements through
 * Reflection.
 * </p>
 */
public interface DataEntry extends Entry {

    /**
     * Gets the Id of this {@code DataEntry}.
     *
     * @return id Database Id
     */
    int getId();

    /**
     * Set id only from instances of AbstractDataTable.
     * // TODO: 26.08.2017 make a better 'security' measure
     *
     * @param id    id to set to this instance
     * @param table table - instance of {@link Enterprise.data.database.AbstractDataTable}
     * @throws IllegalArgumentException if id <= 0
     * @throws IllegalAccessError       if table is no instance of {@link DataTable}
     */
    void setId(int id, Table table);
}
