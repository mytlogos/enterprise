package Enterprise.data.database;

import Enterprise.data.intface.ConHandler;
import Enterprise.data.intface.ConHandlerVoid;
import Enterprise.misc.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class providing the {@link Connection} to the underlying database.
 * Provides static Methods, which executes {@link ConHandler} or {@link ConHandlerVoid}
 * within a transaction.
 * <p>
 * These methods catch the {@link SQLException}s thrown from the {@code ConHandler} and log
 * them with the package logger.
 * </p>
 * <p>
 * The package-private utility Methods differ only in their return type.
 * </p>
 */
class Connections {

    private static Logger logger = Log.packageLogger(Connections.class);

    private Connections() {
        throw new IllegalStateException();
    }

    /**
     * Logs the thrown Exception in case of connection error.
     *
     * @param e a {@code SQLException}, which wil be logged
     */
    private static void logConnectionError(SQLException e) {
        logger.log(Level.SEVERE, "error in establishing the connection", e);
    }

    /**
     * Gets the connection of the underlying database.
     *
     * @return connection a {@code Connection}
     * @throws SQLException if DriverManager could not get connection
     */
    static Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }

    /**
     * @param connectionRFunction
     * @param <E>
     * @return
     */
    static <E> E getConnection(ConHandler<Connection, E> connectionRFunction) {
        E result = null;
        try (Connection connection = connection()) {
            try {
                connection.setAutoCommit(false);
                result = connectionRFunction.handle(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (SQLException e) {
            logConnectionError(e);
        }
        return result;
    }

    /**
     * @param connectionRFunction
     * @param <E>
     * @return
     */
    static <E> E getConnectionAuto(ConHandler<Connection, E> connectionRFunction) {
        E result = null;
        try (Connection connection = connection()) {
            try {
                result = connectionRFunction.handle(connection);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (SQLException e) {
            logConnectionError(e);
        }
        return result;
    }

    static void getCon(ConHandlerVoid<Connection> connectionRFunction) {
        try (Connection connection = connection()) {
            try {
                connection.setAutoCommit(false);
                connectionRFunction.handle(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (SQLException e) {
            logConnectionError(e);
        }
    }

    static void getConAuto(ConHandlerVoid<Connection> connectionRFunction) {
        try (Connection connection = connection()) {
            try {
                connectionRFunction.handle(connection);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (SQLException e) {
            logConnectionError(e);
        }
    }
}