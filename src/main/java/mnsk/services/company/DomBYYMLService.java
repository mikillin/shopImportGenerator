package mnsk.services.company;

import mnsk.services.ImporterService;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


// https://mkyong.com/java/how-to-create-xml-file-in-java-jdom-parser/
public class DomBYYMLService extends ImporterService {
    public void getData() {

        try {
            writeXml(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void writeXml(OutputStream output) throws IOException {

        Document doc = new Document();
        Element rootElement = new Element("yml_catalog");
        rootElement.setAttribute("date", new SimpleDateFormat("yyyy-MM-dd hh:mm").format(new Date()));
        doc.setRootElement(rootElement);

        Element shop = new Element("shop");

        // add xml attribute
        Element currencies = new Element("currencies");
        Element currency = new Element("currency");

        currency.setAttribute("id", "BYN");
        currencies.addContent(currency);
        shop.addContent(currencies);


        //todo: fill in :::  categories
//        for(Category : Categories)
        // todo: categories

//offers

        shop.addContent(new Element("name").setText("mkyong"));
        shop.addContent(new Element("role").setText("support"));
        shop.addContent(new Element("salary")
                .setAttribute("curreny", "USD").setText("5000"));

        // add xml comments
        shop.addContent(new Comment("for special characters like < &, need CDATA"));

        // add xml CDATA
        shop.addContent(new Element("bio")
                .setContent(new CDATA("HTML tag <code>testing</code>")));

        // append child to root
        doc.getRootElement().addContent(shop);


        XMLOutputter xmlOutputter = new XMLOutputter();

        // pretty print
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(doc, output);

    }
}
