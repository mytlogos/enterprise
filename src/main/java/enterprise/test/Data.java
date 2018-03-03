package enterprise.test;

import enterprise.data.dataAccess.DataAccessManager;
import enterprise.data.intface.CreationEntry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import scrape.sources.Source;
import scrape.sources.SourceType;
import scrape.sources.posts.Post;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 */
public class Data {
    private int id;
    private String name;
    private Address address;

    private Data() {

    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        DataAccessManager.manager.add(Collections.singletonList(new Post(Source.create("http://www.wuxiaworld.com/", SourceType.START), "post nr.1", "no content", "no footer", LocalDateTime.now(), "http://www.wuxiaworld.com/", true)));
        DataAccessManager.manager.close();
    }

    private static void hibernate() throws URISyntaxException {
        Configuration configuration = new Configuration();
        configuration.configure(Data.class.getResource("/hibernate/hibernate.cfg.xml"));

//        ServiceRegistry registry = new StandardServiceRegistryBuilder().build();
//        MetadataSources sources = new MetadataSources(registry);

        Post post = new Post(Source.create("http://www.wuxiaworld.com/", SourceType.START), "post nr.1", "no content", "no footer", LocalDateTime.now(), "http://www.wuxiaworld.com/", true);

        try {
            try (SessionFactory factory = configuration.buildSessionFactory()) {

                persist(post, factory);

                List<CreationEntry> entries;
                try (Session session = factory.openSession()) {
                    entries = session.createQuery("from CreationEntryImpl").list();
                    entries.forEach(System.out::println);
                    session.createQuery("from " + Post.class.getSimpleName()).list().forEach(o -> {
                        Post post1 = (Post) o;
                        System.out.println(post1.getTime());
                    });
                }
                persistCollection(new ArrayList<>(entries), factory);
            }
        } catch (PersistenceException e) {
            System.out.println("catching");
            e.printStackTrace();
        }
    }

    private static void persist(Object o, SessionFactory factory) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(o);
            transaction.commit();
        }
    }

    private static void persistCollection(Collection<Object> objects, SessionFactory factory) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            objects.forEach(session::saveOrUpdate);
            transaction.commit();
        }
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
