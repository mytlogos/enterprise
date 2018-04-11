package enterprise.test;

import enterprise.data.impl.CreationImpl;
import enterprise.data.impl.SourceableImpl;
import enterprise.data.impl.UserImpl;
import enterprise.data.intface.Sourceable;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import scrape.sources.Source;
import scrape.sources.SourceType;
import scrape.sources.posts.Post;
import scrape.sources.posts.PostScraper;
import scrape.sources.posts.PostSearchEntry;
import tools.SetList;

import javax.persistence.PersistenceException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
public class Data extends Application {
    private int id;
    private ObservableList<Source> sources = FXCollections.observableArrayList(new SetList<>());

    public Data() {

    }

    public static void main(String[] args) throws URISyntaxException {
        hibernate();
    }

    private static void hibernate() throws URISyntaxException {
        Configuration configuration = new Configuration();
        configuration.configure(Data.class.getResource("/hibernate/hibernate.cfg.xml"));

//        ServiceRegistry registry = new StandardServiceRegistryBuilder().build();
//        MetadataSources sources = new MetadataSources(registry);

        SourceableImpl sourceable = new SourceableImpl();
        sourceable.setUser(new UserImpl());

        sourceable.getSources().add(Source.create("http://www.wuxiaworld.com/", SourceType.START));
        sourceable.getSources().add(Source.create("http://www.bisafans.de/", SourceType.START));
        sourceable.getSources().add(Source.create("http://de.wikipedia.org/", SourceType.START));
        sourceable.getSources().add(Source.create("http://www.gravitytales.com/", SourceType.START));
        try {
            try (SessionFactory factory = configuration.buildSessionFactory()) {

                List<Sourceable> list;
                try (Session session = factory.openSession()) {
                    list = getList(sourceable, session);
                }

                for (Sourceable data1 : list) {
                    try {
                        data1.getSources().add(Source.create("http://www.novelupdates.com/", SourceType.START));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try (Session session = factory.openSession()) {
                    Transaction transaction = session.beginTransaction();
                    for (Sourceable data1 : list) {
                        try {
                            session.update(data1);
                            System.out.println(transaction.getStatus());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    transaction.commit();
                    List<Sourceable> list1 = getList(sourceable, session);

//                    list.forEach(System.out::println);
                    list1.forEach(data1 -> data1.getSources().forEach(source -> System.out.println(source.getId() + ": " + source.getUrl())));
                }
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private static List getList(Object data, Session session) {
        return session.createQuery("from " + data.getClass().getSimpleName()).list();
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
    public void start(Stage primaryStage) throws Exception {
        Source source = Source.create("http://www.wuxiaworld.com/", SourceType.START);
        PostSearchEntry entry = new PostSearchEntry(new CreationImpl(), source, new ArrayList<>());
        PostScraper scraper = PostScraper.scraper(source);
        List<Post> posts = scraper.getPosts(entry);
        System.out.println(posts);
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", sources=" + sources +
                '}';
    }

    public ObservableList<Source> getSources() {
        return sources;
    }

    public void setSources(Collection<Source> sources) {
        this.sources = FXCollections.observableArrayList(new SetList<>(sources));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
