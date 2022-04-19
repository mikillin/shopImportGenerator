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
//new csv file format: Артикул;Номенклатура;Розница
//old csv file format: Номенклатура.Артикул ;Ценовая группа/ Номенклатура/ Характеристика номенклатуры;Опт с НДС;Розница;;
//db file format: "sku";"name";"price";"price_currency";"brand";"category";"pod_category";"material";"type";"image";"status";"budget";"ustanovka";"dostavka";"gabarity";"priznak"

/**
 * Author: S.Rogachevsky
 * Date: 11.02.17
 * Time: 11:59
 */
public class LoadAllBftProductsService extends ImporterService {
    //todo: fill the source file
    private final static String FILE_SOURCE = "C:\\work\\shop\\Halmar-24.02.csv"; //todo: make an array
    private final static String FILE_CURRENT_PRODUCTS_DB = "C:\\work\\shop\\db.csv"; //todo: check always

    //TODO: fill the files
    public static final ArrayList<String> NEW_PRODUCTS_PRICES_FILES_LIST = new ArrayList<>(Arrays.asList("C:\\work\\shop\\SIGNAL-12.04.2022-rozn.csv", "C:\\work\\shop\\HALMAR-12.04.2022-rozn.csv"));


    //TODO: fill the files
    public static final ArrayList<String> EXISTING_PRODUCTS_PRICES_FILES_LIST = new ArrayList<>(Arrays.asList("C:\\work\\shop\\Signal-24.02.csv", "C:\\work\\shop\\Halmar-24.02.csv"));

    //TODO: fill the brands
    public static final ArrayList<String> BRANDS = new ArrayList<>(Arrays.asList("Halmar", "Signal"));
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
    public static final String[] REDUNDANT_PRODUCT_CODE_WORDS = {"new", "laquered", "square", "materac", "lampka", "nowość", "szafka", "rectangular", "white, black"};

    ArrayList<String> specificBrandExistingProductData = new ArrayList<>();
    ArrayList<String> specificBrandExistingProductsNewPrice = new ArrayList<>();
    ArrayList<String> specificBrandExistingProductsTheSamePrice = new ArrayList<>();
    ArrayList<String> specificBrandNewProducts = new ArrayList<>();
    ArrayList<String> deleteProducts = new ArrayList<>();
    static HashMap<String, String> dbDataFor4AllDefinedBrands = new HashMap<>();
    static HashMap<String, String> oldProductsHM = new HashMap<>();
    static HashMap<String, String> newProductsHM = new HashMap<>();

    static String existingProductPricesFileName = "";
    static String newProductPricesFileName = "";

    @Override
    public void getData() {

        fillDBDataFor4Brands();

        ImporterService.initializeFilesHeaders();


        //every brand will be processed
        for (String brand : BRANDS) {
            for (String existingProductListFilename : EXISTING_PRODUCTS_PRICES_FILES_LIST) {
                if (existingProductListFilename.toLowerCase().indexOf(brand.toLowerCase()) != -1) {
                    existingProductPricesFileName = existingProductListFilename;
                    break;
                }

            }
            for (String newProductListFilename : NEW_PRODUCTS_PRICES_FILES_LIST) {
                if (newProductListFilename.toLowerCase().indexOf(brand.toLowerCase()) != -1) {
                    newProductPricesFileName = newProductListFilename;
                    break;
                }

            }
            //TODO: read files
            try {
                specificBrandExistingProductData = getAllDataFromCSVFile(existingProductPricesFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ArrayList<String> newProductData = new ArrayList<>();
            try {
                newProductData = getAllDataFromCSVFile(newProductPricesFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // fill for speedy working
            fillOldNewProductsHMs();
            //TODO: compare old-new data
            //format should be:
            //HEADER
            //lines of data
            String[] newProductLineParsed;
            ArrayList<String> resultProductData = new ArrayList<>();
            for (String newProductDataLine : newProductData) {
                newProductLineParsed = newProductDataLine.split(";", 0);
                if (newProductLineParsed.length == 0 || newProductLineParsed[0] == null || newProductLineParsed[0].length() == 1 || "".equals(newProductLineParsed[0]) || StringUtils.isEmpty(newProductLineParsed[0])

                ) continue;

                //chekc all new
                // check all old
                // if sku new == sku old
                // check price
                // changed - old change price
                // the same - nothing
                // did't find  then it is a new item
                String[] existingProductLineParsed;
                boolean existingProduct = false;
                for (String existingProductDataLine : specificBrandExistingProductData) {
                    existingProductLineParsed = existingProductDataLine.split(";", 0);
                    if (existingProductLineParsed.length == 0 || existingProductLineParsed[0] == null || existingProductLineParsed[0].length() == 1 || "".equals(existingProductLineParsed[0]) || StringUtils.isEmpty(existingProductLineParsed[0]))
                        continue;

                    //TODO: check place of fields. they may be different
                    //check for the article number
                    if (existingProductLineParsed[0].equals(newProductLineParsed[0])) {
                        //TODO: !!!check place of fields. they may be different
                        existingProduct = true;
                        if (existingProductLineParsed.length < 4) {
                            System.err.println("achtung!! parsed length < 4 :: " + existingProductDataLine);
                            continue;
                        }

                        //check price
                        if (existingProductLineParsed[3].equals(newProductLineParsed[2])) {
                            specificBrandExistingProductsTheSamePrice.add(existingProductDataLine);
                        } else {
                            specificBrandExistingProductsNewPrice.add(existingProductDataLine);

                        }
                    }
                }
                if (!existingProduct) //if not in the list of the existing products, than it should be added as new one
                    specificBrandNewProducts.add(newProductDataLine);
            }

            String[] splittedData;
            String[] splittedDataNew;
            String sku = "";
            boolean isExists;
            String productBrand = "";


            ArrayList<String> existingProductDataFromDB = new ArrayList<>();

            try {
                existingProductDataFromDB = getAllDataFromCSVFile(FILE_CURRENT_PRODUCTS_DB);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (String existingProductDataLine : existingProductDataFromDB) {

                splittedData = existingProductDataLine.split(";", 0);
                productBrand = String.valueOf(splittedData[4]);
                productBrand = productBrand.substring(1, productBrand.length()); // without signs "
                if (!productBrand.equalsIgnoreCase(brand))
                    continue;
                isExists = false;
                for (String newProductDataLine : newProductData) {
                    splittedDataNew = newProductDataLine.split(";", 0);
                    sku = String.valueOf(splittedDataNew[0]);
                    sku = sku.substring(1, sku.length()); // without signs "
                    if (String.valueOf(splittedData[0]).equals(sku)) {
                        isExists = true;
                    }
                }
                if (!isExists) {
                    deleteProducts.add(existingProductDataLine);
                }

            }
        }

        fillData(specificBrandExistingProductsTheSamePrice, specificBrandExistingProductsNewPrice, specificBrandNewProducts, deleteProducts);


        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        System.out.println("Hello! " + FILE_SOURCE);

    }


    private static void fillData(ArrayList<String> existingProductsTheSamePrice, ArrayList<String> existingProductsNewPrice, ArrayList<String> newProducts, ArrayList<String> deleteProducts) {
        ArrayList<String> csvData = new ArrayList<>();
        ImportNode in = new ImportNode();
        String[] splittedData;
        String[] infoSplitted;

        String code = "";
        String dataX = "";
        String[] splittedProductInfo;
        //fill changed price
        String infoFromDB = "";
        for (String item : existingProductsNewPrice) {
            splittedData = item.split(";", 0);
            //todo: check
            infoFromDB = dbDataFor4AllDefinedBrands.get(splittedData[1]);
            if (infoFromDB == null) { //it is not in the old DB
                System.err.println("no key in dbDataFor4Brands:" + splittedData[1]);
                //it is new though products Артикул;Номенклатура*new*;Розница
                if (!newProducts.contains(newProductsHM.get(splittedData[0])))
                    newProducts.add(newProductsHM.get(splittedData[0]));
                continue;
            }
            infoFromDB = infoFromDB.replaceAll("\"", "");
            infoSplitted = infoFromDB.split(";", 0);
            in.setSku(infoSplitted[0]);
            in.setImage(infoSplitted[9]);
            in.setPRICE_CURRENCY(infoSplitted[3]);

            code = splittedData[0];
            dataX = newProductsHM.get(oldProductsHM.get(infoSplitted[1]));
            splittedProductInfo = dataX.split(";");

            in.setName(splittedProductInfo[1]); //change name from the new price list
            in.setPrice(splittedProductInfo[2].replaceAll(" ", "") + "0000");    //changed cost
            ImporterService.sbExportOne.append(in.toString());

            //todo:maybe need to add info also in the second file
        }


        String[] lineToHideSplitted;
        for (String lineToHide : deleteProducts) {

            //lineToHide.replaceAll(";1;", ";0;");
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
                    //todo: implement
                    //if (!CategoryProcessingService.checkProductCategoryException(splittedData[1])) //check for the exception in categories, whcih shouldn't be added
                    furnitureItems.add(new Furniture(splittedData[1], Integer.parseInt(splittedData[2].replaceAll(" ", "")), getBFTProductURL(subURL.attr("href"))));
                } catch (Exception exc) {
                    System.err.println(">>> productCode: " + productCode);
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

            pi.setCategory(cathegoryMain);
            pi.setPod_category(subCathegory);
            pi.setBrand(furnitureItem.getName().toUpperCase().contains(BRAND_SIGNAL.toUpperCase()) ? BRAND_SIGNAL : (furnitureItem.getName().toUpperCase().contains(BRAND_HALMAR.toUpperCase()) ? BRAND_HALMAR : ""));
            pi.setStatus(CategoryProcessingService.isProductTypeVorbidden(pi.getBrand(), cathegoryMain, subCathegory) ? "0" : "1");
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
        return BASE_BFT_URL + subURL;
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
                dbDataFor4AllDefinedBrands.put(splittedData[1].replaceAll("\"", "").trim(), item); // HashMap <name: all info>
            }
        }

    }
}
