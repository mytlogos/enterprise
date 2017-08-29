package Enterprise.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 */
public class stubdao {
    public static void main(String[] args) throws SQLException {
        Connection connection = getCon();
        String table = "Create table if not exists testtable ('id' integer primary key , 'column' text)";
        String insert = "insert into testtable values (?,?)";


    }

    private static Connection getCon() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }
}
