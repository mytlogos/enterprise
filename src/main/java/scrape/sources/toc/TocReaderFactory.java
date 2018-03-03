package scrape.sources.toc;

import org.apache.commons.io.FilenameUtils;
import scrape.sources.toc.intface.TocReader;
import scrape.sources.toc.structure.CreationRoot;
import scrape.sources.toc.structure.htmlToc.HtmlTocReader;
import scrape.sources.toc.structure.jsonToc.JsonTocReader;
import scrape.sources.toc.structure.xmlToc.XmlTocReader;

/**
 *
 */
public class TocReaderFactory implements TocReader {
    @Override
    public CreationRoot read(String path) {
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
    public <E> CreationRoot read(E toc) {
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
