package scrape.sources;

/**
 * The type enum of this {@code Source}.
 * At the moment it has no real function, but
 * may be important later for the Scraper.
 */
public enum SourceType {
    /**
     * The site contains the table of content,
     * used for indexing the portions (chapters,...)
     * or fetching the whole novel.
     */
    TOC("TOC"),
    /**
     * The site has no long path, mainly used
     * for scraping posts.
     */
    START("Homepage");

    private final String type;

    /**
     * The constructor of {@code SourceType}.
     *
     * @param type the string representation of the enum
     */
    SourceType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }


}
