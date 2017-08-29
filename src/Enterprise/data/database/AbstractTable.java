package Enterprise.data.database;

import Enterprise.data.intface.DataBase;
import Enterprise.data.intface.Table;

import java.io.IOException;
import java.sql.*;
import java.util.Collection;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class represents a table in a SQL database.
 */
abstract class AbstractTable<E extends DataBase> implements Table<E> {
    private final String tableName;
    static final String INTEGER = "INTEGER";
    static final String TEXT = "TEXT";

    Logger logger = Logger.getLogger(this.getClass().getPackage().getName());

    {
        try {
            //creates a FileHandler for this Package and adds it to this logger
            FileHandler fileHandler = new FileHandler("log\\" + this.getClass().getPackage().getName() + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

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
        createTable();
    }

    /**
     * Gets the name of the table.
     *
     * @return string - name of the table
     */
    final String getTableName() {
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
                statement.execute(createString());
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
    abstract String createString();

    /**
     * Gets the SQL statement for a INSERT operation. Inserts only one row in the table.
     *
     * @return string - the complete SQL statement
     */
    abstract String getInsert();

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

    void validate(DataBase dataBase, Connection connection) throws SQLException {
        if (dataBase == null || connection == null || connection.isClosed()) {
            throw new IllegalArgumentException();
        }
    }

    void validate(Collection<? extends DataBase> entries, Connection connection) throws SQLException {
        if (entries == null || connection == null || connection.isClosed()) {
            throw new IllegalArgumentException();
        }
    }

    void validate(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new IllegalArgumentException();
        }
    }
}
