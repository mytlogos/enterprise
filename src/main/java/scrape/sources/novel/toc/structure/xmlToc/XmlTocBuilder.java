package scrape.sources.novel.toc.structure.xmlToc;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import scrape.sources.novel.toc.intface.TocBuilder;
import scrape.sources.novel.toc.structure.TableOfContent;
import tools.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 *
 */
public class XmlTocBuilder extends XmlAbstractToc implements TocBuilder {
    private String title;
    private Element root;
    private Element section;
    private Element chapter;
    private int totChaptersIndex = 1;
    private int totSubChapIndex = 1;


    public XmlTocBuilder(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.title = title;
        root = getRoot();
    }

    @Override
    public TableOfContent build() {
        String workDir = FileUtils.workDirectory();
        String tocDir = workDir + "/novel/toc";

        Document document = new Document(root);
        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());

        TableOfContent creationRoot = null;
        try {
            if (FileUtils.checkDirectory(tocDir)) {
                String location = tocDir + "/" + title + ".xml";

                FileOutputStream outputStream = new FileOutputStream(location);
                output.output(document, outputStream);

                document.setBaseURI(location);
                creationRoot = new XmlTocReader().read(document);
            }
        } catch (IOException e) {
            return null;
        }

        return creationRoot;
    }

    private Element getRoot() {
        Element root = new Element(rootTag);
        return addTitle(title, root);
    }

    private Element addTitle(String string, Element element) {
        Element title = new Element(titleTag);
        title.setText(string);
        element.addContent(title);
        return element;
    }

    @Override
    public void init(String title, String baseUri) {

    }

    @Override
    public void addSection(String type, boolean isExtra, String title, double index) {
        Element element = new Element(sectionTag);
        element.setAttribute(sectionTypeAttr, type);
        element.setAttribute(isExtraAttr, String.valueOf(isExtra));
        element.setAttribute(sectionTitleAttr, title);
        element.setAttribute(sectionIndexAttr, String.valueOf(index));
        section = element;
    }

    @Override
    public void addPortion(String title, String globalSrc, String localSrc, double index, boolean isExtra) {
        Element element = new Element(chapterTag);

        element.setAttribute(chapSectionIndexAttr, String.valueOf(index));
        element.setAttribute(chapTotIndexAttr, String.valueOf(totChaptersIndex++));
        element.setAttribute(isExtraAttr, String.valueOf(isExtra));

        addTitle(title, element);
        addLocal(localSrc, element);
        addGlobal(globalSrc, element);

        chapter = element;

        if (section != null) {
            section.addContent(element);
        }

    }

    @Override
    public void addPortion(String title, String globalSrc, double index, boolean isExtra) {
        addPortion(title, globalSrc, "", index, isExtra);
    }

    @Override
    public void addSubPortion(String title, String globalSrc, String localSrc, double index, boolean isExtra) {
        Objects.requireNonNull(chapter);

        Element element = new Element(chapterTag);

        element.setAttribute(totSubIndexAttr, String.valueOf(totSubChapIndex++));
        element.setAttribute(subChaptIndexAttr, String.valueOf(index));
        element.setAttribute(isExtraAttr, String.valueOf(isExtra));

        addTitle(title, element);
        addLocal(localSrc, element);
        addGlobal(globalSrc, element);

        chapter.addContent(element);
    }

    @Override
    public void addSubPortion(String title, String globalSrc, double index, boolean isExtra) {
        addSubPortion(title, globalSrc, "", index, isExtra);
    }

    private void addLocal(String uri, Element element) {
        if (uri == null || uri.isEmpty()) {
            return;
        }
        Element local = new Element(localSourceTag);
        local.setText(uri);
        element.addContent(local);
    }

    private void addGlobal(String uri, Element element) {
        if (uri == null || uri.isEmpty()) {
            return;
        }
        Element global = new Element(globalSourceTag);
        global.setText(uri);
        element.addContent(global);
    }
}
