package Enterprise.data.database;

import Enterprise.data.intface.ContainerEntry;

import java.sql.SQLException;

/**
 *
 */
public abstract class AbstractRelationTable<E extends ContainerEntry> extends AbstractTable<E> {
    /**
     * The constructor of {@code AbstractRelationTable}.
     *
     * @param tableName name of the table, which will be managed
     * @throws SQLException if connection to database failed or
     *                      table could not be created
     */
    AbstractRelationTable(String tableName) throws SQLException {
        super(tableName);
    }

    protected String createRelationTableHelper(DataColumn... dataColumns) {
        StringBuilder builder = new StringBuilder();

        int counter = 0;
        builder.append("CREATE TABLE IF NOT EXISTS ").
                append(getTableName()).
                append("(");

        for (DataColumn column : dataColumns) {
            builder.append(",").
                    append(column.getName()).
                    append(" ").
                    append(column.getType());

            appendModifier(column, builder);

            counter = setIndex(column, counter);
        }
        builder.append(")");
        setInsert(counter);
        return builder.toString().replaceFirst(",", "");
    }
}
