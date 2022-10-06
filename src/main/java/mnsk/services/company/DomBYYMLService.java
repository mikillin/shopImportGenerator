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
import java.util.Enumeration;


// https://mkyong.com/java/how-to-create-xml-file-in-java-jdom-parser/
public class DomBYYMLService extends ImporterService {

    enum currencies {
        BYR,
        BYN,
        USD,
        EUR
    }

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
        shop.addContent(new Element("name").setText("Inter-mebel (Интер-мебель)"));
        shop.addContent(new Element("company").setText("ИП Слоущ Илья Абрамович"));
        shop.addContent(new Element("url").setText("inter-mebel.by"));

        Element currencies = new Element("currencies");
        Element currency = new Element("currency");

        currency.setAttribute("id", "BYN");
        currency.setAttribute("rate", "1");
        currencies.addContent(currency);
        shop.addContent(currencies);
//        categories
        Element categories = new Element("categories");
        Element category= new Element("category");
        category.setAttribute("id", "1");
        category.setText("Техника для кухни"); //TODO:
        categories.addContent(category);

        category= new Element("category");
        category.setAttribute("id", "51");
        category.setAttribute("parentId", "1");
        category.setText("Кофеварка"); //TODO:

        categories.addContent(category);
        shop.addContent(categories);

        Element offers = new Element("offers");

        // TODO: loop
        Element offer = new Element("offer");
        offer.setAttribute("id", "TODO:");
        offer.setAttribute("type", "vendor.model");
        offer.setAttribute("available", "true");

        Element url = new Element("url");
        url.setText(""); //todo: how to add?? what kind of URL should be here?
        offer.addContent(url);
        Element price = new Element("price");
        price.setText(""); //todo:
        offer.addContent(price);
        Element price_opt = new Element("price_opt");
        price.setText(""); //todo:
        offer.addContent(price_opt);
        Element currencyId = new Element("currencyId");
        currencyId.setText("BYN"); //todo:
        offer.addContent(currencyId);
        Element categoryId = new Element("categoryId");
        categoryId.setText(""); //todo:
        offer.addContent(categoryId);
        Element picture = new Element("picture");
        picture.setText(""); //todo:
        offer.addContent(picture);
        Element vendor = new Element("vendor");
        vendor.setText(""); //todo:
        offer.addContent(vendor);
        Element model = new Element("model");
        model.setText(""); //todo:
        offer.addContent(model);
        Element description = new Element("description");
        description.setText(""); //todo:
        offer.addContent(description);

        offers.addContent(offer);
        shop.addContent(offers);

//
//        <offer id = "158" type = "vendor.model" available = "true" >
//            <url > http://www.dom.by/item/104/</url>
//            <price > 99.50 </price >
//            <price_opt > 98.50 </price_opt >
//            <currencyId > BYN </currencyId >
//            <categoryId > 1 </categoryId >
//            <picture > http://images.by.st/86413240_s17.jpg</picture>
//            <vendor > Bosch </vendor >
//            <model > DHU 632 D WH </model >
//            <description > </description >
//        </offer >
//
//
//                //todo: fill in :::  categories
////        for(Category : Categories)
//                // todo: categories
//
////offers
//
//                shop.addContent(new Element("role").setText("support"));
//        shop.addContent(new Element("salary")
//                .setAttribute("curreny", "USD").setText("5000"));

        // add xml comments
//        shop.addContent(new Comment("for special characters like < &, need CDATA"));
//
//        // add xml CDATA
//        shop.addContent(new Element("bio")
//                .setContent(new CDATA("HTML tag <code>testing</code>")));

        // append child to root
        doc.getRootElement().addContent(shop);


        XMLOutputter xmlOutputter = new XMLOutputter();

        // pretty print
        xmlOutputter.setFormat(Format.getPrettyFormat());
        xmlOutputter.output(doc, output);

    }
}
