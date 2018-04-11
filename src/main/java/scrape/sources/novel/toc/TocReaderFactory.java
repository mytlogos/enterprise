package scrape.sources.novel.toc;

import org.apache.commons.io.FilenameUtils;
import scrape.sources.novel.toc.intface.TocReader;
import scrape.sources.novel.toc.structure.TableOfContent;
import scrape.sources.novel.toc.structure.htmlToc.HtmlTocReader;
import scrape.sources.novel.toc.structure.jsonToc.JsonTocReader;
import scrape.sources.novel.toc.structure.xmlToc.XmlTocReader;

/**
 *
 */
public class TocReaderFactory implements TocReader {
    @Override
    public TableOfContent read(String path) {
        TocReader reader;

        if (FilenameUtils.isExtension(path, "xml")) {
            reader = new XmlTocReader();

        } else if (FilenameUtils.isExtension(path, "html")) {
            reader = new HtmlTocReader();

        } else if (FilenameUtils.isExtension(path, "json")) {
            reader = new JsonTocReader();
        } else {
            return null;
        }
        return reader.read(path);
    }

    @Override
    public <E> TableOfContent read(E toc) {
        TocReader reader;

        if (toc instanceof org.jsoup.nodes.Document) {
            reader = new HtmlTocReader();
        } else if (toc instanceof org.jdom2.Document) {
            reader = new XmlTocReader();
        } else {
            // TODO: 05.11.2017 check json
            reader = new JsonTocReader();
        }
        return reader.read(toc);
    }
}
