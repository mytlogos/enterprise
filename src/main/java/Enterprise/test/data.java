package Enterprise.test;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;

/**
 */
public class data {

    public static void main(String[] args) throws IOException, JDOMException {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document schema = saxBuilder.build(data.class.getClassLoader().getResourceAsStream("schema.xml"));
        System.out.println(schema.getRootElement().getValue());
    }
}
