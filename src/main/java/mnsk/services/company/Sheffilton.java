package mnsk.services.company;


import mnsk.App;
import mnsk.beans.Furniture;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.CategoryProcessingService;
import mnsk.services.ImporterService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import static mnsk.services.CategoryProcessingService.getSiteCategoryNames;
//new csv file format: Артикул;Номенклатура;Розница
//old csv file format: Номенклатура.Артикул ;Ценовая группа/ Номенклатура/ Характеристика номенклатуры;Опт с НДС;Розница;;
//db file format: "sku";"name";"price";"price_currency";"brand";"category";"pod_category";"material";"type";"image";"status";"budget";"ustanovka";"dostavka";"gabarity";"priznak"

/**
 * Author: S.Rogachevsky
 * Date: 11.02.17
 * Time: 11:59
 */
public class Sheffilton extends ImporterService {
    //todo: fill the source file
    private final static String FILE_SOURCE = "C:\\work\\shop\\sheffilton-14.04.2022.csv"; //todo: make an array
    private final static String FILE_CURRENT_PRODUCTS_DB = "C:\\work\\shop\\db.csv"; //todo: check always

    //TODO: fill the files
    public static final ArrayList<String> NEW_PRODUCTS_PRICES_FILES_LIST =
            new ArrayList<>(Arrays.asList(
                    "c:\\work\\shop\\sheffilton-14.04.2022 .csv"
                    // , "C:\\work\\shop\\SV-20.04-rozn list 2.csv"
                    // , "C:\\work\\shop\\SV-20.04-rozn list 3.csv"
                    // , "C:\\work\\shop\\SV-20.04-rozn list 4.csv"

            ));


    //TODO: fill the files
    public static final ArrayList<String> EXISTING_PRODUCTS_PRICES_FILES_LIST =
            new ArrayList<>(Arrays.asList(
                    "c:\\work\\shop\\sheffilton-14.04.2022 .csv"
//                    , "C:\\work\\shop\\SV-20.04-rozn list 2.csv"
//                    , "C:\\work\\shop\\SV-20.04-rozn list 3.csv"
//                    , "C:\\work\\shop\\SV-20.04-rozn list 4.csv"
            ));

    //TODO: fill the brands
    public static final ArrayList<String> BRANDS = new ArrayList<>(Arrays.asList("Sheffilton"));
    private final static String SEARCH_URL = "https://bft.by/catalog/?q=";
    private final static int PRODUCT_CODE_SOURCE_CSV_INDEX = 0;
    public static final String BASE_BFT_URL = "http://bft.by";
    public static final String PRODUCT_ONLY_PATTERN = ".*[\\d].*";
    //    public static final String SELECTOR_FOR_PRODUCT_NAME = ".B_lastCrumb";
    public static final String SELECTOR_FOR_PRODUCT_NAME = "h1";
    public static final String SELECTOR_FOR_PRODUCT_IMAGE = "div .ax-element-slider-main a";
    public static final String SELECTOR_FOR_CLASSIFICATION = ".bx-breadcrumb-item";
    //    public static final String SELECTOR_FOR_PRODUCT_URL = "div.block-product>a";
    public static final String SELECTOR_FOR_PRODUCT_URL = "div.bxr-element-image>a";

    public static final String BRAND_SV_MEBEL = "Sheffilton";
    public static final String SIZE_DELIMITER = "x";
    public static final String REDUNDANT_COST_WORDS = " руб.";
    public static final String DESCRIPTION_SELECTOR = "td.bxr-props-name";
    public static final String[] REDUNDANT_PRODUCT_CODE_WORDS = {"new", "laquered", "square", "materac", "lampka", "nowość", "szafka", "rectangular", "white, black"};

    ArrayList<String> specificBrandExistingProductData = new ArrayList<>();

    ArrayList<String> existingProductsNewPrice = new ArrayList<>();
    ArrayList<String> existingProductsTheSamePrice = new ArrayList<>();
    ArrayList<String> newProducts = new ArrayList<>();
    ArrayList<String> deleteProducts = new ArrayList<>();

    static HashMap<String, String> dbDataFor4AllDefinedBrands = new HashMap<>(); //main base with old existing data
    static HashMap<String, String> oldProductsHM = new HashMap<>();
    static HashMap<String, String> newProductsHM = new HashMap<>();

    static String existingProductPricesFileName = "";
    static String newProductPricesFileName = "";


    /////////// TODO:: CSV File should be without char ; - it is in the description fieild


    ////////// TODO: CHECK THE MESSAGE ABOVE
    @Override
    public void getData() {

        fillDBDataFor4Brands();

        ImporterService.initializeFilesHeaders();

        //System.out.println("--" + getSiteCategoryNames("brand", "cat", "sub", "name"));

        ArrayList<String[]> newDataSplitted = new ArrayList<>();
        for (String fileWithData : EXISTING_PRODUCTS_PRICES_FILES_LIST) {
            try {
                String resultLine = "";
                String[] tmpLineSplitted;
                for (String line : getAllDataFromCSVFile(fileWithData)) {
                    tmpLineSplitted = line.split(";", -1);
                    if (tmpLineSplitted.length != 15 && tmpLineSplitted.length != 13) { //part of product, 12 -new product partly
                        if (!StringUtils.isEmpty(tmpLineSplitted[0]))
                            resultLine += "#" + line;//# for making row later
                    } else if (tmpLineSplitted.length == 13) {//  part of a new priodcut

                        if (!StringUtils.isEmpty(resultLine)) { // description has several lines
                            newDataSplitted.add(resultLine.split(";", -1));
                        }
                        resultLine = line;
                    } else { // new product

                        if (tmpLineSplitted[1].length() > 1) //not empty
                            newDataSplitted.add(tmpLineSplitted);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String dataFromDB = "";
        String[] dataFromDBSplitted = null;
        for (String[] data : newDataSplitted) {
            //todo: change as the name on the site is another as in the pricelist it is too long
            if (data.length < 4 || data[3] == null || StringUtils.isEmpty(data[3]))
                continue;
            dataFromDB = dbDataFor4AllDefinedBrands.get(String.valueOf(data[3]));
            if (dataFromDB == null) { // name of the product was in the DB
                //this means that the price will be new
                //TODO: make a method to add data as new
                //check from DB wil be with quotes " - and then check without  them
                newProducts.add(String.join(";",
                        new String[]{
                                data[1].replaceAll("\"", "").replaceAll(";", ","),
                                data[3].replaceAll("\"", "").replaceAll(";", ","),
                                data[13].replaceAll("\"", "").replaceAll(";", ",")}));
                // todo:
                //  PRODUCT_CODE_SOURCE_CSV_INDEX= 0,
                //  name,
                //  cost

            } else {
                // old product
                dataFromDBSplitted = dataFromDB.split(";");
                if ((String.valueOf(data[2]) + "0000").equals(String.valueOf(dataFromDBSplitted[2]).replaceAll("\"", ""))) {
                    //the same price

                    existingProductsTheSamePrice.add(dataFromDB);
                } else {
                    // another price
                    dataFromDBSplitted[2] = data[13] + "0000";
                    String.join(";", dataFromDBSplitted);
                    existingProductsNewPrice.add(dataFromDB);
                }
            }
        }

        String[] valueParsed = null;
        String[] valueCheckedParsed = null;

        for (String value : dbDataFor4AllDefinedBrands.values()) {
            valueParsed = value.split(";");

            for (String item : existingProductsTheSamePrice) {
                valueCheckedParsed = item.split(";");
                if (valueParsed[0].equals(valueCheckedParsed[0])) { //sku was already processed
                    continue;
                }
            }
            for (String item : existingProductsNewPrice) {
                valueCheckedParsed = item.split(";");
                if (valueParsed[0].equals(valueCheckedParsed[0])) { //sku was already processed
                    continue;
                }
            }

            deleteProducts.add(value);
        }


        fillData(existingProductsNewPrice, newProducts, deleteProducts);


        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        System.out.println("Hello! " + FILE_SOURCE);

    }


    private static void fillData
            (ArrayList<String> existingProductsNewPrice, ArrayList<String> newProducts, ArrayList<String> deleteProducts) {
        ArrayList<String> csvData;
        ImportNode in = new ImportNode();
        String[] splittedData;

        //fill 1 file: only new price
        for (String item : existingProductsNewPrice) {
            splittedData = item.split(";", -1);

            in.setSku(splittedData[0]);
            in.setImage(splittedData[9]);
            in.setPRICE_CURRENCY(splittedData[3]);

            in.setName(splittedData[1]); //change name from the new price list
            in.setPrice(splittedData[2]);    //changed cost
            ImporterService.sbExportOne.append(in.toString());

        }


        String[] lineToHideSplitted;
        for (String lineToHide : deleteProducts) {
            lineToHideSplitted = lineToHide.split(";", 0);

            ProductImporter pi = new ProductImporter();
            pi.setSKU(String.valueOf(lineToHideSplitted[0]));

            pi.setMaterial("deleted");
            pi.setGabarity("deleted");


            //TODO: fill in
            pi.setStatus("0");
            pi.setName("name");
            pi.setCategory("deleted");
            pi.setPod_category("deleted");
            pi.setBrand("deleted");


            ImporterService.sbExportTwo.append(pi);
        }


// fill new products
        csvData = newProducts;

        ArrayList<Furniture> furnitureItems = new ArrayList<>();

        String productName = "";
        String cathegoryMainFromVendorSite = "";
        String subCathegoryFromVendorSite = "";
        String price = "";
        String imageURL = "";
        Elements description;
        String material = "";
        String size = "";

        for (String data : csvData) {
            splittedData = data.split(";", 0);

            if (splittedData == null || splittedData.length < 1 || StringUtils.isEmpty(splittedData[0])) // head of product types for Sokol pnly
                continue;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String productCode = getProductCodeWithoutRedundantWords(splittedData[PRODUCT_CODE_SOURCE_CSV_INDEX]);
            Document preview = ImporterService.getHTMLDocument(SEARCH_URL + productCode.trim().replaceAll(" ", "+"));
            if (preview == null) {
                System.err.println("preview is null >>>>" + SEARCH_URL + productCode);
                continue;
            }
            Elements elements = preview.select(SELECTOR_FOR_PRODUCT_URL);

            if (elements.size() == 0) {
                System.err.println(">>>>" + SEARCH_URL + productCode);
                continue;
            }


            for (Element subURL : elements) {
                try {
                    //todo: implement
                    //if (!CategoryProcessingService.isProductTypeVorbidden("SV-Мебель", splittedData[1])) //check for the exception in categories, whcih shouldn't be added
                    furnitureItems.add(new Furniture(splittedData[1], Integer.parseInt(splittedData[2].replaceAll(" ", "")), getBFTProductURL(subURL.attr("href"))));
                } catch (Exception exc) {
                    System.err.println(">>> productCode: " + productCode + ", exc : " + exc);
                }
            }
        }


        for (Furniture furnitureItem : furnitureItems) {
            ProductImporter pi = new ProductImporter();
            size = "";
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Document product = ImporterService.getHTMLDocument(furnitureItem.getLink());

            if (product == null) {
                System.err.println(">> error: " + furnitureItem.getLink());
                continue;
            }
            Elements imageElements = product.select(SELECTOR_FOR_PRODUCT_IMAGE);
            String imageCorrectURL = "";

            if (imageElements.size() == 0 || imageElements.get(0).attr("href").equals("")) {
                System.err.println("No Image furnitureItem.getLink(): " + furnitureItem.getLink());
                imageCorrectURL = "C:\\git\\shopImportGenerator\\view\\noimage.jpg";
            } else {
                imageCorrectURL = imageElements.get(0).attr("href");

            }
            imageURL = getBFTProductURL(imageCorrectURL);
            subCathegoryFromVendorSite = product.select(SELECTOR_FOR_CLASSIFICATION).last().text();
            cathegoryMainFromVendorSite = product.select(SELECTOR_FOR_CLASSIFICATION).last().previousElementSibling().text();

//            if (!CategoryProcessingService.isProductTypeVorbidden("SV-Мебель", cathegoryMainFromVendorSite, subCathegoryFromVendorSite)) //check for the exception in categories, whcih shouldn't be added
//                System.out.println("");
            String imageName = ImporterService.saveImageOnDisk(imageURL);


            description = product.select(DESCRIPTION_SELECTOR);
//            for (Element descriptionElement : description) {
//                if (descriptionElement.text().trim().contains("Длина"))
//                    size += descriptionElement.nextElementSibling().text() + SIZE_DELIMITER;
//                if (descriptionElement.text().trim().contains("Ширина"))
//                    size += descriptionElement.nextElementSibling().text() + SIZE_DELIMITER;
//                if (descriptionElement.text().trim().contains("Высота"))
//                    size += (descriptionElement.nextElementSibling().text() + SIZE_DELIMITER);
//                if (descriptionElement.text().trim().contains("Глубина"))
//                    size += (descriptionElement.nextElementSibling().text() + SIZE_DELIMITER);
//                if (descriptionElement.text().trim().contains("Материал") && descriptionElement.text().split("Материал").length == 1)
//                    material = descriptionElement.nextElementSibling().text();
//
//            }
            size.replaceAll(";", " и "); // есть размеры для двух столов и там ; как разделитель
            size = size.length() > 0 ? size.substring(0, size.length() - 1) + "см." : ""; // убираем последний "х"

            in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
            in.setName(furnitureItem.getName());
            in.setImage(imageName);
            in.setPrice(furnitureItem.getCost() + "0000");


            pi.setSKU(in.getSku());
            pi.setName(in.getName());
            pi.setMaterial(material);
            pi.setGabarity(size);
            String priznak = "";
            Random rand = new Random();
            int n = rand.nextInt(500);
            if (in.getName().toUpperCase().contains("NEW") || n == 1) {
                priznak = "47"; //new product
            }
            n = rand.nextInt(100);
            if (n == 1)
                priznak = "46"; //best
            n = rand.nextInt(50);
            if (n == 1)
                priznak = "45"; // lower price
            pi.setPriznak(priznak);
            pi.setBrand(BRAND_SV_MEBEL);

            CategoryProcessingService.InnerClass siteCategoryInfo = getSiteCategoryNames(pi.getBrand(), cathegoryMainFromVendorSite, subCathegoryFromVendorSite, in.getName());

            // todo: return classification

            pi.setCategory(siteCategoryInfo.categoryName);
            pi.setPod_category(siteCategoryInfo.subcategoryName);
//            pi.setCategory(cathegoryMainFromVendorSite);
//            pi.setPod_category(subCathegoryFromVendorSite);
            pi.setStatus(CategoryProcessingService.isProductTypeVorbidden(pi.getBrand(), siteCategoryInfo.categoryName, siteCategoryInfo.subcategoryName, in.getName()) ? "0" : "1");
            ImporterService.sbExportOne.append(in.toString());
            ImporterService.sbExportTwo.append(pi.toString());

        }

    }


    private static ArrayList<String> getAllDataFromCSVFile(String fileSource) throws FileNotFoundException {

        ArrayList<String> al = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileSource))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                // if (line.matches(PRODUCT_ONLY_PATTERN))      // choose only goods info
                al.add(line);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return al;
    }


    private static String getBFTProductURL(String subURL) {
        if (subURL.indexOf("noimage") == -1)
            return BASE_BFT_URL + subURL;
        else
            return subURL;
    }

    private static String getProductCodeWithoutRedundantWords(String sourceProduceCode) {

        for (String word : REDUNDANT_PRODUCT_CODE_WORDS) {
            sourceProduceCode = sourceProduceCode.replaceAll(word, "");
        }
        return sourceProduceCode.trim();

    }


    private void fillOldNewProductsHMs() {
        ArrayList<String> fileData = new ArrayList();

        //fill new data
        try {
            fileData = getAllDataFromCSVFile(newProductPricesFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] splittedData;

        for (String item : fileData) {
            splittedData = item.split(";", 0);
            if (splittedData != null && splittedData.length > 0)
                newProductsHM.put(splittedData[0], item); // HashMap <code: info>
        }


        //fill old data
        try {
            fileData = getAllDataFromCSVFile(existingProductPricesFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (String item : fileData) {
            splittedData = item.split(";", 0);
            if (splittedData != null && splittedData.length > 1)
                oldProductsHM.put(splittedData[1], splittedData[0]); // HashMap <oldname: code info>
        }
    }


    private void fillDBDataFor4Brands() {
        ArrayList<String> existingProductDataFromDB = new ArrayList();
        try {
            existingProductDataFromDB = getAllDataFromCSVFile(FILE_CURRENT_PRODUCTS_DB);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] splittedData;
        String brand = "";
        for (String item : existingProductDataFromDB) {
            splittedData = item.split(";", 0);

            brand = String.valueOf(splittedData[4]);
            brand = brand.substring(1, brand.length() - 1); // without signs "
            if (BRANDS.contains(brand)) {
                dbDataFor4AllDefinedBrands.put(splittedData[1].replaceAll("\"", "").trim(), item.replaceAll("\"", "")); // HashMap <name: all info>
            }
        }

    }
}
