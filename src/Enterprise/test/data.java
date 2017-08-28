package Enterprise.test;

import Enterprise.data.database.CreationEntryTable;
import Enterprise.data.database.UserTable;
import Enterprise.data.impl.SimpleCreationEntry;
import Enterprise.data.impl.SimpleSourceable;
import Enterprise.data.impl.SimpleUser;
import Enterprise.data.impl.SourceableEntryImpl;
import Enterprise.data.intface.*;
import org.sqlite.SQLiteException;

import javax.lang.model.util.Elements;
import javax.xml.crypto.Data;
import java.lang.ref.WeakReference;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Created by Dominik on 29.06.2017.
 * Part of OgameBot.
 */
public class data {

    private static Function<Connection, Object> connectionRFunction;

    static class afrik {
        static AtomicInteger integer = new AtomicInteger(1);
        static List<Integer> reusableInts = new ArrayList<>();

        static int creatId() {
            if (reusableInts.isEmpty()) {
                return integer.getAndIncrement();
            } else {
                int id = reusableInts.get(0);
                reusableInts.remove(0);
                return id;
            }
        }

        static void deleteId(int id) {
            reusableInts.add(id);
        }
    }

    private static  <E> E getConnection(ConHandler<Connection, E> connectionRFunction) {
        E result = null;
        try (Connection connection = connection()) {
            try {
                connection.setAutoCommit(false);
                result = connectionRFunction.handle(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        HashMap<String, WeakReference<Object>> hashMap = new HashMap<>();


        String select = "Select * from USERTABLE where OWNSTATUS = ? AND COMMENT = ? AND LIST = ? AND PROCESSED = ? AND RATING = ? AND KEYWORDS = ?";

        User user = new SimpleUser("eins", "zwei", 0, "drei", 0, "vier");

        Boolean bol = getConnection(connection -> getaBoolean(select, user, connection));
        System.out.println(bol);
    }

    private static Boolean getaBoolean(String select, User user, Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setString(1, user.getOwnStatus());
            statement.setString(2, user.getComment());
            statement.setString(3, user.getList());
            statement.setInt(4, user.getProcessedPortion());
            statement.setInt(5, user.getRating());
            statement.setString(6, user.getKeyWords());

            ResultSet set = statement.executeQuery();
            System.out.println("Closed: " + statement.isClosed());
            while (set.next()) {
                System.out.println(set.getInt(1));
            }
        }
        return false;
    }

    private static Connection connection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:enterprise.db");
    }

    public static boolean insert(User entry, Connection connection) {
        boolean inserted = false;
        try {
            try (PreparedStatement stmt = connection.prepareStatement(getInsert(), Statement.RETURN_GENERATED_KEYS)) {
                setInsertData(entry, stmt);
                int affected = stmt.executeUpdate();
                ResultSet set = stmt.getGeneratedKeys();

                if (set != null && set.next()) {
                    entry.setId(set.getInt(1), new UserTable());
                    System.out.println(set.getInt(1));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void setInsertData(User entry, PreparedStatement stmt) throws SQLException {
        String ownStatus = entry.getOwnStatus();
        String comment = entry.getComment();
        int rating = entry.getRating();
        int processedPortion = entry.getProcessedPortion();
        String list = entry.getList();
        String keyWords = entry.getKeyWords();

        stmt.setNull(1,Types.INTEGER);
        stmt.setString(2,ownStatus);
        stmt.setString(3,comment);
        stmt.setString(4,list);
        stmt.setInt(5,processedPortion);
        stmt.setInt(6,rating);
        stmt.setString(7,keyWords);
    }

    static String getInsert() {
        return "insert into USERTABLE values(?,?,?,?,?,?,?)";
    }
}
