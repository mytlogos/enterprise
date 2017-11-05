package Enterprise.misc;

/**
 *
 */
@FunctionalInterface
public interface ConsumerEx<E> {
    void accept(E e) throws Exception;
}
