package Enterprise.data.intface;

import Enterprise.misc.SQLUpdate;

/**
 * Classes, which will be saved into the Database, need to implement this Interface
 * Fields which will be saved into Database, need to be marked with the {@link SQLUpdate} Annotation
 * <p>
 * Fields annotated with @SQL need to have corresponding Getter Methods, and StateChanged Getter Methods,
 * because {@link Enterprise.data.database.AbstractDataTable} creates the UpdateStatements through
 * Reflection.
 * </p>
 */
public interface DataBase extends Entry {

    /**
     * Gets the Id of this {@code DataBase}.
     *
     * @return id Database Id
     */
    int getId();

    /**
     * Set id only from instances of AbstractDataTable.
     * // TODO: 26.08.2017 maybe some better 'security' measure
     * @param id id to set to this instance
     * @param table table - instance of {@link Enterprise.data.database.AbstractDataTable}
     * @throws IllegalArgumentException if id <= 0
     */
    void setId(int id, Table table);
}
