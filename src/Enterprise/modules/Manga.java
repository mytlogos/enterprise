package Enterprise.modules;

import Enterprise.data.intface.SourceableEntry;

/**
 * Container class for {@link Module#MANGA}.
 */
public class Manga extends EnterpriseSegments<SourceableEntry> {
    private static final Manga instance = new Manga();

    public static Manga getInstance() {
        return instance;
    }

    private Manga() {
        if (instance != null) {
            throw new IllegalStateException();
        }
    }
}
