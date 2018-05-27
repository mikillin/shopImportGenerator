package mnsk.services;


import mnsk.beans.ImportNode;
import mnsk.beans.ProductImporter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import mnsk.App;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: S.Rogachevsky
 * Date: 11.02.17
 * Time: 11:59
 */
public class BftService extends ImporterService {
    private final static String FILE_SOURCE = "C:\\data.csv";
    private final static String SEARCH_URL = "http://bft.by/rezultaty-poiska.html?search=";
    private final static int PRODUCT_CODE_SOURCE_CSV_INDEX = 0;
    public static final String BASE_BFT_URL = "http://bft.by/";
    public static final String PRODUCT_ONLY_PATTERN = ".*[\\d].*";
    public static final String SELECTOR_FOR_PRODUCT_NAME = ".B_lastCrumb";
    public static final String SELECTOR_FOR_PRODUCT_IMAGE = "#product-images a";
    public static final String SELECTOR_FOR_CLASSIFICATION = ".B_crumb";
    public static final String SELECTOR_FOR_PRODUCT_URL = "div.block-product>a";
    public static final String BRAND_SIGNAL = "Signal";
    public static final String SIZE_DELIMITER = "x";
    public static final String REDUNDANT_COST_WORDS = " руб.";
    public static final String DESCRIPTION_SELECTOR = ".description-fs .col-xs-6";
    public static final String[] REDUNDANT_PRODUCT_CODE_WORDS = {"new", "laquered", "square", "materac", "lampka",
            "nowość", "szafka", "rectangular", "white, black"};
    public static final String BRAND_HALMAR = "Halmar";


    public static void getDataForSpecialID(ArrayList<String> codes) {


        String[] splittedData;
        Set<String> productLinks = new HashSet<>();
        String productName = "";
        String cathegoryMain = "";
        String subCathegory = "";
        String price = "";
        String imageURL = "";
        Elements description;
        String material = "";
        String size = "";
        String article = "";

        try {
            ImporterService.initializeFilesHeaders();
            Connection connection = DriverManager.getConnection(App.url, App.username, App.password);
            Statement stmt = connection.createStatement();
            ResultSet rset;


            for (String code : codes) {
                code = code.replaceAll(" ", "%20");
                productLinks.clear();
                Document preview = ImporterService.getHTMLDocument(SEARCH_URL + code);
                if (preview == null) {
                    System.out.println(" no document for code >>>" + code);
                    continue;
                }
                Elements elements = preview.select(SELECTOR_FOR_PRODUCT_URL);

                if (elements.size() == 0) {
                    System.err.println(">>>>" + SEARCH_URL + code);
                }

                for (Element subURL : elements) {
                    productLinks.add(getBFTProductURL(subURL.attr("href")));
                }


                for (String productLink : productLinks) {

                    try {
                        ImportNode in = new ImportNode();
                        ProductImporter pi = new ProductImporter();
                        size = "";

                        Document product = ImporterService.getHTMLDocument(productLink);
                        price = product.select(".price").get(0).text().replace(REDUNDANT_COST_WORDS, "").trim();


                        description = product.select(DESCRIPTION_SELECTOR);
                        for (Element descriptionElement : description) {
                            if (descriptionElement.text().contains("Артикул"))
                                article = descriptionElement.nextElementSibling().text(); // for checking exact article

                            if (descriptionElement.text().contains("Длина"))
                                size += descriptionElement.nextElementSibling().text() + SIZE_DELIMITER;
                            if (descriptionElement.text().contains("Ширина"))
                                size += descriptionElement.nextElementSibling().text() + SIZE_DELIMITER;
                            if (descriptionElement.text().contains("Высота"))
                                size += (descriptionElement.nextElementSibling().text() + SIZE_DELIMITER);
                            if (descriptionElement.text().contains("Глубина"))
                                size += (descriptionElement.nextElementSibling().text() + SIZE_DELIMITER);


                            // i don't know wat is split('Материал') if (descriptionElement.text().contains("Материал") && descriptionElement.text().split("Материал").length == 1)

                            if (descriptionElement.text().contains("Материал"))
                                material = descriptionElement.nextElementSibling().text();

                        }

                        if (!article.equals(code))
                            continue;

                        productName = product.select(SELECTOR_FOR_PRODUCT_NAME).get(0).text();
                        imageURL = getBFTProductURL(product.select(SELECTOR_FOR_PRODUCT_IMAGE).attr("href"));
                        subCathegory = product.select(SELECTOR_FOR_CLASSIFICATION).last().text();
                        cathegoryMain = product.select(SELECTOR_FOR_CLASSIFICATION).last().previousElementSibling().text();

                        String imageName = ImporterService.saveImageOnDisk(imageURL);

                        size.replaceAll(";", " и "); // есть размеры для двух столов и там ; как разделитель
                        size = size.length() > 0 ? size.substring(0, size.length() - 1) + "см." : ""; // убираем последний "х"

                        in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
                        in.setName(productName);
                        in.setImage(imageName);
                        in.setPrice(price.trim().replaceAll(" ", "") + "0000");


                        pi.setSpecialKeyID(article.trim());
                        pi.setHtmlLink(productLink);
                        pi.setStatus(ProductImporter.DEFAULT_STATUS_ON);
                        pi.setDostavka(ProductImporter.DEFAULT_DOSTAVKA);
                        pi.setUstanovka(ProductImporter.DEFAULT_USTANOVKA);
                        pi.setSKU(in.getSku());
                        pi.setName(in.getName());
                        pi.setMaterial(material);
                        pi.setGabarity(size);

                        pi.setCategory(cathegoryMain);
                        pi.setPod_category(subCathegory);
                        pi.setBrand(in.getName().toUpperCase().indexOf(BRAND_SIGNAL.toUpperCase()) != -1 ? BRAND_SIGNAL : (in.getName().toUpperCase().indexOf(BRAND_HALMAR.toUpperCase()) != -1 ? BRAND_HALMAR : "NO BRAND!!"));


                        ImporterService.sbExportOne.append(in.toString());
                        ImporterService.sbExportTwo.append(pi.toString());


                        //update specialID


                        rset = stmt.executeQuery("SELECT * FROM FURNITURE WHERE SKU = '" + in.getSku() + "'");
                        if (!rset.next())
                        {
                            stmt.executeUpdate("INSERT INTO FURNITURE (SKU, SPECIALID) VALUES ('" + in.getSku() + "', '" + article.trim() + "')");
                            System.out.println("in FURNITURE Inserted specialID " + article.trim());
                        }
                        else
                        {
                            stmt.executeUpdate("UPDATE FURNITURE SET specialID = '" + article.trim() + "' WHERE SKU = '" + in.getSku() + "'");
                            System.out.println("in FURNITURE Updated specialID " + article.trim());
                        }

                        rset = stmt.executeQuery("SELECT * FROM EXTRAINFO WHERE SKU = '" + in.getSku() + "'");
                        if (!rset.next())
                        {
                            stmt.executeUpdate("INSERT INTO EXTRAINFO (SKU, SPECIALID) VALUES ('" + in.getSku() + "', '" + article.trim() + "')");
                            System.out.println("Inserted EXTRAINFO specialID " + article.trim());
                        }
                        else
                        {
                            stmt.executeUpdate("UPDATE EXTRAINFO SET specialID = '" + article.trim() + "' WHERE SKU = '" + in.getSku() + "'");
                            System.out.println("Updated EXTRAINFO specialID " + article.trim());
                        }


                        //end updating



                    } catch (Exception e) {
                        System.out.println(" code : " + code + " productLink : " + productLink + " getDataForSpecialID " + e);
                    }

                }
            }
            ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
            ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

            connection.close();

        } catch (SQLException ex) {

        }
        System.out.println("Hello!");

    }


    @Override
    public void getData() {
        List<String> csvData = new ArrayList<>();
        try {
            csvData = getAllDataFromCSVFile(FILE_SOURCE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] splittedData;
        Set<String> productLinks = new HashSet<>();
        String productName = "";
        String cathegoryMain = "";
        String subCathegory = "";
        String price = "";
        String imageURL = "";
        Elements description;
        String material = "";
        String size = "";

        for (String data : csvData) {
            splittedData = data.split(";");
            String productCode = getProductCodeWithoutRedundantWords(splittedData[PRODUCT_CODE_SOURCE_CSV_INDEX]);
            Document preview = ImporterService.getHTMLDocument(SEARCH_URL + productCode.trim());
            Elements elements = preview.select(SELECTOR_FOR_PRODUCT_URL);

            if (elements.size() == 0) {
                System.err.println(">>>>" + SEARCH_URL + productCode);
                continue;
            }

            for (Element subURL : elements) {
                productLinks.add(getBFTProductURL(subURL.attr("href")));
            }
        }

        ImporterService.initializeFilesHeaders();

        for (String productLink : productLinks) {
            ImportNode in = new ImportNode();
            ProductImporter pi = new ProductImporter();
            size = "";
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Document product = ImporterService.getHTMLDocument(productLink);
            price = product.select(".price").get(0).text().replace(REDUNDANT_COST_WORDS, "").trim();
            productName = product.select(SELECTOR_FOR_PRODUCT_NAME).get(0).text();
            imageURL = getBFTProductURL(product.select(SELECTOR_FOR_PRODUCT_IMAGE).attr("href"));
            subCathegory = product.select(SELECTOR_FOR_CLASSIFICATION).last().text();
            cathegoryMain = product.select(SELECTOR_FOR_CLASSIFICATION).last().previousElementSibling().text();

            String imageName = ImporterService.saveImageOnDisk(imageURL);


            description = product.select(DESCRIPTION_SELECTOR);
            for (Element descriptionElement : description) {
                if (descriptionElement.text().contains("Длина"))
                    size += descriptionElement.nextElementSibling().text() + SIZE_DELIMITER;
                if (descriptionElement.text().contains("Ширина"))
                    size += descriptionElement.nextElementSibling().text() + SIZE_DELIMITER;
                if (descriptionElement.text().contains("Высота"))
                    size += (descriptionElement.nextElementSibling().text() + SIZE_DELIMITER);
                if (descriptionElement.text().contains("Глубина"))
                    size += (descriptionElement.nextElementSibling().text() + SIZE_DELIMITER);
                if (descriptionElement.text().contains("Материал") && descriptionElement.text().split("Материал").length == 1)
                    material = descriptionElement.nextElementSibling().text();

            }
            size.replaceAll(";", " и "); // есть размеры для двух столов и там ; как разделитель
            size = size.length() > 0 ? size.substring(0, size.length() - 1) + "см." : ""; // убираем последний "х"

            in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
            in.setName(productName);
            in.setImage(imageName);
            in.setPrice(price.trim() + "0000");


            pi.setSKU(in.getSku());
            pi.setName(in.getName());
            pi.setMaterial(material);
            pi.setGabarity(size);

            pi.setCategory(cathegoryMain);
            pi.setPod_category(subCathegory);
            pi.setBrand(BRAND_SIGNAL);
            ImporterService.sbExportOne.append(in.toString());
            ImporterService.sbExportTwo.append(pi.toString());

        }
        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        System.out.println("Hello!");

    }


    private static List<String> getAllDataFromCSVFile(String fileSource) throws FileNotFoundException {

        List<String> al = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileSource))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                if (line.matches(PRODUCT_ONLY_PATTERN))      // choose only goods info
                    al.add(line);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return al;
    }

    public static List<String> getSpecialIDFromBFTCSVPriceFile(String fileSource) throws FileNotFoundException {

        List<String> al = new ArrayList<>();
        String[] lineElements = new String[10];
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileSource))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lineElements = line.split(";", -1);
                if (lineElements.length >= 3)
                    if (lineElements[3].equals("SIGNAL") || lineElements[3].equals("HALMAR")) {
                        al.add(lineElements[2]);
                    }

            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return al;
    }

    private static String getBFTProductURL(String subURL) {
        return BASE_BFT_URL + subURL;
    }

    private static String getProductCodeWithoutRedundantWords(String sourceProduceCode) {

        System.out.printf("");
        for (String word : REDUNDANT_PRODUCT_CODE_WORDS) {
            sourceProduceCode = sourceProduceCode.replaceAll(word, "");
        }
        return sourceProduceCode.trim();

    }

}
