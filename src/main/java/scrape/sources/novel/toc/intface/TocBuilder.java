package scrape.sources.novel.toc.intface;

import scrape.sources.novel.toc.structure.TableOfContent;

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
     * @return the Table of Contents tree, TableOfContent, or null, if it failed
     */
    TableOfContent build();

    /**
     * Sets the title and baseUri of the Table of Contents.
     *
     * @param title   title of the creation, not null
     * @param baseUri uri to the creation, not null
     */
    void init(String title, String baseUri);

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
    void addPortion(String title, String globalSrc, String localSrc, double index, boolean isExtra);

    /**
     * Adds a Chapter to an available Section or the root if none is present.
     *
     * @param title     title of the chapter
     * @param globalSrc String of the uri path of the internet location
     * @param index     index of the chapter
     * @param isExtra   defines if this chapter is part of the main story
     */
    void addPortion(String title, String globalSrc, double index, boolean isExtra);

    /**
     * Adds a Chapter to an available Section or the root if none is present.
     *
     * @param title     title of the chapter
     * @param globalSrc String of the uri path of the internet location
     * @param localSrc  String of the uri path of the local location
     * @param index     index of the chapter
     * @param isExtra   defines if this chapter is part of the main story
     */
    void addSubPortion(String title, String globalSrc, String localSrc, double index, boolean isExtra);

    /**
     * Adds a Chapter to an available Section or the root if none is present.
     *
     * @param title     title of the chapter
     * @param globalSrc String of the uri path of the internet location
     * @param index     index of the chapter
     * @param isExtra   defines if this chapter is part of the main story
     */
    void addSubPortion(String title, String globalSrc, double index, boolean isExtra);
}
