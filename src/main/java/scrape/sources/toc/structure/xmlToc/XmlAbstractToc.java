package scrape.sources.toc.structure.xmlToc;

/**
 *
 */
abstract class XmlAbstractToc {
    final String rootTag = "creation";
    final String sectionTag = "section";
    final String sectionTypeAttr = "type";
    final String sectionTitleAttr = "title";
    final String sectionIndexAttr = "sectionIndex";

    final String titleTag = "title";
    final String isExtraAttr = "isExtra";

    final String chapterTag = "chapter";
    final String chapTotIndexAttr = "totIndex";
    final String chapSectionIndexAttr = "sectIndex";

    final String localSourceTag = "localSource";
    final String globalSourceTag = "internetSource";

    final String subChapterTag = "totalSubIndex";
    final String subChaptIndexAttr = "chaptIndex";
    final String totSubIndexAttr = "totalSubIndex";

}
