package Enterprise.data.intface;

import java.sql.SQLException;

/**
 *
 */
@FunctionalInterface
public interface ConHandler<T,R> {
    R handle(T t) throws SQLException;
}
