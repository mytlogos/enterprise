package enterprise.data.dataAccess;

import enterprise.data.Default;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 *
 */
class Cleaner {

    private final String name;
    private final String location;

    Cleaner(String name, String location) {
        this.name = name.endsWith(".db") ? name : name + ".db";
        this.location = location == null ? "" : location;
    }

    void cleanUp() {
        String url = "jdbc:sqlite:" + location + name;
        System.out.println(url);
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {
//                System.out.println(statement.executeQuery("Select * from Source"));
                System.out.println(statement.execute("Delete from Source where id not in(Select elt from SourceableImpl_sources)"));
                System.out.println(statement.execute("Delete from PostConfig where id not in(Select id from Source)"));
            }
            System.out.println("cleaning");
        } catch (SQLException e) {
            Default.LOGGER.log(Level.SEVERE, "exception occurred while cleaning up the database", e);
        }
    }
}
