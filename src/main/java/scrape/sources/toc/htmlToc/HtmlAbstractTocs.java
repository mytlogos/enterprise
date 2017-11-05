package scrape.sources.toc.htmlToc;

/**
 *
 */
class HtmlAbstractTocs {
    String creationTitleTag = "a";
    String creationTitleId = "enterprise-title";

    String tocTag = "div";
    String tocId = "enterprise-toc";

    String childrenContainerTag = "div";

    String volumeTag = "h1";
    String volumeId = "data-volumeId";

    String chapterTag = "h2";
    String chapterIdAttr = "data-totalChapterId";

    String subChapterTag = "h3";
    String subChapterIdAttr = "data-subChapterIdAttr";


    String getVolumeId(int count) {
        return "volume " + count;
    }

    String getChapterId(int count) {
        return "totalChapter " + count;
    }

    String getSubChapterId(int count) {
        return "subChapter " + count;
    }

    String getChapterElementTitle(int i) {
        return "Chapter " + i;
    }


}
