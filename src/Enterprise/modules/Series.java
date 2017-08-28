package Enterprise.modules;

import Enterprise.data.intface.SourceableEntry;

/**
 * Container class for {@link Module#NOVEL}.
 */
public class Series extends EnterpriseSegments<SourceableEntry> {
    private static final Series instance = new Series();

    public static Series getInstance() {
        return instance;
    }

    private Series() {
        if (instance != null) {
            throw new IllegalStateException();
        }
    }
}
