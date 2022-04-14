package mnsk.services.company;


import mnsk.App;
import mnsk.beans.Furniture;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.ImporterService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Author: S.Rogachevsky
 * Date: 11.02.17
 * Time: 11:59
 */
public class LoadAllBftProductsService extends ImporterService {
    //todo: fill the source file
    private final static String FILE_SOURCE = "C:\\work\\shop\\Halmar-24.02.csv"; //todo: make an array

    //TODO: fill the files
    public static final ArrayList<String> NEW_PRODUCTS_PRICES_FILES_LIST = new ArrayList<>(Arrays.asList(
            "C:\\work\\shop\\SIGNAL-12.04.2022-rozn.csv",
            "C:\\work\\shop\\HALMAR-12.04.2022-rozn.csv"
    ));


    //TODO: fill the files
    public static final ArrayList<String> FORMER_PRODUCTS_PRICES_FILES_LIST = new ArrayList<>(Arrays.asList(
            "C:\\work\\shop\\Signal-24.02.csv",
            "C:\\work\\shop\\Halmar-24.02.csv"
    ));

    //TODO: fill the brands
    public static final ArrayList<String> BRANDS = new ArrayList<>(Arrays.asList(
            "Halmar",
            "Signal"
    ));
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
    public static final String BRAND_SIGNAL = "Signal";
    public static final String BRAND_HALMAR = "Halmar";
    public static final String SIZE_DELIMITER = "x";
    public static final String REDUNDANT_COST_WORDS = " руб.";
    public static final String DESCRIPTION_SELECTOR = "td.bxr-props-name";
    public static final String[] REDUNDANT_PRODUCT_CODE_WORDS = {"new", "laquered", "square", "materac", "lampka",
            "nowość", "szafka", "rectangular", "white, black"};


    @Override
    public void getData() {

        List<String> formerData = new ArrayList<>();
        List<String> newData = new ArrayList<>();
        String formerProductPricesFileName = "";
        String newProductPricesFileName = "";

        for (String brand: BRANDS){
            for (String formerProductListFilename: FORMER_PRODUCTS_PRICES_FILES_LIST){
                if(formerProductListFilename.indexOf(brand) != -1)
                    formerProductPricesFileName = formerProductListFilename;
                break;
            }
            for (String newProductListFilename: NEW_PRODUCTS_PRICES_FILES_LIST){
                if(newProductListFilename.indexOf(brand) != -1)
                    newProductPricesFileName = newProductListFilename;
                break;
            }
            //TODO: read files
            List<String> formerProductData = new ArrayList<>();
            try {
                formerProductData = getAllDataFromCSVFile(formerProductPricesFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            List<String> newProductData = new ArrayList<>();
            try {
                newProductData = getAllDataFromCSVFile(formerProductPricesFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //TODO: compare old-new data
            //format sgould be:
            //HEADER
            //lines of data
            for(String data : newProductData){

            }
        }

        List<String> csvData = new ArrayList<>();
        try {
            csvData = getAllDataFromCSVFile(FILE_SOURCE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] splittedData;
        ArrayList<Furniture> furnitureItems = new ArrayList<>();

        //todo: delete
//        furnitureItems.add(
//                new Furniture("Name", 4,"https://bft.by/catalog/mebel_1/kresla_ud/kompyuternye_kresla/kreslo_kompyuternoe_halmar_gonzo_2_belyy/"
//                ));


        String productName = "";
        String cathegoryMain = "";
        String subCathegory = "";
        String price = "";
        String imageURL = "";
        Elements description;
        String material = "";
        String size = "";
        String categoryName = "";
        String subCategoryName = "";
        boolean isCheckSubCategoryNameFlag = false;
        for (String data : csvData) {
            //todo: delete
//            if (data != null)
//                break;

            splittedData = data.split(";", 0);
            if (splittedData[0] == null
                    || splittedData[0].length() == 1 // ehhhrrr... only this works
                    || "".equals(splittedData[0])
                    || StringUtils.isEmpty(splittedData[0])) {
                if (!isCheckSubCategoryNameFlag) {
                    categoryName = splittedData[1];
                    subCategoryName = "";
                    isCheckSubCategoryNameFlag = true;
                    continue;
                } else {
                    subCategoryName = splittedData[1];
                    isCheckSubCategoryNameFlag = false;
                    continue;
                }
            }

            try {
                Thread.sleep(1000);
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
                    if (!CategoryProcessingService.checkProductCategoryException(splittedData[1])) //check for the exception in categories, whcih shouldn't be added
                        furnitureItems.add(
                                new Furniture(splittedData[1],
                                        Integer.parseInt(splittedData[3]),
                                        getBFTProductURL(subURL.attr("href"))));
                } catch (Exception exc) {
                    System.err.println(">>> productCode: " + productCode);
                }
            }
        }

        ImporterService.initializeFilesHeaders();

        for (Furniture furnitureItem : furnitureItems) {
            ImportNode in = new ImportNode();
            ProductImporter pi = new ProductImporter();
            size = "";
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Document product = ImporterService.getHTMLDocument(furnitureItem.getLink());

            imageURL = getBFTProductURL(product.select(SELECTOR_FOR_PRODUCT_IMAGE).attr("href"));
            subCathegory = product.select(SELECTOR_FOR_CLASSIFICATION).last().text();
            cathegoryMain = product.select(SELECTOR_FOR_CLASSIFICATION).last().previousElementSibling().text();

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

            pi.setStatus("1");
            pi.setCategory(cathegoryMain);
            pi.setPod_category(subCathegory);
            pi.setBrand(furnitureItem.getName().contains(BRAND_SIGNAL) ? BRAND_SIGNAL : (furnitureItem.getName().contains(BRAND_HALMAR) ? BRAND_HALMAR : ""));
            ImporterService.sbExportOne.append(in.toString());
            ImporterService.sbExportTwo.append(pi.toString());

        }
        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        System.out.println("Hello! " + FILE_SOURCE);

    }


    private static List<String> getAllDataFromCSVFile(String fileSource) throws FileNotFoundException {

        List<String> al = new ArrayList<>();
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
