package enterprise.data.dataAccess.hibernate;

import enterprise.data.dataAccess.DataAccessLayerImpl;
import enterprise.data.intface.DataEntry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.PersistenceException;
import java.util.Collection;

/**
 *
 */
public class HibernateLayer extends DataAccessLayerImpl {
    private final SessionFactory factory;

    public HibernateLayer(String name, String location) {
        super(name, location);
        Configuration configuration = new Configuration();
//        configuration.setProperty("connection.url", "jdbc:sqlite:enterpriseHibernate.db");
        configuration.configure(getClass().getResource("/hibernate/hibernate.cfg.xml"));
        factory = configuration.buildSessionFactory();
    }

    @Override
    public void add(Collection<DataEntry> entries) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();

            for (DataEntry entry : entries) {
                try {
                    session.saveOrUpdate(entry);
                    transaction.commit();
                } catch (PersistenceException e) {
                    transaction.rollback();
                }

            }
        }
    }

    @Override
    public void update(Collection<DataEntry> entries) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();

            for (DataEntry entry : entries) {
                session.update(entry);
            }
            //workaround when database on update is not changed?, the status still remains on "COMMITTED"
            transaction.commit();
        }
    }

    @Override
    public void delete(Collection<DataEntry> entries) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();

            for (DataEntry entry : entries) {
                try {
                    session.delete(entry);
                    transaction.commit();
                } catch (PersistenceException e) {
                    transaction.rollback();
                }

            }
        }
    }

    @Override
    public <E extends DataEntry> Collection<E> getAll(Class<E> eClass) {

        try (Session session = factory.openSession()) {
            Query<E> query = session.createQuery("from " + eClass.getSimpleName(), eClass);
            return query.list();
        }
    }

    @Override
    public void close() {
        super.close();
        factory.close();
    }
}
