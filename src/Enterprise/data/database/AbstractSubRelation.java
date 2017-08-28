package Enterprise.data.database;

import Enterprise.data.intface.DataBase;
import Enterprise.data.intface.SubRelationTable;
import scrape.sources.Source;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Abstract class of {@code SubRelationTable}.
 */
abstract class AbstractSubRelation<E extends DataBase> extends AbstractTable<E> implements SubRelationTable<E> {
    private final String mainId;
        /**
     * The constructor of {@code AbstractSubRelation}.
     * Sets the tableName of this instance.
     *
     * @param tableName name of the Table
     */
    AbstractSubRelation(String tableName, String mainId) throws SQLException {
        super(tableName);
        this.mainId = mainId;
    }

    /**
     * Returns a SQL statement of an SELECT operation.
     * The main Id is the part, which is the main component or container
     * of the data in the other columns.
     *
     * @param id id of the main Id column which should be filtered after
     * @return string - a complete sql statement
     */
    String getFromMainId(int id) {
        return "Select * from " + getTableName() + " where " + mainId + " = " + id;
    }

    abstract Source getData(Connection connection, ResultSet rs) throws SQLException;

    abstract String getDelete();

    abstract boolean removeDead(E entry, Connection connection) throws SQLException;

    abstract boolean removeDead(Collection<? extends E> entries, Connection connection) throws SQLException;

}
