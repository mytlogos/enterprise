package enterprise.data.dataAccess.hibernate.dialect;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataBuilderInitializer;
import org.hibernate.engine.jdbc.dialect.internal.DialectResolverSet;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

/**
 * Copied from <a href ="https://github.com/ZsoltFabok/sqlite-dialect">SQLiteDialect</a>.
 */
public class SQLiteMetadataBuilderInitializer implements MetadataBuilderInitializer {
    private static final SQLiteDialect dialect = new SQLiteDialect();
    static private final DialectResolver resolver = (DialectResolver) info -> {
        if (info.getDatabaseName().equals("SQLite"))
            return dialect;
        return null;
    };

    @Override
    public void contribute(MetadataBuilder metadataBuilder, StandardServiceRegistry serviceRegistry) {
        DialectResolver dialectResolver = serviceRegistry.getService(DialectResolver.class);

        if ((dialectResolver instanceof DialectResolverSet)) {
            ((DialectResolverSet) dialectResolver).addResolver(resolver);
        }
    }
}
