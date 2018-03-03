package enterprise.misc;

/**
 *
 */
public interface TriConsumer<T, R, S> {
    void consume(T t, R r, S s);
}
