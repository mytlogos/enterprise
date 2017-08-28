package Enterprise.data.database;

import Enterprise.data.intface.ConHandler;
import Enterprise.data.intface.ConHandlerVoid;
import Enterprise.data.intface.DataBase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
class Connections{

    private Connections() {
        throw new IllegalStateException();
    }

    private static Logger logger = Logger.getLogger(Connections.class.getPackage().getName());

    static  {
        try {
            //creates a FileHandler for this Package and adds it to this logger
            FileHandler fileHandler = new FileHandler("log\\" + Connections.class.getPackage().getName() + ".log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
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
     *
     * @param connectionRFunction
     * @param <E>
     * @return
     */
    static  <E> E getConnection(ConHandler<Connection, E> connectionRFunction) {
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
     *
     * @param connectionRFunction
     * @param <E>
     * @return
     */
    static  <E> E getConnectionAuto(ConHandler<Connection, E> connectionRFunction) {
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
}
