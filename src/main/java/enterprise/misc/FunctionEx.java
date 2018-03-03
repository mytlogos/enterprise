package enterprise.misc;

/**
 *
 */
@FunctionalInterface
public interface FunctionEx<T, R> {
    R apply(T t) throws Exception;
}
