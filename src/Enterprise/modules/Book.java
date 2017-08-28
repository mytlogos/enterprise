package Enterprise.modules;

import Enterprise.data.intface.CreationEntry;

/**
 * Container class for {@link Module#BOOK}.
 */
public class Book extends EnterpriseSegments<CreationEntry> {
    private static final Book instance = new Book();

    public static Book getInstance() {
        return instance;
    }

    private Book() {
        if (instance != null) {
            throw new IllegalStateException();
        }
    }
}
