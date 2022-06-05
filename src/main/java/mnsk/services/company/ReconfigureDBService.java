package mnsk.services.company;


import mnsk.App;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.CategoryProcessingService;
import mnsk.services.ImporterService;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static mnsk.services.CategoryProcessingService.isProductTypeVorbidden;

/**
 * Author: S.Rogachevsky
 * Date: 11.02.17
 * Time: 11:59
 */
public class ReconfigureDBService extends ImporterService {
    private final static String FILE_CURRENT_PRODUCTS_DB = "C:\\work\\shop\\db.csv"; //todo: check always


    static ArrayList<String[]> dbData = new ArrayList<>();

    @Override
    public void getData() {

        fillDataFromDB();

        ImporterService.initializeFilesHeaders();

        ProductImporter pi;
        ImportNode in;

        for (String[] items : dbData) {

            try {
//                if (isProductTypeVorbidden(items[1]))
//                    continue;
                pi = new ProductImporter();
                in = new ImportNode();

                in.setSku(items[0]);
                in.setName(items[1]);
                in.setImage(items[10]);
                in.setPrice(items[2]);
                in.setPRICE_CURRENCY(items[3]);


                pi.setOpisanie(items[9]);
                pi.setDostavka("от 2 до 30 дней");
                pi.setUstanovka("Возможна рассрочка");
                pi.setSKU(in.getSku());
                pi.setName(in.getName());
                pi.setMaterial(items[7]);
                pi.setGabarity(items[15]);
                pi.setPriznak(items[16]);
                pi.setBrand(items[4]);

                CategoryProcessingService.InnerClass inner = CategoryProcessingService.getSiteCategoryNames(items[1]);
                pi.setCategory(inner.categoryName);
                pi.setPod_category(inner.subcategoryName);
                pi.setStatus(items[11].equals("0") ? "0"
                        : (CategoryProcessingService.isProductTypeVorbidden(pi.getBrand(), inner.categoryName, inner.subcategoryName, in.getName()) ? "0" : "1"));
                ImporterService.sbExportOne.append(in);
                ImporterService.sbExportTwo.append(pi);

            } catch (Exception exc) {
                System.err.println("For this product line sku was not defined: " + items[0]);
            }
        }

        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        System.out.println("Hello! ");
    }




    private void fillDataFromDB() {
        ArrayList<String> existingProductDataFromDB = new ArrayList();
        try {
            existingProductDataFromDB = getAllDataFromCSVFile(FILE_CURRENT_PRODUCTS_DB);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (String item : existingProductDataFromDB) {
            dbData.add(item.replaceAll("\"", "").split(";", -1)); // HashMap <name: all info>

        }

    }
}
