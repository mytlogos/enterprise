package Enterprise.modules;

import Enterprise.data.intface.SourceableEntry;

/**
 * Container class for {@link Module#ANIME}.
 */
public final class Anime extends EnterpriseSegments<SourceableEntry> {

    private static final Anime instance = new Anime();

    public static Anime getInstance() {
        return instance;
    }

    private Anime() {
        if (instance != null) {
            throw new IllegalStateException();
        }
    }

}
