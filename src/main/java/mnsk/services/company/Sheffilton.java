package mnsk.services.company;


import mnsk.App;
import mnsk.beans.Furniture;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.CategoryProcessingService;
import mnsk.services.ImporterService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import static mnsk.services.CategoryProcessingService.getSiteCategoryNames;
//new csv file format: Артикул;Номенклатура;Розница
//old csv file format: Номенклатура.Артикул ;Ценовая группа/ Номенклатура/ Характеристика номенклатуры;Опт с НДС;Розница;;
//db file format: "sku";"name";"price";"price_currency";"brand";"category";"pod_category";"material";"type";"image";"status";"budget";"ustanovka";"dostavka";"gabarity";"priznak"

/**
 * Author: S.Rogachevsky
 * Date: 11.02.17
 * Time: 11:59
 */
public class Sheffilton extends BFTCommon {
    //TODO: fill the files

    //TODO::: CHECK dot, komma, ; as separate sign in all files

    //TODO: delete column with pics
    //TODO: delete column with description


    public static final String BRAND = "Sheffilton";
    public static final int NEW_PRODUCT_FILE_INDEX_FIELD_SKU = 1; // todo: check please
    public static final int NEW_PRODUCT_FILE_INDEX_FIELD_NAME = 2; // todo: check please
    public static final int NEW_PRODUCT_FILE_INDEX_FIELD_PRICE = 11;
    public static final int DB_LINE_INDEX_FIELD_SKU = 0;
    public static final int DB_FILE_LINE_INDEX_FIELD_SKU = 0;
    public static final int DB_FILE_LINE_INDEX_FIELD_NAME = 1;
    public static final int DB_FILE_LINE_INDEX_FIELD_CURRENCY = 3;
    public static final int DB_FILE_LINE_INDEX_FIELD_BRAND = 4;
    public static final int DB_FILE_LINE_INDEX_FIELD_IMAGE = 10;

    public static String NEW_PRODUCTS_PRICES_FILE = "C:\\work\\shop\\Sheffilton-24.05-rozn.csv";


    public static final String SELECTOR_FOR_PRODUCT_IMAGE = "div .ax-element-slider-main a";
    public static final String SELECTOR_FOR_CLASSIFICATION = ".bx-breadcrumb-item";
    public static final String SELECTOR_FOR_PRODUCT_URL = "div.bxr-element-image>a";
    public static final String DESCRIPTION_SELECTOR = "td.bxr-props-name";

    public static final String[] REDUNDANT_PRODUCT_CODE_WORDS = {};


    ArrayList<String> existingProductData = new ArrayList<>();
    ArrayList<String> newProducts = new ArrayList<>();
    ArrayList<String> deleteProducts = new ArrayList<>();

    static HashMap<String, String> dbData = new HashMap<>(); //main base with old existing data
    static HashMap<String, String> oldRelationNameSKUHM = new HashMap<>();
    static HashMap<String, String> newRelationSKUAllInfoHM = new HashMap<>();


    ArrayList<String> existingProductsWithNewPrice = new ArrayList<>();

    @Override
    public void getData() {

        dbData = fillDBDataHSForExactBrand(BRAND);

        ImporterService.initializeFilesHeaders();


        ArrayList<String> newProductData = new ArrayList<>();
        try {
            newProductData = getAllDataFromCSVFile(NEW_PRODUCTS_PRICES_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // fill for speedy working
        fillNewData();

        String[] newProductLineParsed;
        boolean existingProduct;
        for (String newProductDataLine : newProductData) {
            newProductLineParsed = newProductDataLine.split(";", 0);
            if (isLineNotContainsSKUOnTheFirstPosition(newProductLineParsed)) {
                continue;
            }
            if (dbData.get(newProductLineParsed[2]) == null) //if not in the list of the existing products, than it should be added as new one
            {
                newProducts.add(newProductDataLine);
            } else {
                existingProductsWithNewPrice.add(newProductDataLine);
            }
        }

        //delete obsolete products

        ArrayList<String> dataFromDB = new ArrayList<>();

        try {
            dataFromDB = getAllDataFromCSVFile(FILE_CURRENT_PRODUCTS_DB);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        String[] splittedDBLine;
        String[] splittedNewPriceLine;
        String name = "";
        boolean isExists;
        String productBrand = "";


        //check for products which should be deleted
        // just go through new and from db. if no in db
        for (String dataFromDBLine : dataFromDB) {

            splittedDBLine = dataFromDBLine.split(";", 0);
            productBrand = String.valueOf(splittedDBLine[DB_FILE_LINE_INDEX_FIELD_BRAND]); // brand
            if (productBrand.indexOf("\"") != -1)
                productBrand = productBrand.substring(1, productBrand.length()); // without signs "
            if (!productBrand.equalsIgnoreCase(BRAND))
                continue;
            isExists = false;
            for (String newProductDataLine : newProductData) {
                splittedNewPriceLine = newProductDataLine.split(";", 0);
                if (splittedNewPriceLine.length == 0)
                    continue;
                name = String.valueOf(splittedNewPriceLine[2]);
                if (name.contains("\""))
                    name = name.substring(1, name.length()); // without signs "
                if (String.valueOf(splittedDBLine[DB_FILE_LINE_INDEX_FIELD_NAME]).equals(name)) {
                    isExists = true;
                    break;
                }
            }
            if (!isExists) {
                deleteProducts.add(dataFromDBLine);
            }
        }


        fillData(existingProductsWithNewPrice, newProducts, deleteProducts);

        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        System.out.println("Hello! ");
    }

    private static void fillData(ArrayList<String> existingProductsNewPrice, ArrayList<String> newProducts, ArrayList<String> deleteProducts) {
        ArrayList<String> csvData = new ArrayList<>();
        ImportNode in = new ImportNode();
        String[] splittedData;
        String[] newProductLineSplitted;

        String[] splittedProductInfo;

        //existing
        for (String item : existingProductsNewPrice) {

            in = new ImportNode();
            newProductLineSplitted = item.split(";", 0);

            in.setPrice(newProductLineSplitted[NEW_PRODUCT_FILE_INDEX_FIELD_PRICE].replaceAll(" ", "") + "0000");    //changed cost
            in.setName(newProductLineSplitted[NEW_PRODUCT_FILE_INDEX_FIELD_NAME]);

//            String temp = newRelationSKUAllInfoHM.get(oldRelationNameSKUHM.get(infoSplitted[DB_FILE_LINE_INDEX_FIELD_NAME].replaceAll("\"", "")));
            String temp = dbData.get(newProductLineSplitted[NEW_PRODUCT_FILE_INDEX_FIELD_NAME]);
            //should be ?
            if (temp == null)
                temp = dbData.get(newProductLineSplitted[DB_FILE_LINE_INDEX_FIELD_NAME].replaceAll("\"", ""));
            if (temp == null)
                continue;
            splittedProductInfo = temp.split(";");

            //change name from the new price list
            in.setImage(splittedProductInfo[DB_FILE_LINE_INDEX_FIELD_IMAGE].replaceAll("_0.jpg", ".jpg"));
            in.setSku(splittedProductInfo[DB_FILE_LINE_INDEX_FIELD_SKU]);
            in.setPRICE_CURRENCY(splittedProductInfo[DB_FILE_LINE_INDEX_FIELD_CURRENCY]);

            ImporterService.sbExportOne.append(in.toString());
        }

//delete
        setAsDeletedObsoleteProducts(deleteProducts);


// fill new products
        csvData = newProducts;

        ArrayList<Furniture> furnitureItems = new ArrayList<>();
        String cathegoryMainFromVendorSite = "";
        String subCathegoryFromVendorSite = "";
        String imageURL = "";
        String material = "";
        String size = "";

        for (String data : csvData) {
            if (data == null)
                continue;
            splittedData = data.split(";", 0);
            // to avoid crawling restrictions
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String productCode = getProductCodeWithoutRedundantWords(splittedData[NEW_PRODUCT_FILE_INDEX_FIELD_SKU]);
            Document preview = ImporterService.getHTMLDocument(SEARCH_URL + productCode.trim().replaceAll(" ", "+"));
            if (preview == null) {
                System.err.println("preview is null >>>>" + SEARCH_URL + productCode);
                continue;
            }
            Elements elements = preview.select(SELECTOR_FOR_PRODUCT_URL);

            if (elements.size() == 0) {
                System.err.println(">>>> no data for: " + SEARCH_URL + productCode);
                continue;
            }

            for (Element subURL : elements) {
                try {
                    //todo: implement
                    //if (!CategoryProcessingService.checkProductCategoryException(splittedData[1])) //check for the exception in categories, whcih shouldn't be added
                    furnitureItems.add(new Furniture(splittedData[NEW_PRODUCT_FILE_INDEX_FIELD_NAME]
                            , Integer.parseInt(splittedData[NEW_PRODUCT_FILE_INDEX_FIELD_PRICE].replaceAll(" ", ""))
                            , getBFTProductURL(subURL.attr("href"))));
                } catch (Exception exc) {
                    System.err.println(">>> productCode: " + productCode + ", exc: " + exc);
                }
            }
        }


        for (Furniture furnitureItem : furnitureItems) {
            ProductImporter pi = new ProductImporter();
            size = "";
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Document product = ImporterService.getHTMLDocument(furnitureItem.getLink());
            if (product == null) {
                System.err.println(">> error: " + furnitureItem.getLink());
                continue;
            }
            imageURL = getBFTProductURL(product.select(SELECTOR_FOR_PRODUCT_IMAGE).attr("href"));
            subCathegoryFromVendorSite = product.select(SELECTOR_FOR_CLASSIFICATION).last().text();
            cathegoryMainFromVendorSite = product.select(SELECTOR_FOR_CLASSIFICATION).last().previousElementSibling().text();

            String imageName = ImporterService.saveImageOnDisk(imageURL);
            if (imageName.indexOf("noimage.jpg") != -1)
                System.err.println(":: image error >> or was no image, or error: " + furnitureItem.getLink());
            //todo: implement
            // Elements description = product.select(DESCRIPTION_SELECTOR);


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
            priznak = getGeneratedPriznak(in.getName());
            pi.setPriznak(priznak);
            pi.setBrand(BRAND);

            CategoryProcessingService.InnerClass siteCategoryInfo = CategoryProcessingService.getSiteCategoryNames(pi.getBrand(), cathegoryMainFromVendorSite, subCathegoryFromVendorSite, in.getName());

            pi.setCategory(siteCategoryInfo.categoryName);
            pi.setPod_category(siteCategoryInfo.subcategoryName);
            pi.setStatus(CategoryProcessingService.isProductTypeVorbidden(pi.getBrand(), cathegoryMainFromVendorSite, subCathegoryFromVendorSite, in.getName()) ? "0" : "1");
            ImporterService.sbExportOne.append(in.toString());
            ImporterService.sbExportTwo.append(pi.toString());

        }
    }


    private ArrayList<String> fillNewData() {
        ArrayList<String> newProductPricesData = new ArrayList();

        //fill new data
        try {
            newProductPricesData = getAllDataFromCSVFile(NEW_PRODUCTS_PRICES_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] splittedData;

        for (String item : newProductPricesData) {
            splittedData = item.split(";", 0);
            if (splittedData != null && splittedData.length > 0)
                newRelationSKUAllInfoHM.put(splittedData[NEW_PRODUCT_FILE_INDEX_FIELD_SKU], item); // HashMap <code: info>
        }
        return newProductPricesData;
    }
}
