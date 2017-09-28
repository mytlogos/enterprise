package Enterprise.data.database;

import Enterprise.data.intface.DataEntry;
import Enterprise.data.intface.Table;
import Enterprise.misc.Log;

import java.sql.*;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * This class represents a table in a SQL database.
 */
abstract class AbstractTable<E> implements Table<E> {
    private final String tableName;
    protected String insert;
    protected String create;

    Logger logger = Log.packageLogger(this);

    /**
     * The constructor of {@code AbstractTable}.
     * Tries to create the table with the provided {@code tableName},
     * if it does not exist.
     *
     * @param tableName name of the table, which will be managed
     * @throws SQLException if connection to database failed or
     *                      table could not be created
     */
    AbstractTable(String tableName) throws SQLException {
        this.tableName = tableName;
    }

    protected void init() {
        create = createString();
        createTable();
    }

    /**
     * Gets the name of the table.
     *
     * @return string - name of the table
     */
    protected final String getTableName() {
        return tableName;
    }

    /**
     * Gets the SQL statement for selecting all data in the table.
     *
     * @return string - dynamic SQL statement
     */
    final String getAll() {
        return "SELECT * FROM " + getTableName();
    }

    @Override
    final public boolean createTable() {
        boolean created = false;
        if (!tableExists()) {
            created = Connections.getConnection(connection -> {
                Statement statement = connection.createStatement();
                statement.execute(create);
                return tableExists(connection);
            });
        }
        return created;
    }

    @Override
    final public boolean tableExists() {
        return Connections.getConnection(this::tableExists);
    }

    /**
     * Checks if the specified table of the class exists already.
     * Uses the given Connection.
     *
     * @param connection connection to the database
     * @return true if the table exists
     * @throws SQLException if there was an error while checking with the database
     */
    private boolean tableExists(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet res = meta.getTables(null, null, getTableName(), null);
        boolean exists = false;
        while (res.next()) {
            if (getTableName().equalsIgnoreCase(res.getString("TABLE_NAME"))) {
                exists = true;
            }
        }
        return exists;
    }

    /**
     * Drops the whole table, this action is irrecoverable.
     *
     * @return true, if dropped
     * @throws SQLException if it could not be dropped, if for example, the table not exists
     */
    final public boolean dropTable() throws SQLException {
        boolean dropped = false;
        if (tableExists()) {
            Connections.getConnection(connection -> {
                String drop = "DROP TABLE " + getTableName();
                Statement statement = connection.createStatement();
                statement.execute(drop);
                return !tableExists();
            });
        } else dropped = true;
        return dropped;
    }

    /**
     * Gets the SQL statement for a CREATE TABLE operation.
     *
     * @return string - the complete SQL statement
     */
    protected abstract String createString();

    @Override
    final public void deleteAll() {
        String deleteAll = "Delete from " + getTableName();
        Connections.getCon(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(deleteAll)) {
                statement.executeUpdate();
                System.out.println("Inhalt von " + getTableName() + " wurde gelöscht!");
            }
        });
    }

    void validate(DataEntry dataEntry, Connection connection) throws SQLException {
        if (dataEntry == null || connection == null || connection.isClosed()) {
            throw new IllegalArgumentException();
        }
    }

    void validate(Collection<? extends DataEntry> entries, Connection connection) throws SQLException {
        if (entries == null || connection == null || connection.isClosed()) {
            throw new IllegalArgumentException();
        }
    }

    void validate(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new IllegalArgumentException();
        }
    }

    int setIndex(DataColumn column, int counter) {
        counter++;
        column.setIndex(counter);
        return counter;
    }

    void appendModifier(DataColumn tableIdColumn, StringBuilder start) {
        for (String s : tableIdColumn.getModifiers()) {
            start.append(" ").append(s);
        }
    }

    void setInsert(int count) {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into ").
                append(getTableName()).
                append(" values(");
        for (int i = 0; i < count; i++) {
            builder.append("?,");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        insert = builder.toString();
    }

    protected void setIntNull(PreparedStatement statement, DataColumn column) throws SQLException {
        statement.setNull(column.getIndex(), Types.INTEGER);
    }

    protected void setStringNull(PreparedStatement statement, DataColumn column) throws SQLException {
        statement.setNull(column.getIndex(), Types.VARCHAR);
    }

    protected int getInt(ResultSet set, DataColumn column) throws SQLException {
        return set.getInt(column.getName());
    }

    protected String getString(ResultSet set, DataColumn column) throws SQLException {
        return set.getString(column.getName());
    }

    protected boolean getBoolean(ResultSet set, DataColumn column) throws SQLException {
        return set.getBoolean(column.getName());
    }

    protected void setString(PreparedStatement statement, DataColumn column, String string) throws SQLException {
        int index = column.getIndex();
        if (index < 1) {
            System.out.println(column.getName());
            throw new IllegalArgumentException();
        }
        statement.setString(index, string);
    }

    protected void setBoolean(PreparedStatement statement, DataColumn column, boolean b) throws SQLException {
        int index = column.getIndex();
        if (index < 1) {
            System.out.println(column.getName());
            throw new IllegalArgumentException();
        }
        statement.setBoolean(index, b);
    }

    protected void setInt(PreparedStatement statement, DataColumn column, int i) throws SQLException {
        if (column.getIndex() < 1) {
            System.out.println(column.getName());
            throw new IllegalArgumentException();
        }
        statement.setInt(column.getIndex(), i);
    }
}
