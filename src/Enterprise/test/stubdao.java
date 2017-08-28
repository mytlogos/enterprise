package Enterprise.test;

import javax.lang.model.element.NestingKind;
import java.lang.ref.WeakReference;
import java.sql.*;
import java.util.Arrays;

/**
 *
 */
public class stubdao {
    public static void main(String[] args) throws SQLException {
        Connection connection = getCon();
        String table = "Create table if not exists testtable ('id' integer primary key , 'column' text)";
        String insert = "insert into testtable values (?,?)";
        connection.createStatement().execute(table);
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setNull(1, Types.INTEGER);
        statement.setString(2, "probetext1");
        statement.addBatch();
        ResultSet set = statement.getGeneratedKeys();
        while (set.next()) {
            System.out.println(set.getLong(1));
        }
        statement.setNull(1, Types.INTEGER);
        statement.setString(2, "probetext2");
        statement.addBatch();
        ResultSet set1 = statement.getGeneratedKeys();
        while (set1.next()) {
            System.out.println(set1.getLong(1));
        }
        statement.setNull(1, Types.INTEGER);
        statement.setString(2, "probetext3");
        statement.addBatch();
        ResultSet set2 = statement.getGeneratedKeys();
        while (set2.next()) {
            System.out.println(set2.getLong(1));
        }
        statement.setNull(1, Types.INTEGER);
        statement.setString(2, "probetext4");
        statement.addBatch();
        System.out.println(Arrays.toString(statement.executeBatch()));
        ResultSet set3 = statement.getGeneratedKeys();
        while (set3.next()) {
            System.out.println(set3.getLong(1));
        }

        PreparedStatement statement1 = connection.prepareStatement("Select id from testtable where column = 'probetext1'");
        ResultSet set4 = statement1.executeQuery();
        while (set4.next()) {
            System.out.println(set4.getLong(1));
        }

    }

    private static Connection getCon() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }
}
