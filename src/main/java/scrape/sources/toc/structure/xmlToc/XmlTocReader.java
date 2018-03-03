package scrape.sources.toc.structure.xmlToc;

import enterprise.misc.FunctionEx;
import enterprise.test.Data;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import scrape.sources.toc.intface.TocReader;
import scrape.sources.toc.structure.CreationRoot;
import scrape.sources.toc.structure.intface.Portionable;
import scrape.sources.toc.structure.novel.Chapter;
import scrape.sources.toc.structure.novel.NovelSection;
import scrape.sources.toc.structure.novel.SubChapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class XmlTocReader extends XmlAbstractToc implements TocReader {

    public XmlTocReader() {

    }

    @Override
    public <E> CreationRoot read(E toc) {
        return null;
    }

    @Override
    public CreationRoot read(String path) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document schema = saxBuilder.build(Data.class.getClassLoader().getResourceAsStream(path));
            return getRoot(schema, path);
        } catch (JDOMException | IOException e) {
            return new CreationRoot("N/A", path);
        }
    }

    private CreationRoot getRoot(Document document, String path) {
        Element root = document.getRootElement();
        Element titleElement = root.getChild(titleTag);
        String title = titleElement.getTextNormalize();

        CreationRoot creationRoot = new CreationRoot(title, path);
        List<Element> sections = root.getChildren(sectionTag);

        if (sections.isEmpty()) {
            List<Element> chapters = root.getChildren(chapterTag);
            if (chapters.isEmpty()) {
                return creationRoot;
            } else {
                creationRoot.addAllPortions(getPortions(chapters, creationRoot));
            }
        } else {
            creationRoot.addAllSections(getSections(sections));
        }
        return creationRoot;
    }

    private List<NovelSection> getSections(List<Element> sections) {
        List<NovelSection> novelSections = new ArrayList<>();

        for (Element section : sections) {
            String title = handleAttr(section, sectionTitleAttr, Attribute::getValue);
            Boolean isExtra = handleAttr(section, isExtraAttr, Attribute::getBooleanValue);
            String type = handleAttr(section, sectionTypeAttr, Attribute::getValue);
            Double index = handleAttr(section, sectionIndexAttr, Attribute::getDoubleValue);

            if (checkNonNull(title, isExtra, type, index)) {
                // TODO: 03.11.2017 suppress warnings?
                NovelSection nodeSection = new NovelSection(title, type, isExtra, index);

                List<Element> chapters = section.getChildren(chapterTag);
                List<Chapter> portions = getPortions(chapters, nodeSection);
                nodeSection.addAll(portions);

                novelSections.add(nodeSection);
            } else {
                System.out.println("failed");
            }
        }
        return novelSections;
    }

    private <E> E handleAttr(Element section, String attr, FunctionEx<Attribute, E> funct) {
        Attribute attribute = section.getAttribute(attr);
        try {
            return funct.apply(attribute);
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<Chapter> getPortions(List<Element> chapters, Portionable parent) {
        for (Element chapter : chapters) {
            Element child = chapter.getChild(titleTag);
            Boolean isExtra = handleAttr(chapter, isExtraAttr, Attribute::getBooleanValue);
            Double totIndex = handleAttr(chapter, chapTotIndexAttr, Attribute::getDoubleValue);
            Double sectIndex = handleAttr(chapter, chapSectionIndexAttr, Attribute::getDoubleValue);

            if (checkNonNull(child, isExtra, totIndex, sectIndex)) {
                // TODO: 03.11.2017 suppress warnings?
                Chapter portion = new Chapter(child.getTextNormalize(), isExtra, totIndex, sectIndex);

                List<Element> subChapters = chapter.getChildren(subChapterTag);
                List<SubChapter> subPortions = getSupPortions(subChapters);
                portion.addAll(subPortions);

                parent.add(portion);
            }
        }

        return new ArrayList<>();
    }

    private List<SubChapter> getSupPortions(List<Element> subChapters) {
        List<SubChapter> subChapterList = new ArrayList<>();
        for (Element subChapter : subChapters) {
            Element child = subChapter.getChild(titleTag);
            Boolean isExtra = handleAttr(subChapter, isExtraAttr, Attribute::getBooleanValue);
            Double totIndex = handleAttr(subChapter, totSubIndexAttr, Attribute::getDoubleValue);
            Double sectIndex = handleAttr(subChapter, subChaptIndexAttr, Attribute::getDoubleValue);

            if (checkNonNull(child, isExtra, totIndex, sectIndex)) {
                SubChapter subPortion = new SubChapter(child.getTextNormalize(), isExtra, totIndex, sectIndex);
                subChapterList.add(subPortion);
            }
        }
        return subChapterList;
    }

    private boolean checkNonNull(Object... objects) {
        return Arrays.stream(objects).allMatch(Objects::nonNull);
    }
}
