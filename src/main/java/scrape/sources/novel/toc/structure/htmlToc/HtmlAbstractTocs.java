package scrape.sources.novel.toc.structure.htmlToc;

/**
 *
 */
class HtmlAbstractTocs {
    final String creationTitleTag = "a";
    final String creationTitleId = "enterprise-title";

    final String tocTag = "div";
    final String tocId = "enterprise-toc";

    final String childrenContainerTag = "div";

    final String volumeTag = "h1";
    final String volumeId = "data-volumeId";

    final String chapterTag = "h2";
    final String chapterIdAttr = "data-totalChapterId";

    final String subChapterTag = "h3";
    final String subChapterIdAttr = "data-subChapterIdAttr";


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
