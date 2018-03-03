package enterprise.data.dataAccess.hibernate.dialect;

import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

import java.sql.PreparedStatement;

/**
 * Copied from <a href ="https://github.com/ZsoltFabok/sqlite-dialect">SQLiteDialect</a>.
 */
public class SQLiteLimitHandler implements LimitHandler {
    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        if (LimitHelper.hasFirstRow(selection)) {
            return sql + " limit ? offset ?";
        } else {
            return sql + " limit ?";
        }
    }

    @Override
    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) {
        return 0;
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) {
        return 0;
    }

    @Override
    public void setMaxRows(RowSelection selection, PreparedStatement statement) {
    }
}
