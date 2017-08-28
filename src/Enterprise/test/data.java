package Enterprise.test;

import Enterprise.data.intface.ConHandler;
import Enterprise.data.intface.User;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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

    public static void main(String[] args) throws ClassNotFoundException, SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        HashMap<String, WeakReference<Object>> hashMap = new HashMap<>();


        Class<?> tableClass = Class.forName("Enterprise.data.database.UserTable");
        Field tableField = tableClass.getDeclaredField("commentC");
        tableField.setAccessible(true);

        Method getInstance = tableClass.getDeclaredMethod("getInstance", (Class<?>[]) null);
        getInstance.setAccessible(true);
        System.out.println((String) tableField.get(getInstance.invoke(null, (Object[]) null)));
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
}
