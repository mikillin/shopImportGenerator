package mnsk.services.company;

import mnsk.App;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.ImporterService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: S.Rogachevsky
 * Date: 05.12.16
 * Time: 22:57
 */
public class DomBYService extends ImporterService {
    private final static String SEARCH_FOR_BRAND = "signal";
    public static final int DATA_LINK_INDEX = 10;
    public static final int DATA_NAME_INDEX = 1;

    private final static String FILE_SOURCE = "C:\\work\\shop\\products.csv";


    @Override
    public void getData() {
        ImporterService.initializeFilesHeaders();


        List<String> allData = null;
        try {
            allData = getAllDataFromCSVFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] dataArray;
        Document htmlDocument;
        String elementValue;
        String categoryName;
        for (String data : allData) {
            ImportNode in = new ImportNode();
            ProductImporter pi = new ProductImporter();

            dataArray = data.split(App.CSV_SEPARATOR);

            htmlDocument = ImporterService.getHTMLDocument(dataArray[DATA_LINK_INDEX]);


            List<String> onPageImageLinks = getImageLinks(htmlDocument);


            String tmpImageNames = "";
            int imageLinksNamePart = 0;
            boolean addNamePart = onPageImageLinks.size() > 1;
            for (String imageLink : onPageImageLinks) {
                tmpImageNames += (tmpImageNames.equals("")) ? "" : ",";
                tmpImageNames += ImporterService.saveImageOnDisk(imageLink, imageLinksNamePart++, addNamePart);
            }


            Elements els = htmlDocument.getElementsByClass("b-product__tabs__characteristics__item");
            String material = "";
            for (Element el : els) {

                elementValue = el.getElementsByClass("b-product__tabs__characteristics__item__label").text();
                if (elementValue.contains("Материал")) {
                    material += material.equals("") ? "" : " + ";
                    material += el.getElementsByClass("b-product__tabs__characteristics__item__value").text();

                } else if (elementValue.contains("Размеры")) {
                    pi.setGabarity(el.getElementsByClass("b-product__tabs__characteristics__item__value").text());

                } else if (elementValue.contains("Бренд")) {
                    pi.setBrand(el.getElementsByClass("b-product__tabs__characteristics__item__value").text());

                }
            }


            //заполнение первого листа
            categoryName = htmlDocument.getElementsByClass("b-breadcrumbs__link").get(0).text();
            in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
            in.setName(categoryName + " " + dataArray[DATA_NAME_INDEX]);
            in.setImage(tmpImageNames);


            pi.setSKU(in.getSku());
            pi.setName(in.getName());
            pi.setMaterial(material);

            pi.setCategory(categoryName);
            ImporterService.sbExportOne.append(in.toString());//.append(System.lineSeparator());
            ImporterService.sbExportTwo.append(pi.toString());//.append(System.lineSeparator());
        }

        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);
        System.out.println("Hello!");

    }


    private static List<String> getImageLinks(Document doc) {
        String imageLinkData;
        List<String> imageLinks = new ArrayList<>();
        Elements elements = doc.getElementsByAttribute("data-cloudzoom");
        for (Element element : elements) {
            imageLinkData = element.attr("data-cloudzoom");
            int beginIndex = imageLinkData.indexOf("'") + 1;
            int endIndex = imageLinkData.indexOf("'", beginIndex);
            imageLinks.add(imageLinkData.substring(beginIndex, endIndex));
        }
        return imageLinks;
    }


    private static List<String> getAllDataFromCSVFile() throws FileNotFoundException {

        List<String> al = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_SOURCE))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (SEARCH_FOR_BRAND.equals("") || line.contains(SEARCH_FOR_BRAND))
                    al.add(line);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return al;
    }


}
