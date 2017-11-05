package scrape.sources.toc.xmlToc;

/**
 *
 */
public abstract class XmlAbstractToc {
    String rootTag = "creation";
    String sectionTag = "section";
    String sectionTypeAttr = "type";
    String sectionTitleAttr = "title";
    String sectionIndexAttr = "sectionIndex";

    String titleTag = "title";
    String isExtraAttr = "isExtra";

    String chapterTag = "chapter";
    String chapTotIndexAttr = "totIndex";
    String chapSectionIndexAttr = "sectIndex";

    String localSourceTag = "localSource";
    String globalSourceTag = "internetSource";

    String subChapterTag = "totalSubIndex";
    String subChaptIndexAttr = "chaptIndex";
    String totSubIndexAttr = "totalSubIndex";

}
