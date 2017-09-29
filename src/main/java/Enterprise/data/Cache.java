package Enterprise.data;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Generic Class for creating a Cache backed by a {@code HashMap}.
 * The keys have Strong References, while the Values are wrapped in
 * {@link WeakReference}.
 */
public class Cache<K, V> {
    private Map<K, WeakReference<V>> cacheMap = new HashMap<>();

    /**
     * Checks if the key from {@code getKey} function is available.
     * Puts the value parameter in the map with the key if no mapping
     * is available or it is mapped to null.
     * Returns the value parameter if the key value equals
     * the value parameter.
     *
     * @param value  value to check in the cache
     * @param getKey function which gets the key from value
     * @return returns either value or the value of the cache
     */
    public V checkCache(V value, Function<V, K> getKey) {
        return checkCache(value, getKey, (v, v2) -> Objects.equals(v, v2) ? v : v2);
    }

    /**
     * Checks if the key from {@code getKey} function is available.
     * Puts the value parameter in the map with the key if no mapping
     * is available or it is mapped to null.
     * The first parameter of biFunction is the cache value,
     * the second parameter the parameter {@code value}.
     *
     * @param value  value to check in the cache
     * @param getKey function which gets the key from value
     * @return returns value of the biFunction parameter
     */
    public V checkCache(V value, Function<V, K> getKey, BiFunction<V, V, V> biFunction) {
        if (value == null || getKey == null || biFunction == null) {
            throw new IllegalArgumentException("no null values allowed");
        }
        K key = getKey.apply(value);
        WeakReference<V> reference = cacheMap.get(key);

        if (reference == null) {
            cacheMap.put(key, new WeakReference<>(value));
            return value;

        } else if (reference.get() == null) {
            cacheMap.put(key, new WeakReference<>(value));
            return value;

        } else {
            return biFunction.apply(reference.get(), value);
        }
    }
}
