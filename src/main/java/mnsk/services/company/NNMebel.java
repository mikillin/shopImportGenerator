package mnsk.services.company;


import mnsk.App;
import mnsk.beans.Furniture;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.CategoryProcessingService;
import mnsk.services.ImporterService;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

//todo: !!!! нужен только 1 файл с корпусной мебелью остальные просто столешницы и т.д.
//todo: check indeces of fields
//TODO::: CHECK dot, komma, ; as separate sign in all files
//Todo: check the first line, mostly it is halved on 2 parts


//new csv file format: Артикул;Номенклатура;Розница
//old csv file format: Номенклатура.Артикул ;Ценовая группа/ Номенклатура/ Характеристика номенклатуры;Опт с НДС;Розница;;
//db file format: "sku";"name";"price";"price_currency";"brand";"category";"pod_category";"material";"type";"image";"status";"budget";"ustanovka";"dostavka";"gabarity";"priznak"

/**
 * Author: S.Rogachevsky
 * Date: 11.02.17
 * Time: 11:59
 */
public class NNMebel extends BFTCommon {
    //TODO: fill the files
//!!!!
    //TODO::: CHECK dot, komma, ; as separate sign in all files

    public static final String BRAND = "NN-Мебель";
    public static final int EXISTING_PRODUCT_FILE_INDEX_FIELD_SKU = 1; //todo: !!! always make test of the indeces below
    public static final int EXISTING_PRODUCT_FILE_INDEX_FIELD_NAME = 2; //todo:check please
    public static final int EXISTING_PRODUCT_LINE_INDEX_FIELD_PRICE = 7;
    public static final int NEW_PRODUCT_FILE_INDEX_FIELD_SKU = 1; // todo: check please
    public static final int NEW_PRODUCT_FILE_INDEX_FIELD_NAME = 2; // todo: check please
    public static final int NEW_PRODUCT_FILE_INDEX_FIELD_PRICE = 7;
    public static final int NEW_PRODUCT_LINE_INDEX_FIELD_PRICE = 7;
    public static final int DB_LINE_INDEX_FIELD_SKU = 0;
    public static final int DB_FILE_LINE_INDEX_FIELD_SKU = 0;
    public static final int DB_FILE_LINE_INDEX_FIELD_NAME = 1;
    public static final int DB_FILE_LINE_INDEX_FIELD_CURRENCY = 3;
    public static final int DB_FILE_LINE_INDEX_FIELD_BRAND = 4;
    public static final int DB_FILE_LINE_INDEX_FIELD_IMAGE = 10;
    public static final int FIRST_RESULT_INDEX = 0;


    //todo: !!!!нужен только 1 файл с корпусной мебелью остальные просто столешницы и т.д.


    //    public static String EXISTING_PRODUCTS_PRICES_FILE = "C:\\work\\shop\\test old.csv";
//    public static String NEW_PRODUCTS_PRICES_FILE = "C:\\work\\shop\\test new.csv";
//    public static String EXISTING_PRODUCTS_PRICES_FILE = "C:\\work\\shop\\SV-МЕБЕЛЬ с 24.05.2022 - Корпус К list1 .csv";
    public static String EXISTING_PRODUCTS_PRICES_FILE = "C:\\work\\shop\\NNM-old.csv";
    public static String NEW_PRODUCTS_PRICES_FILE = "C:\\work\\shop\\NNM-16.08.2022.csv";
// public static String EXISTING_PRODUCTS_PRICES_FILE = "C:\\work\\shop\\SV-20.04-rozn list 2.csv";
//    public static String NEW_PRODUCTS_PRICES_FILE = "C:\\work\\shop\\SV-МЕБЕЛЬ с 24.05.2022 - Кухни list2.csv";


    //    public static final String SELECTOR_FOR_PRODUCT_IMAGE = "div .bxr-element-image a";
    public static final String SELECTOR_FOR_PRODUCT_IMAGE = "#main-photo";
    public static final String SELECTOR_FOR_CLASSIFICATION = ".bx-breadcrumb-item";
    public static final String SELECTOR_FOR_PRODUCT_URL = "div.bxr-element-image>a";
    public static final String DESCRIPTION_SELECTOR = "td.bxr-props-name";

    public static final String[] REDUNDANT_PRODUCT_CODE_WORDS = {};


    ArrayList<String> existingProductData = new ArrayList<>();
    ArrayList<String> newProducts = new ArrayList<>();
    ArrayList<String> deleteProducts = new ArrayList<>();

    static HashMap<String, String> dbData = new HashMap<>(); //main base with old existing data
    static HashMap<String, String> oldRelationNameSKUHM = new HashMap<>();
    static HashMap<String, String> oldRelationSKUAllInfoHM = new HashMap<>();
    static HashMap<String, String> newRelationSKUAllInfoHM = new HashMap<>();


    ArrayList<String> existingProductsWithNewPrice = new ArrayList<>();
    ArrayList<String> existingProductsWithOldPrice = new ArrayList<>();

    @Override
    public void getData() {

        fillDBDataHSForExactBrand();

        ImporterService.initializeFilesHeaders();

        //TODO: read files
        try {
            existingProductData = getAllDataFromCSVFile(EXISTING_PRODUCTS_PRICES_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<String> newProductData = new ArrayList<>();
        try {
            newProductData = getAllDataFromCSVFile(NEW_PRODUCTS_PRICES_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // fill for speedy working
        fillOldAndNewProductsHMs();
        //TODO: compare old-new data
        //format should be:
        //HEADER
        //lines of data


        // from DB - old namd : data
        // check new all
        // check new all - old all by ID
        // ID exists -- then another or the same price
        // ID doesn't exist -- new product and price

        String[] newProductLineParsed;
        boolean existingProduct;


//        error
//        newProductData.clear();
//        newProductData.add("Шкафы-купе;Шкаф-купе К №24 (2.0 м) (Фасад Тип 1) (ЛЕВАЯ) Дуб Сильвер;00-00101345;25.04;1;0.089;484");
//        existingProductData.clear();
//        existingProductData.add("Шкафы-купе;00-00101345;Шкаф-купе К №24 (2.0 м) (Фасад Тип 1) (ЛЕВАЯ) Дуб Сильвер;25.04;1;0.089;436");


        for (String newProductDataLine : newProductData) {
            newProductLineParsed = newProductDataLine.split(";", 0);
            if (isLineNotContainsSKUOnTheFirstPosition(newProductLineParsed))
                continue;

            //check all new
            // check all old
            // if sku new == sku olds
            // check price
            // changed - old change price
            // the same - nothing
            // did't find  then it is a new item
            String[] existingProductLineParsed;
            existingProduct = false;
            for (String existingProductDataLine : existingProductData) {
                existingProductLineParsed = existingProductDataLine.split(";", 0);
                if (isLineNotContainsSKUOnTheFirstPosition(existingProductLineParsed))
                    continue;

                //TODO: check place of fields. they may be different
                //check for the article number
                if (existingProductLineParsed[EXISTING_PRODUCT_FILE_INDEX_FIELD_SKU].equals(newProductLineParsed[NEW_PRODUCT_FILE_INDEX_FIELD_SKU])) {
                    //TODO: !!!check place of fields. they may be different
                    existingProduct = true;
                    if (existingProductLineParsed.length < EXISTING_PRODUCT_LINE_INDEX_FIELD_PRICE + 1) { // depends on the document format
                        System.err.println("achtung!! parsed length < 3 :: " + existingProductDataLine);
                        continue;
                    }

                    //check price
                    if (!existingProductLineParsed[EXISTING_PRODUCT_LINE_INDEX_FIELD_PRICE].equals(newProductLineParsed[NEW_PRODUCT_LINE_INDEX_FIELD_PRICE])) {
                        existingProductsWithNewPrice.add(existingProductDataLine);
                        //existing that to find in db hashmap the name, take all info and set the name from  new price list
                    } else {
                        existingProductsWithOldPrice.add(existingProductDataLine);
                        //existing that to find in db hashmap the name, take all info and set the name from  new price list
                    }
                    continue;

                }
            }
            if (!existingProduct) //if not in the list of the existing products, than it should be added as new one
                newProducts.add(newProductDataLine);
        }

        String[] splittedData;
        String[] splittedDataNew;
        String sku = "";
        boolean isExists;
        String productBrand = "";


        //check for products which should be deleted
        // just go through new and from db. if no in db
        ArrayList<String> dataFromDB = new ArrayList<>();

        try {
            dataFromDB = getAllDataFromCSVFile(FILE_CURRENT_PRODUCTS_DB, BRAND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (String dataFromDBLine : dataFromDB) {

            splittedData = dataFromDBLine.split(";", 0);
            isExists = false;
            for (String newProductDataLine : newProductData) {
                splittedDataNew = newProductDataLine.split(";", 0);
                if (splittedDataNew.length == 0)
                    continue;
                if (String.valueOf(splittedData[DB_FILE_LINE_INDEX_FIELD_NAME]).equals(splittedDataNew[2])) {
                    isExists = true;
                }
            }

            for (String newProductDataLine : existingProductsWithNewPrice) {
                splittedDataNew = newProductDataLine.split(";", 0);
                if (splittedDataNew.length == 0)
                    continue;
                if (String.valueOf(splittedData[DB_FILE_LINE_INDEX_FIELD_NAME]).equals(splittedDataNew[1])) {
                    isExists = true;
                }
            }

            for (String newProductDataLine : existingProductsWithOldPrice) {
                splittedDataNew = newProductDataLine.split(";", 0);
                if (splittedDataNew.length == 0)
                    continue;
                if (String.valueOf(splittedData[DB_FILE_LINE_INDEX_FIELD_NAME]).equals(splittedDataNew[1])) {
                    isExists = true;
                }
            }

            if (!isExists) {
                deleteProducts.add(dataFromDBLine);
            }
        }

        fillData(existingProductsWithNewPrice, newProducts, deleteProducts);

        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);
    }

    private static void fillData(ArrayList<String> existingProductsNewPrice, ArrayList<String> newProducts, ArrayList<String> deleteProducts) {
        ArrayList<String> csvData;
        ImportNode in = new ImportNode();
        String[] splittedData;
        String[] infoSplitted;

        String[] splittedProductInfo;
        //fill changed price
        String infoFromDB = "";
        String newInfo = "";
        String oldInfo = "";
        //existing products
        for (String item : existingProductsNewPrice) {
            splittedData = item.split(";", 0); //old existing line from old file
            //todo: check
            //get the name and with name get ID, then with ID we're going to the new products and set  name and price
//            infoFromDB = dbData.get("\""+splittedData[EXISTING_PRODUCT_FILE_INDEX_FIELD_NAME]+"\"");
            infoFromDB = dbData.get(formatData(splittedData[EXISTING_PRODUCT_FILE_INDEX_FIELD_NAME]));
            if (infoFromDB == null) { //it is not in the old DB. possibly were deleted from the DB, although they were in the price
                System.err.println("old value should be in DB. However, no key in dbDataFor4Brands:" + item);
                //it is new though products Артикул;Номенклатура*new*;Розница
                newInfo = newRelationSKUAllInfoHM.get(splittedData[EXISTING_PRODUCT_FILE_INDEX_FIELD_SKU]);
                if (!newProducts.contains(newInfo))
                    newProducts.add(newInfo);
                continue;
            }

            infoSplitted = infoFromDB.split(";", 0);
            in.setSku(infoSplitted[DB_FILE_LINE_INDEX_FIELD_SKU]);
            in.setImage(infoSplitted[DB_FILE_LINE_INDEX_FIELD_IMAGE]);
            in.setPRICE_CURRENCY(infoSplitted[DB_FILE_LINE_INDEX_FIELD_CURRENCY]);


//            String temp = newRelationSKUAllInfoHM.get(oldRelationNameSKUHM.get(infoSplitted[DB_FILE_LINE_INDEX_FIELD_NAME]));
            String temp = newRelationSKUAllInfoHM.get(oldRelationNameSKUHM.get(formatData(infoSplitted[DB_FILE_LINE_INDEX_FIELD_NAME])));
            if (temp == null)
                continue;
            splittedProductInfo = temp.split(";");

            in.setName(splittedProductInfo[NEW_PRODUCT_FILE_INDEX_FIELD_NAME]); //change name from the new price list
            in.setPrice(splittedProductInfo[NEW_PRODUCT_FILE_INDEX_FIELD_PRICE].replaceAll(" ", "") + "0000");    //changed cost

            ImporterService.sbExportOne.append(in.toString());
        }


        setAsDeletedObsoleteProducts(deleteProducts);


// fill new products
        csvData = newProducts;

        ArrayList<Furniture> furnitureItems = new ArrayList<>();
        String cathegoryMainFromVendorSite = "";
        String subCathegoryFromVendorSite = "";
        String imageURL = "";
        String material = "";
        String size = "";

        for (String data : newProducts) {
            if (data == null)
                continue;
            splittedData = data.split(";", 0);
            // to avoid crawling restrictions
            try {
                Thread.sleep(1000);
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
                System.err.println(">>>> no data on the bft site for the product: " + SEARCH_URL + productCode);
                continue;
            }
// there is no need to load all selected similar product. it is required only the first one
//            for (Element subURL : elements) {
            try {
                //todo: implement
                //if (!CategoryProcessingService.checkProductCategoryException(splittedData[1])) //check for the exception in categories, whcih shouldn't be added
                furnitureItems.add(new Furniture(splittedData[NEW_PRODUCT_FILE_INDEX_FIELD_NAME]
                        , Integer.parseInt(splittedData[NEW_PRODUCT_FILE_INDEX_FIELD_PRICE].replaceAll(" ", ""))
                        , getBFTProductURL(elements.get(FIRST_RESULT_INDEX).attr("href"))));
            } catch (Exception exc) {
                System.err.println(">>> productCode: " + productCode + ", exc: " + exc);
            }
//            }
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

            String imageName = ImporterService.saveImageOnDisk(imageURL, BRAND);

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
            pi.setStatus(CategoryProcessingService.isProductTypeVorbidden(pi.getBrand(), cathegoryMainFromVendorSite, subCathegoryFromVendorSite) ? "0" : "1");
            ImporterService.sbExportOne.append(in);
            ImporterService.sbExportTwo.append(pi);

        }
    }


    /**
     * will fill HashMaps for Old and New Products
     * format: id : all information
     */
    private void fillOldAndNewProductsHMs() {
        ArrayList<String> newProductPricesData = fillNewData();

        //fill old data
        fillOldExistingProductsHM(newProductPricesData);
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

    private void fillOldExistingProductsHM(ArrayList<String> newProductPricesData) {
        String[] splittedData;
        //fill old data
        ArrayList<String> existingProductPricesData = new ArrayList<>();
        try {
            existingProductPricesData = getAllDataFromCSVFile(EXISTING_PRODUCTS_PRICES_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (String item : existingProductPricesData) {
            splittedData = item.split(";", 0);
            if (splittedData != null && splittedData.length > 1)
                oldRelationNameSKUHM.put(splittedData[EXISTING_PRODUCT_FILE_INDEX_FIELD_NAME]
                        , splittedData[EXISTING_PRODUCT_FILE_INDEX_FIELD_SKU]); // HashMap <oldname: code info>
            // oldRelationSKUAllInfoHM.put(splittedData[EXISTING_PRODUCT_FILE_INDEX_FIELD_SKU], item);
        }
    }


    private void fillDBDataHSForExactBrand() {
        ArrayList<String> existingProductDataFromDB = new ArrayList();
        try {
            existingProductDataFromDB = getAllDataFromCSVFile(FILE_CURRENT_PRODUCTS_DB, BRAND);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] splittedData;
        String brand = "";
        for (String item : existingProductDataFromDB) {
            splittedData = item.split(";", 0);
            dbData.put(splittedData[DB_FILE_LINE_INDEX_FIELD_NAME].trim(), item); // HashMap <name: all info>
//                dbData.put(splittedData[DB_FILE_LINE_INDEX_FIELD_NAME].trim(), item); // HashMap <name: all info>
        }

    }
}
