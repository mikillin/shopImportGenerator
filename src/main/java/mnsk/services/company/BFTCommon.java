package mnsk.services.company;

import mnsk.beans.export.ProductImporter;
import mnsk.services.ImporterService;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class BFTCommon extends ImporterService {
    public final static String SEARCH_URL = "https://bft.by/catalog/?q=";
    public static final String BASE_BFT_URL = "http://bft.by";
    public final static String FILE_CURRENT_PRODUCTS_DB = "C:\\work\\shop\\db.csv"; //todo: check always
    public static final String[] REDUNDANT_PRODUCT_CODE_WORDS = {};
    public static ArrayList<String> NEW_PRODUCTS_PRICES_FILES_LIST =
            new ArrayList<>(Arrays.asList(
                    "C:\\work\\shop\\Sokol-25.05-rozn.csv"

            ));


    //TODO: fill the files
    public static ArrayList<String> EXISTING_PRODUCTS_PRICES_FILES_LIST =
            new ArrayList<>(Arrays.asList(
                    "C:\\work\\shop\\sokol-14.04-ROZN.csv"
            ));


    public static String getBFTProductURL(String subURL) {
        if (subURL.indexOf("noimage") == -1)
            return BASE_BFT_URL + subURL;
        else
            return subURL;
    }


    public static void setAsDeletedObsoleteProducts(ArrayList<String> deleteProducts) {

        for (String lineToHide : deleteProducts) {

            ProductImporter pi = new ProductImporter();
            pi.setSKU(String.valueOf(lineToHide.split(";")[0]));
            pi.setStatus("0");

            ImporterService.sbExportTwo.append(pi);
        }
    }

    public static boolean isLineNotContainsSKUOnTheFirstPosition(String[] arrayOfLineSplitData) {
        return arrayOfLineSplitData.length == 0 || arrayOfLineSplitData[0] == null || arrayOfLineSplitData[0].length() == 1 || "".equals(arrayOfLineSplitData[0]) || StringUtils.isEmpty(arrayOfLineSplitData[0]);
    }


    @Override
    public void getData() {

    }

    public static String getExistingProductPricesFileName(String brand) {
        String existingProductPricesFileName = "";
        for (String existingProductListFilename : EXISTING_PRODUCTS_PRICES_FILES_LIST) {
            if (existingProductListFilename.toLowerCase().indexOf(brand.toLowerCase()) != -1) {
                existingProductPricesFileName = existingProductListFilename;
                break;
            }

        }

        return existingProductPricesFileName;
    }

    public static String getNewProductPricesFileName(String brand) {
        String newProductPricesFileName = "";
        for (String newProductListFilename : NEW_PRODUCTS_PRICES_FILES_LIST) {
            if (newProductListFilename.toLowerCase().indexOf(brand.toLowerCase()) != -1) {
                newProductPricesFileName = newProductListFilename;
                break;
            }

        }

        return newProductPricesFileName;
    }


    public static String getGeneratedPriznak(String name) {
        String priznak = "";
        Random rand = new Random();
        int n = rand.nextInt(500);
        if (name.toUpperCase().contains("NEW") || n == 1) {
            priznak = "47"; //new product
        }
        n = rand.nextInt(100);
        if (n == 1)
            priznak = "46"; //best
        n = rand.nextInt(50);
        if (n == 1)
            priznak = "45"; // lower price
        return priznak;
    }


    public static String getProductCodeWithoutRedundantWords(String sourceProduceCode) {

        for (String word : REDUNDANT_PRODUCT_CODE_WORDS) {
            sourceProduceCode = sourceProduceCode.replaceAll(word, "");
        }
        return sourceProduceCode.trim();

    }

    public static ArrayList<String> getAllDataFromCSVFile(String fileSource) throws FileNotFoundException {

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
}
