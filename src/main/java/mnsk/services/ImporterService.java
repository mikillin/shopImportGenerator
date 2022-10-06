package mnsk.services;


import mnsk.App;
import mnsk.beans.export.ProductImporter;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Author: S.Rogachevsky
 * Date: 05.12.16
 * Time: 22:57
 */
public abstract class ImporterService {
    public static final int DB_FILE_LINE_INDEX_FIELD_SKU = 0;
    public static final int DB_FILE_LINE_INDEX_FIELD_NAME = 1;
    public static final int DB_FILE_LINE_INDEX_FIELD_PRICE = 2;
    public static final int DB_FILE_LINE_INDEX_FIELD_CURRENCY = 3;
    public static final int DB_FILE_LINE_INDEX_FIELD_BRAND = 4;
    public static final int DB_FILE_LINE_INDEX_FIELD_CATEGORY = 5;
    public static final int DB_FILE_LINE_INDEX_FIELD_SUBCATEGORY = 6;
    public static final int DB_FILE_LINE_INDEX_FIELD_MATERIAL = 7;
    public static final int DB_FILE_LINE_INDEX_FIELD_TYPE = 8;
    public static final int DB_FILE_LINE_INDEX_FIELD_DESCRIPTION = 9;
    public static final int DB_FILE_LINE_INDEX_FIELD_IMAGE = 10;
    public static final int DB_FILE_LINE_INDEX_FIELD_STATUS = 11;
    public static final int DB_FILE_LINE_INDEX_FIELD_BUDGET = 12;
    public static final int DB_FILE_LINE_INDEX_FIELD_USTANOVKA = 13;
    public static final int DB_FILE_LINE_INDEX_FIELD_DOSTAVKA = 14;
    public static final int DB_FILE_LINE_INDEX_FIELD_SIZE = 15;
    public static final int DB_FILE_LINE_INDEX_FIELD_PRIZNAK = 16;
    public static final String PRICE_CURRENCY = "BYR";
    public static final String PRODUCT_TO_PUBLISH_STATUS = "1";
    public static final String PRODUCT_TO_HIDE_STATUS = "0";


    public final static String FILE_CURRENT_PRODUCTS_DB = "C:\\work\\shop\\db.csv";
    public static final String FILE_NO_IMAGE_PATH = "C:\\work\\shop\\noimage.jpg";


    public static StringBuffer sbExportOne = new StringBuffer();
    public static StringBuffer sbExportTwo = new StringBuffer();


    public abstract void getData();


    /**
     * input - csv line from db sql
     *
     * @param deleteProducts
     */
    public static void setAsDeletedObsoleteProducts(ArrayList<String> deleteProducts) {

        for (String lineToHide : deleteProducts) {

            ProductImporter pi = new ProductImporter();
            pi.setSKU(formatData(String.valueOf(lineToHide.split(";")[DB_FILE_LINE_INDEX_FIELD_SKU])));
            pi.setName(formatData(lineToHide.split(";")[DB_FILE_LINE_INDEX_FIELD_NAME]));
            pi.setStatus(PRODUCT_TO_HIDE_STATUS);

            ImporterService.sbExportTwo.append(pi);
        }
    }


    /**
     * checks what from DB to delete. which names are not in the list of new products
     *
     * @param newProductNames
     * @return
     */
    public static ArrayList<String> deleteFromDBNotExisting(ArrayList<String> newProductNames) {
        ArrayList<String> dataFromDB = new ArrayList<>();
        ArrayList<String> toDelete = new ArrayList<>();
        String[] splittedDBData;
        boolean isExists;
        try {
            dataFromDB = getAllDataFromCSVFile(FILE_CURRENT_PRODUCTS_DB);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (String dataFromDBLine : dataFromDB) {

            splittedDBData = dataFromDBLine.split(";", 0);

            isExists = false;
            for (String newProductName : newProductNames) {

                if (String.valueOf(splittedDBData[DB_FILE_LINE_INDEX_FIELD_NAME]).equals(newProductName)) {
                    isExists = true;
                }
            }
            if (!isExists) {
                toDelete.add(dataFromDBLine);
            }
        }
        return toDelete;
    }

    public static void saveTOFile(StringBuffer sb, String fileName) {


        BufferedInputStream bis;
        bis = new BufferedInputStream(new ByteArrayInputStream(sb.toString().getBytes(Charset.forName("UTF-8"))));
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(fileName));
            int ch;
            addBOMTOUTF(fos);
            while ((ch = bis.read()) != -1) {
                fos.write(ch);
            }
            bis.close();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void addBOMTOUTF(FileOutputStream fos) {

        try {
            fos.write(0xef); // emits 0xef
            fos.write(0xbb); // emits 0xbb
            fos.write(0xbf); // emits 0xbf
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void initializeFilesHeaders() {
        sbExportOne.append("sku").append(App.CSV_SEPARATOR).append("name").append(App.CSV_SEPARATOR).append("image").append(App.CSV_SEPARATOR).append("price").append(App.CSV_SEPARATOR).append("price_currency").append(App.CSV_END_LINE);
        sbExportTwo.append("SKU").append(App.CSV_SEPARATOR).append("SKU").append(App.CSV_SEPARATOR).append("name").append(App.CSV_SEPARATOR).append("category").append(App.CSV_SEPARATOR).append("pod_category").append(App.CSV_SEPARATOR).append("gabarity").append(App.CSV_SEPARATOR).append("brand").append(App.CSV_SEPARATOR).append("material").append(App.CSV_SEPARATOR).append("priznak").append(App.CSV_SEPARATOR).append("ustanovka").append(App.CSV_SEPARATOR).append("dostavka").append(App.CSV_SEPARATOR).append("type").append(App.CSV_SEPARATOR).append("kype_razmesch").append(App.CSV_SEPARATOR).append("kype_kolichestvo").append(App.CSV_SEPARATOR).append("softmebel_mehanizm").append(App.CSV_SEPARATOR).append("matracy_razmer").append(App.CSV_SEPARATOR).append("matracy_sostav").append(App.CSV_SEPARATOR).append("opisanie").append(App.CSV_SEPARATOR).append("status").append(App.CSV_SEPARATOR).append("pohozhye").append(App.CSV_SEPARATOR).append("color").append(App.CSV_SEPARATOR).append("htmlLink").append(App.CSV_SEPARATOR).append("specialKeyID").append(App.CSV_END_LINE);

    }


    public static Document getHTMLDocument(String htmlLink) {
        Document doc = null;
        try {
            doc = Jsoup.connect(htmlLink).timeout(0).get();
        } catch (java.lang.NullPointerException npe) {
            System.err.println(">>>>" + htmlLink);
            npe.printStackTrace();
        } catch (org.jsoup.HttpStatusException ex) {
            ex.printStackTrace();
            System.err.println(">>>>" + htmlLink);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(">>>>" + htmlLink);
        }

        return doc;
    }

    public static String saveImageOnDisk(String link, int imageLinksCount, boolean addPart) {


        String imageFileName;
        URL url;
        URLConnection conn;


        imageFileName = String.valueOf(App.BEGIN_ARTICLE_NUMBER) + (addPart ? "-" + imageLinksCount : "") + App.SAVED_FILES_EXTENSION;

        try {

            url = new URL(link); //Формирование url-адреса файла
            conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(new File(imageFileName));

            int ch;
            while ((ch = bis.read()) != -1) {
                fos.write(ch);
            }
            bis.close();
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
            imageFileName = "noimage.jpg";
        }

        return App.IMAGE_FILE_PATH + imageFileName;
    }

    public static String saveImageOnDisk(Elements elements, String imgTag) {
        String names = "";
        int count = 0;
        for (Element element : elements) {
            if (names.length() > 0)
                names += ",";
            names += saveImageOnDisk(element.baseUri() + element.attr(imgTag), count, true);
            count++;
        }
        return names;

    }

    public static String saveImageOnDisk(String link) {
        return saveImageOnDisk(link, "");
    }

    public static String saveImageOnDisk(String link, String brand) {


        String imageFileName;
        URL url;
        URLConnection conn;
        imageFileName = String.valueOf(App.BEGIN_ARTICLE_NUMBER) + App.SAVED_FILES_EXTENSION;
        String outputFolderPath = brand;
        File directory = new File(outputFolderPath);
        if (!directory.exists()) {
            directory.mkdir();
        }

        outputFolderPath = brand + "/" + new SimpleDateFormat("ddMMyyyy").format(new Date());
        directory = new File(outputFolderPath);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String filePath = outputFolderPath + "/" + imageFileName;
        if (link.indexOf("noimage") != -1 || link.equals("")) { //local file
            try {
                Files.copy(new FileInputStream(FILE_NO_IMAGE_PATH), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (!link.startsWith("http"))
                link = "http:" + link;
            try {
                try (InputStream in = new URL(link).openStream()) {
                    Files.copy(in, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                System.err.println("Link : " + link);
                e.printStackTrace();
                imageFileName = "noimage.jpg";
            }
        }

        return App.IMAGE_FILE_PATH + imageFileName;
    }


    public static HashMap fillDBDataHSForExactBrand(String brand) {
        ArrayList<String> existingProductDataFromDB = new ArrayList();
        HashMap<String, String> dbData = new HashMap<>();
        try {
            existingProductDataFromDB = getAllDataFromCSVFile(FILE_CURRENT_PRODUCTS_DB);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String[] splittedData;
        String fieldBrand = "";
        for (String item : existingProductDataFromDB) {
            splittedData = item.split(";", 0);

            fieldBrand = String.valueOf(splittedData[DB_FILE_LINE_INDEX_FIELD_BRAND]);
            if (fieldBrand.indexOf("\"") != -1)
                fieldBrand = fieldBrand.substring(1, fieldBrand.length() - 1); // without signs "
            if (brand.equals(fieldBrand)) {
                dbData.put(formatData(splittedData[DB_FILE_LINE_INDEX_FIELD_NAME]), item); // HashMap <name: all info>
            }
        }
        return dbData;
    }

    public static ArrayList<String> getAllDataFromCSVFile(String fileSource) throws FileNotFoundException {

        ArrayList<String> al = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileSource))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                al.add(formatData(line));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return al;
    }


    public static ArrayList<String> getAllDataFromCSVFile(String fileSource, String brand) throws FileNotFoundException {

        ArrayList<String> source = getAllDataFromCSVFile(fileSource);
        ArrayList<String> output = new ArrayList<>();
        for (String line : source)
            if (line.indexOf(brand) != -1)
                output.add(line);
        return output;
    }

    public static String formatData(String input) {
        return input.replaceAll(",", ".").replaceAll("\"", "").trim();
    }

}
