package scrape.sources.toc.intface;

/**
 * Builds a Table of Content (Toc) and saves
 * in a File in the directory defined in the
 * Implementation.
 */
public interface TocBuilder {
    /**
     * Saves the build Table of Content.
     * Returns the String path of the location
     * of the File.
     *
     * @return string - path or an empty String if it failed
     */
    String build();

    /**
     * Adds a Section to root of the Table of Contents.
     * The Type is defined in a String (volume, arc,...).
     *
     * @param type    type of the section
     * @param isExtra defines if this section is part of the main story
     * @param title   title of the section
     * @param index   index of the section, 0 or greater
     */
    void addSection(String type, boolean isExtra, String title, double index);

    /**
     * Adds a Chapter to an available Section or the root if none is present.
     *
     * @param title     title of the chapter
     * @param globalSrc String of the uri path of the internet location
     * @param localSrc  String of the uri path of the local location
     * @param index     index of the chapter
     * @param isExtra   defines if this chapter is part of the main story
     */
    void addChapter(String title, String globalSrc, String localSrc, double index, boolean isExtra);

    /**
     * Adds a Chapter to an available Section or the root if none is present.
     *
     * @param title     title of the chapter
     * @param globalSrc String of the uri path of the internet location
     * @param index     index of the chapter
     * @param isExtra   defines if this chapter is part of the main story
     */
    void addChapter(String title, String globalSrc, double index, boolean isExtra);

    /**
     * Adds a Chapter to an available Section or the root if none is present.
     *
     * @param title     title of the chapter
     * @param globalSrc String of the uri path of the internet location
     * @param localSrc  String of the uri path of the local location
     * @param index     index of the chapter
     * @param isExtra   defines if this chapter is part of the main story
     */
    void addSubChapter(String title, String globalSrc, String localSrc, double index, boolean isExtra);

    /**
     * Adds a Chapter to an available Section or the root if none is present.
     *
     * @param title     title of the chapter
     * @param globalSrc String of the uri path of the internet location
     * @param index     index of the chapter
     * @param isExtra   defines if this chapter is part of the main story
     */
    void addSubChapter(String title, String globalSrc, double index, boolean isExtra);
}
