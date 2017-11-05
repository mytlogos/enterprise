package scrape;

/**
 *
 */
public interface Reader<E> {
    E read(String path);
}
