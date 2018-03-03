package enterprise.data.dataAccess.hibernate;

import enterprise.data.concurrent.Updater;
import enterprise.data.dataAccess.DataAccessLayer;
import enterprise.data.impl.CreationEntryImpl;
import enterprise.data.impl.SourceableEntryImpl;
import enterprise.data.intface.CreationEntry;
import enterprise.data.intface.DataEntry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class HibernateLayer implements DataAccessLayer {
    private final SessionFactory factory;

    public HibernateLayer() {
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
                try {
                    session.update(entry);
                    transaction.commit();
                } catch (PersistenceException e) {
                    transaction.rollback();
                }
            }
        }
    }

    @Override
    public Collection<CreationEntry> getCreationEntries() {
        Collection<CreationEntry> entries = new ArrayList<>();

        Collection<CreationEntryImpl> creationEntries = getAll(CreationEntryImpl.class);
        Collection<SourceableEntryImpl> sourceableEntries = getAll(SourceableEntryImpl.class);

        entries.addAll(creationEntries);
        entries.addAll(sourceableEntries);

        return entries;
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
    public void startUpdater() {
        Updater.start();
    }

    @Override
    public void stopUpdater() {
        Updater.stop();
    }

    @Override
    public <E extends DataEntry> Collection<E> getAll(Class<E> eClass) {

        try (Session session = factory.openSession()) {
            Query<E> query = session.createQuery("from " + eClass.getSimpleName());
            return query.list();
        }
    }

    @Override
    public void close() {
        factory.close();
        stopUpdater();
    }
}
