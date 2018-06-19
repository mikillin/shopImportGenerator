package mnsk;

import mnsk.beans.ImportNode;
import mnsk.beans.ProductImporter;
import mnsk.services.ImporterService;
import org.jsoup.select.Elements;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ImportFileGenerator {

    public final static String SOURCE_FILE_DB = "d:/source.xml";
    public final static String SOURCE_FILE_CSV_BFT = "d:/source.csv";
    // public final static String SOURCE_FILE_CSV_WITH_SPECIALID = "d:/сodes.csv";
//    public final static String SOURCE_FILE_DB = "C:/source.xml";
//    public final static String SOURCE_FILE_DB = "d:/src.xml";

    private final static String FILE_SOURCE = "C:\\data.csv";
    private final static String SEARCH_URL = "https://bft.by/catalog/?q=";
    private final static int PRODUCT_CODE_SOURCE_CSV_INDEX = 0;
    public static final String BASE_BFT_URL = "http://bft.by/";
    public static final String PRODUCT_ONLY_PATTERN = ".*[\\d].*";
    public static final String SELECTOR_FOR_PRODUCT_NAME = ".B_lastCrumb";
    public static final String SELECTOR_FOR_PRODUCT_IMAGE = "img.zoom-img";
    //    public static final String SELECTOR_FOR_CLASSIFICATION = ".B_crumb";
    public static final String SELECTOR_FOR_CLASSIFICATION = ".bx-breadcrumb-item";
    public static final String SELECTOR_FOR_PRODUCT_URL = "div.bxr-element-name>a";
    public static final String BRAND_SIGNAL = "Signal";
    public static final String SIZE_DELIMITER = "x";
    public static final String REDUNDANT_COST_WORDS = " руб.";
    public static final String DESCRIPTION_SELECTOR = ".description-fs .col-xs-6";
    public static final String[] REDUNDANT_PRODUCT_CODE_WORDS = {"new", "laquered", "square", "materac", "lampka",
            "nowość", "szafka", "rectangular", "white, black"};
    public static final String BRAND_HALMAR = "Halmar";


    //delete in xml first DB initializyytion
    public static void main(String[] args) {
        generateImportFiles();
    }


    //TODO: !!!
    //TODO: проверить все ли из базы у нас есть.. пока без них
    //TODO: !!!
    public static void generateImportFiles() {

        try {
            File inputFileDB = new File(SOURCE_FILE_DB);
            File inputFileCSVBFT = new File(SOURCE_FILE_CSV_BFT);
            //    File inputFileCSVspecialID = new File(SOURCE_FILE_CSV_WITH_SPECIALID);


            Connection connection = DriverManager.getConnection(App.url, App.username, App.password);
            Statement stmt = connection.createStatement();
            ResultSet rset;

            //
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFileDB);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("table");
            String[] row;
            Furniture furnitureDB;


            Scanner sc;

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

            //TODO: updating sku with specialID from file.
            /* sc = new Scanner(inputFileCSVspecialID);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                row = line.split(";");
                String sku = row[0];
                String specialID = row[3];

                if (sku != null && specialID != null && !sku.equals("") && !specialID.equals("")) {
                    stmt.executeQuery("UPDATE FURNITURE SET specialID = " + specialID + " WHERE SKU = '" + sku + "'");
                }
            }


*/
            ImporterService.initializeFilesHeaders();


            sc = new Scanner(inputFileCSVBFT);
            ArrayList<String> alCSVBFT = new ArrayList();
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();


                //TODO: Add Halmar and others file!
// here we have several particular price lists. No need to seek for strings with special vendor
//                if (line != null && line.contains(";Signal;"))
                alCSVBFT.add(line);
            }


            // пройти по базе.. если есть 1 статус и нет кода...

            // go by rows of source
            for (int i = 0; i < alCSVBFT.size(); i++) {
                String[] bftData = alCSVBFT.get(i).split(";", -1);
                String bftCode = bftData[1]; // specialID
                price = bftData[2]; // price

                if (price.contains(",") || price.contains("."))
                    System.out.println("error price, bftCode = " + bftCode);

                try {
                    Integer.valueOf(price);
                } catch (NumberFormatException exception) {
                    System.out.println("can't parse the price for code: " + bftCode);
                    continue;
                }
                rset = stmt.executeQuery("SELECT * FROM FURNITURE WHERE specialID = '" + bftCode + "'");
                if (rset.next()) {
                    String cost = rset.getString("cost");
                    if (cost != null && !cost.equals(price + "0000")) {

                        String query = "UPDATE FURNITURE SET COST = " + price + "0000" + " WHERE specialID = '" + bftCode + "'";
                        stmt.executeUpdate(query);
                        System.out.println("Update specialID = " + bftCode);
                    }
                } else {
                    //add new product

                    /*

                    //parse HTML page. Save photo, add to local database
                    String code = bftCode;
                    code = code.replaceAll(" ", "%20");
                    productLinks.clear();
                    org.jsoup.nodes.Document preview = ImporterService.getHTMLDocument(SEARCH_URL + code); //V-CH-W27-WIESZAK +
                    if (preview == null) {
                        System.out.println(" no document for code >>>" + code);
                        continue;
                    }
                    Elements elements = preview.select(SELECTOR_FOR_PRODUCT_URL);

                    if (elements.size() == 0) {
                        System.err.println(">>>> no data for: " + SEARCH_URL + code);
                        continue;
                    }

                    for (org.jsoup.nodes.Element subURL : elements) {
                        productLinks.add(getBFTProductURL(subURL.attr("href")));
                    }


                    for (String productLink : productLinks) {

                        try {
                            ImportNode in = new ImportNode();
                            ProductImporter pi = new ProductImporter();
                            size = "";

                            org.jsoup.nodes.Document product = ImporterService.getHTMLDocument(productLink);
                            // price = product.select(".price").get(0).text().replace(REDUNDANT_COST_WORDS, "").trim();
                            price = product.select("meta[itemprop=price]").get(0).attr("content");

                            description = product.select(DESCRIPTION_SELECTOR);
                            for (org.jsoup.nodes.Element descriptionElement : description) {
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

                            // if (!article.equals(code))
                            //     continue;
                            productName = "";
                            if (product.select(SELECTOR_FOR_PRODUCT_NAME).size() > 0)
                                productName = product.select(SELECTOR_FOR_PRODUCT_NAME).get(0).text();

                            imageURL = "";
                            if (product.select(SELECTOR_FOR_PRODUCT_IMAGE).attr("src") != null)
                                imageURL = product.select(SELECTOR_FOR_PRODUCT_IMAGE).attr("src");

                            subCathegory = "";
                            if (product.select(SELECTOR_FOR_CLASSIFICATION) != null)
                                subCathegory = product.select(SELECTOR_FOR_CLASSIFICATION).last().text();

                            cathegoryMain = "";
                            if (product.select(SELECTOR_FOR_CLASSIFICATION) != null)
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


                            //update specialID
                            String query =
                                    "INSERT INTO FURNITURE " +
                                            "(sku, specialID, name, image, cost, currency, vendor, category, subcategory, feature, material, size," +
                                            "status, description, installation, delivery,  type)" + // no budget
                                            "VALUES (" +
                                            "'" + in.getSku() + "', " +
                                            "'" + bftCode + "', " +
                                            "'" + in.getName() + "', " +
                                            "'" + in.getImage() + "', " +
                                            "'" + in.getPrice() + "', " +
                                            "'" + in.getPRICE_CURRENCY() + "', " +
                                            "'" + pi.getBrand() + "', " +
                                            "'" + pi.getCategory() + "', " +
                                            "'" + pi.getPod_category() + "', " +
                                            "'" + pi.getPriznak() + "', " +
                                            "'" + pi.getMaterial() + "', " +
                                            "'" + pi.getGabarity() + "', " +
                                            "'" + pi.getStatus() + "', " +
                                            "'" + pi.getOpisanie() + "', " +
                                            "'" + pi.getUstanovka() + "', " +
                                            "'" + pi.getDostavka() + "', " +
                                            "'" + pi.getType() + "' " +
                                            ")";
                            System.out.println(">>> insert new: " + query);
                            try {
                                stmt.executeUpdate(query);
                            } catch (SQLException sqlException) {
                                System.out.println(">>> insert error: " + sqlException);
                            }

                            //end updating


                        } catch (Exception e) {
                            System.out.println(" code : " + code + " productLink : " + productLink + " getDataForSpecialID " + e);
                        }

                    }*/
                    //load from the site all information, add new SKU, download image

                }

            }

            //TODO: after filling the local database it is time to upload data from database to import files.


            for (int temp = 0; temp < nList.getLength(); temp++) {
                //table
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    furnitureDB = fillFurnitureObject(eElement);
                    if (furnitureDB.getVendor().equalsIgnoreCase("Signal") || furnitureDB.getVendor().equalsIgnoreCase("Halmar")) {
                        rset = stmt.executeQuery("SELECT * FROM FURNITURE WHERE SKU = '" + furnitureDB.getSku() + "'");

                        ImportNode in = new ImportNode();
                        ProductImporter pi = new ProductImporter();

                        in.setSku(furnitureDB.getSku());
                        in.setName(furnitureDB.getName());
                        in.setImage(furnitureDB.getImage());
                        in.setPrice(String.valueOf(furnitureDB.getCost()));

                        //default

                        pi.setDostavka(ProductImporter.DEFAULT_DOSTAVKA);
                        pi.setUstanovka(ProductImporter.DEFAULT_USTANOVKA);
                        pi.setSKU(in.getSku());
                        pi.setName(in.getName());

                        pi.setMaterial(furnitureDB.getMaterial());
                        pi.setGabarity(furnitureDB.getSize());

                        pi.setCategory(furnitureDB.getCategory());
                        pi.setPod_category(furnitureDB.getSubcategory());
                        pi.setBrand(in.getName().toUpperCase().indexOf(BRAND_SIGNAL.toUpperCase()) != -1 ? BRAND_SIGNAL : (in.getName().toUpperCase().indexOf(BRAND_HALMAR.toUpperCase()) != -1 ? BRAND_HALMAR : "NO BRAND!!"));

                        if (!rset.next() || rset.getString("status") == null || rset.getString("status").equals("0")) {
                            pi.setStatus(ProductImporter.DEFAULT_STATUS_OFF);

                        } else {
                            pi.setStatus(ProductImporter.DEFAULT_STATUS_ON);
                        }

                        ImporterService.sbExportOne.append(in.toString());
                        ImporterService.sbExportTwo.append(pi.toString());
                    }
                }


            }

            ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
            ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * parse column elements and fills the furniture object
     *
     * @param element
     * @return
     */
    private static Furniture fillFurnitureObject(Element element) {

        Furniture furniture = new Furniture();
        NodeList nl = element.getChildNodes();
//columns
        for (int i = 1; i <= nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node == null || node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            String value = node.getTextContent();
            String columnName = node.getAttributes().item(0).getTextContent();
            switch (columnName) {
                case "sku":
                    furniture.setSku(value);
                    break;
                case "name":
                    furniture.setName(value);
                    break;
                case "price":
                    furniture.setCost(Integer.parseInt(value));
                    break;
                case "price_currency":
                    furniture.setCurrency(value);
                    break;
                case "brand":
                    furniture.setVendor(value);
                    break;
                case "category":
                    furniture.setCategory(value);
                    break;
                case "pod_category":
                    furniture.setSubcategory(value);
                    break;
                case "material":
                    furniture.setMaterial(value);
                    break;
                case "type":
                    furniture.setType(value);
                    break;
                case "opisanie":
                    furniture.setDescription(value);
                    break;
                case "image":
                    furniture.setImage(value);
                    break;
                case "status":
                    furniture.setStatus(value.equals("1") ? 1 : 0);
                    break;
                case "budget":
                    furniture.setBudget(value.equals("1") ? 1 : 0);
                    break;
                case "ustanovka":
                    furniture.setInstallation(value);
                    break;
                case "dostavka":
                    furniture.setDelivery(value);
                    break;
                case "gabarity":
                    furniture.setSize(value);
                    break;
                case "priznak":
                    furniture.setFeature(value);
                    break;

            }
        }

        return furniture;

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
