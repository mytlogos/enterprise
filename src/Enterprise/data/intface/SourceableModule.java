package Enterprise.data.intface;

import java.util.Collection;
import java.util.List;

/**
 * Represents a Module which is Sourceable.
 * @see SourceableEntry
 */
public interface SourceableModule {
    /**
     * Adds a translator to this Module.
     *
     * @param translator translator to add
     * @return true if successful
     */
    boolean addTranslator(String translator);

    /**
     * Adds a {@code Collection} of translators to this Module.
     *
     * @param translators translator to add
     * @return true if successful
     */
    boolean addTranslator(Collection<String> translators);

    /**
     * Gets the Translators of this {@code SourceableModule}.
     *
     * @return translators - {@code List} of translators
     */
    List<String> getTranslators();
}
