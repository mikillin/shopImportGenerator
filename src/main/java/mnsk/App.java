package mnsk;

import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.CategoryProcessingService;
import mnsk.services.company.*;
import mnsk.services.ImporterService;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * before using this app download prices from DB
 */

//todo: upload data from source file.
// search and compare with database

public class App {
    public static final String SAVED_FILES_EXTENSION = ".jpg";

    public final static String FILE_EXPORT_FIRST = "c:\\work\\shop\\1.csv";
    public final static String FILE_EXPORT_SECOND = "c:\\work\\shop\\2.csv";

    public final static String CSV_SEPARATOR = ";";
    public final static String CSV_END_LINE = System.lineSeparator();
    public final static String IMAGE_FILE_PATH = "public://";
    public static int BEGIN_ARTICLE_NUMBER = 12504; // sv мебель самый точный расчет

    //todo: сделать удаление если нет в базе: проход по базе если нет в новом прайсе - удалить.
    //12194 -  12503 Сокол Мебель 06.10
    //11626 - 12193 MD 12.09
    //11562 - 11625 NN Мебель 18.08
    //11559 - 11561 СВ Мебель 18.08
    //11392 - 11558 СВ Мебель 26.07
    // 11370- 1391 - Sokol
    // 11306- 1369 - MD
    // 11200 -11305 SV-Мебель
    // 10650 -11195  MD
    // 10564 - 10612 Собственное производство
    // Sheffilton 9700 - 10075,
    // Signal 9600 - 9700
    // Монтанья 9500 - 9600
    // soldatov 9300 - 9500
    // sheffileton 6700 - 9200
    // sokol 6300 - 6700,
    // sv-мебель 5300-6300,


    public static String url = "jdbc:mysql://localhost:3306/shop";
    public static String username = "root";
    public static String password = "admin";
    // static String sourceFile = "c:/ready/XXX-2.csv";
    static String sourceFile = "c:\\work\\shop\\ready\\xxx-3.csv ";
    static String sourceFileCodes = "c:\\work\\shop\\ready\\codes.csv";
    static String sourceDataFromDB = "c:\\work\\shop\\furniture.csv";
    static String sourceFileBFTCSV = "c:\\work\\shop\\bft.csv";
    static String[] sourceFilesBFTCSV = {"c:\\work\\shop\\Signal-26.05-rozn.csv"
            // , "c:\\work\\shop\\Halmar-24.02.csv"
    };

    public static String[] specialMarketingProperty = {"45", "46", "47"};
    //static String sourceFileBFTCSV = "c:/ready/testbft.csv ";
    static int skuBegin = 0;

    public static void main(String[] args) {
        System.out.println("Start:" + new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(new Date()));
        ///// TODO: CHECK THE STRUCTURE OF THE DB FILE- IT IS NEW- OPISANIE IS ADDED
        //  System.out.print(CategoryProcessingService.isProductWithNameVorbidden("Основание для стола"));
//        System.out.println("");

//        System.out.printf("-->"  + CategoryProcessingService.CLASSIFICATION_RULES.get("Signal"));
//            SokolMEBELItems();
//       SheffiltonItems();
//        SoldatovItems();
//        MontanjaItems();
//                   ReconfigureItems();
//           svMEBELItems();
//           NNMEBELItems();
//        allSignalHalmarItems();
//        BFT2FilesItems();

//        MebelDeloService.getProductData();


        domByYMLService();


        //importMainData();
        //updateLastSKU(); //obligatory
        //  importMainData("common");
        //   updateSpecialIDs("common");
        //     updateBFTPrices("common");
        //exportDataTOFileSignalHalmar();
        // - createNewListOFProductsBFT();
//        System.out.print(">>" + CategoryProcessingService.getSiteCategoryNames("Мебель для гостиной / Набор мебели для гостиной Гостиная К №7 (Зеркало)"));


//        String name = "Каркас стула Sheffilton SHT-S93";
//        CategoryProcessingService.InnerClass siteCategoryInfo =
//        CategoryProcessingService.getSiteCategoryNames(name);
//        System.out.println("category: " + siteCategoryInfo.categoryName);
//        System.out.println("subcategory: " + siteCategoryInfo.subcategoryName);
//        if (CategoryProcessingService.isProductTypeVorbidden("MD", siteCategoryInfo.categoryName, siteCategoryInfo.subcategoryName, "Кухня КГ-1 1.8 м (белый/бетон)"))
//        System.out.println("Запрещен: " + CategoryProcessingService.isProductTypeVorbidden(name));


        //System.out.print(">>" + CategoryProcessingService.isProductTypeVorbidden("Кухня КГ-1 1.8 м (белый/бетон)"));
        System.out.println("Finish:" + new SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(new Date()));
    }

    static void updateLastSKU() {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            ResultSet rset;


            rset = stmt.executeQuery("SELECT CAST(sku AS UNSIGNED) AS skunum FROM FURNITURE  order by skunum  desc");
            if (rset.next())
                App.BEGIN_ARTICLE_NUMBER = Integer.parseInt(rset.getString("skunum")) + 1;
            else
                System.out.println(">>> no last sku!");


        } catch (SQLException exc) {
            System.out.println(exc);

        }

    }

    static void createNewListOFProductsBFT() {
        try {
            List<String> codes = BftDBService.getSpecialIDFromBFTCSVPriceFile(sourceFileBFTCSV);
            ArrayList<String> newCodes = new ArrayList<String>();
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            ResultSet rset;

            for (String code : codes) {
                rset = stmt.executeQuery("SELECT * FROM FURNITURE WHERE specialID = '" + code + "' and status = 1");
                if (!rset.next())
                    newCodes.add(code);

                rset = stmt.executeQuery("SELECT * FROM FURNITURE WHERE specialID = '" + code + "' and status = 0");
                if (rset.next())
                    System.out.println("Err: turned off not added !!! " + code);
            }

            BftDBService.getDataForSpecialID(newCodes);
            connection.close();

        } catch (IOException exc) {
            System.out.println("Err: " + exc);
        } catch (
                SQLException exc) {
            System.out.println("Err: " + exc);
        }

        System.out.println("Hello!");


    }


    static void domByYMLService() {
        ImporterService is = new DomBYYMLService();
        is.getData();
    }

    static void MontanjaItems() {
        ImporterService is = new MebelMinskMontanjaService();
        is.getData();
    }

    static void ReconfigureItems() {
        ImporterService is = new ReconfigureDBService();
        is.getData();
    }

    static void SoldatovItems() {
        ImporterService is = new MebelMinskSoldatovService();
        is.getData();
    }

    static void SokolMEBELItems() {

        ImporterService is = new Sokol();
        is.getData();
    }

    static void SheffiltonItems() {
        ImporterService is = new Sheffilton();
        is.getData();
    }

    static void svMEBELItems() {
        ImporterService is = new SVMebel();
        is.getData();
    }

    static void NNMEBELItems() {
        ImporterService is = new NNMebel();
        is.getData();
    }

    static void allSignalHalmarItems() {

        ImporterService is = new BftService();
        is.getData();
    }


    static void newSignalHalmarItems() {
        String test = "", query = "";

        try {
            Scanner scanner = new Scanner(new File(sourceFileBFTCSV));
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            ResultSet set;
            String[] data;
            scanner.nextLine(); // pass 1 top column description row
            ImportNode in;
            ProductImporter pi;

            ImporterService is = new ImporterService() {
                @Override
                public void getData() {

                }
            };

            is.initializeFilesHeaders();


            while (scanner.hasNextLine()) {
                data = test.split(";", -1);
//update old price
                try {
                    query =
                            "SELECT * FROM FURNITURE " +
                                    "specialID =" +
                                    getValue(data[2]);
                    ResultSet rset = stmt.executeQuery(query);
                    if (!rset.next()) {
                        //read the last sku from table
//add to file data
                        // set to last rounded by 100 pi.setSKU(data);
                     /*   pi.setSpecialID(data[2]);
                        pi.setPrice(data[7]);

                        load to StringBuffer
*/

                    }
                } catch (ArrayIndexOutOfBoundsException exc) {
                    System.out.println(exc);
                }
                //     System.out.println(query);
                stmt.executeUpdate(query);
            }
        } catch (IOException e2xc) {
            System.out.println(e2xc);
        } catch (SQLException exc) {
            System.out.println(exc);

        }


    }

    static void exportDataTOFileSignalHalmar() {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt
                    .executeQuery("select * from FURNITURE.furniture " +
                            "where vendor in ('SIGNAL', 'HALMAR') and status = 1");
            ImportNode in;
            ProductImporter pi;

            ImporterService is = new ImporterService() {
                @Override
                public void getData() {

                }
            };

            is.initializeFilesHeaders();

            while (rs.next()) {
                in = new ImportNode();
                pi = new ProductImporter();


                pi.setSKU(rs.getString("sku"));
                pi.setName(rs.getString("name"));
                pi.setCategory(rs.getString("category"));
                pi.setPod_category(rs.getString("subcategory"));
                pi.setGabarity(rs.getString("size"));
                pi.setBrand(rs.getString("vendor"));
                pi.setMaterial(rs.getString("material"));
                pi.setPriznak(rs.getString("feature"));
                pi.setUstanovka(rs.getString("installation"));
                pi.setDostavka(rs.getString("delivery"));
                pi.setType(rs.getString("type"));
                pi.setOpisanie(rs.getString("description"));

                //TODO: heey
                pi.setStatus((rs.getString("specialID").equals("0") ? "0" : "1")); // logic for now only
                is.sbExportTwo.append(pi.toString());

                in.setSku(rs.getString("sku"));
                in.setName(rs.getString("name"));
                in.setImage(rs.getString("image"));
                in.setPrice(rs.getString("cost"));
                in.setPRICE_CURRENCY(rs.getString("currency"));
                is.sbExportOne.append(in.toString());
            }

            is.saveTOFile(is.sbExportOne, "1.csv");
            is.saveTOFile(is.sbExportTwo, "2.csv");

            connection.close();
        } catch (SQLException exc) {
            System.out.println("sql exc:" + exc);
        }
    }

    static void importMainData() {
        String test = "", query = "";

        try {
            Scanner scanner = new Scanner(new File(sourceDataFromDB));
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();

            //clear all data
            query = "TRUNCATE TABLE FURNITURE";
            stmt.executeUpdate(query);

            String[] data;
            scanner.nextLine(); // pass 1 top column description row
            while (scanner.hasNextLine()) {
                test = scanner.nextLine();
//                while (test.indexOf("public://") == -1 && scanner.hasNextLine()) {
//                    test += scanner.nextLine();//why doesn't it work
//                }
                data = test.split(";", -1);

                try {

                    query =
                            "INSERT INTO FURNITURE " +
                                    "(" +
                                    "sku, " +
                                    "name," +
                                    "cost, " +
                                    "currency, " +
                                    "vendor, " +
                                    "category, " +
                                    "subcategory," +
                                    "material, " +
                                    "type, " +
                                    //"description, " +
                                    "image, " +
                                    "status, " +
                                    "budget,  " +
                                    "installation, " +
                                    "delivery, " +
                                    "size, " +
                                    "feature" +
                                    " ) VALUES (" +
                                    data[0] + ", " + //sku
                                    getValue(data[1]) + ", " +
                                    getValue(data[2]) + ", " +
                                    getValue(data[3]) + ", " +
                                    getValue(data[4]) + ", " +
                                    getValue(data[5]) + ", " +
                                    getValue(data[6]) + ", " + //subcat
                                    getValue(data[7]) + ", " +
                                    getValue(data[8]) + ", " +
                                    //   getValue(data[9]) + ", " + //description
                                    getValue(data[9]).replaceAll("_7.jpg", ".jpg")
                                            .replaceAll("_6.jpg", ".jpg")
                                            .replaceAll("_5.jpg", ".jpg")
                                            .replaceAll("_4.jpg", ".jpg")
                                            .replaceAll("_3.jpg", ".jpg")
                                            .replaceAll("_2.jpg", ".jpg")
                                            .replaceAll("_1.jpg", ".jpg")
                                            .replaceAll("_0.jpg", ".jpg") + ", " + /// image
                                    getValue(data[10]) + ", " +
                                    (data[11] == null || data[11].equals("") ? "0" : "1") + ", " + //budget
                                    getValue(data[12]) + ", " +
                                    getValue(data[13]) + ", " +
                                    getValue(data[14]) + ", " +
                                    getValue(data[15]) + ")";
                    //System.out.println("output query: " + query);

                } catch (ArrayIndexOutOfBoundsException exc) {
                    System.out.println(exc);
                }
                //     System.out.println(query);
                stmt.executeUpdate(query);
            }
        } catch (IOException e2xc) {
            System.out.println(e2xc);
        } catch (SQLException exc) {
            System.out.println(exc);

        }
    }


    static void updateSpecialIDs(String siteAbbreviation) {
        String test = "", query = "";
        try {
            Scanner scanner = new Scanner(new File(sourceDataFromDB));
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            String[] data;

            scanner.nextLine(); //skip top row
            while (scanner.hasNextLine()) {
                test = scanner.nextLine();
                data = test.split(";", -1);
                if (!data[3].equals("")) { // check for this. 4 reallly?
                    query =
                            "UPDATE FURNITURE " +
                                    "set specialID = " +
                                    getValue(data[3]) +
                                    " WHERE sku = " +
                                    data[0];
                    //System.out.println(query);
                    int result = stmt.executeUpdate(query);
                } else {
                    System.out.println("no data for: " + test);

                }

            }
            stmt.close();
            connection.close();
        } catch (IOException exc) {
            System.out.println(test);
            System.err.println(
                    "Somethings was wrong: " + exc);
        } catch (SQLException exc) {
            System.out.println(test);
            System.err.println("Somethings was wrong: " + exc);
        }

    }


    static void updateBFTPrices(String siteAbbreviation) {
        String test = "", query = "";
        try {
            Scanner scanner = new Scanner(new File(sourceFileBFTCSV));
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stmt = connection.createStatement();
            String[] data;

            scanner.nextLine(); //skip top row
            //add check for new ones. create a list of them.
            while (scanner.hasNextLine()) {
                test = scanner.nextLine();
                data = test.split(";", -1);
                int price = (data[9].equals("") || data[9] == null) ? 0 : (int) (Float.parseFloat(data[9].replaceAll(",", "\\.")) * 10000);
                query =
                        "UPDATE FURNITURE " +
                                "set cost = " +
                                price +
                                " WHERE specialID = '" +
                                data[2] + "'";
                stmt.executeUpdate(query);
            }
            stmt.close();
            connection.close();
        } catch (IOException exc) {
            System.out.println(test);
            System.err.println(
                    "Somethings was wrong: " + exc);
        } catch (SQLException exc) {
            System.out.println(test);
            System.err.println("Somethings was wrong: " + exc);
        }

    }


    /**
     * generate proper output
     *
     * @param source
     * @return
     */
    static String getValue(String source) {

        return source == null || source.equals("") || source.equals("NULL") ? "null" : source.indexOf("\"") != -1 ? source : "\"" + source + "\"";
    }

}
