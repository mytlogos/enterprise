package Enterprise.data.intface;

import java.sql.SQLException;

/**
 *
 */
@FunctionalInterface
public interface ConHandlerVoid<T> {
    void handle(T t) throws SQLException;
}
