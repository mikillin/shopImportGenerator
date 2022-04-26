package mnsk.services;


import mnsk.App;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Author: S.Rogachevsky
 * Date: 05.12.16
 * Time: 22:57
 */
public abstract class ImporterService {
    public static StringBuffer sbExportOne = new StringBuffer();
    public static StringBuffer sbExportTwo = new StringBuffer();


    public abstract void getData();


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
            doc = Jsoup.connect(htmlLink).get();
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
        }

        return App.IMAGE_FILE_PATH + imageFileName;
    }

    public static String saveImageOnDisk(String link) {


        String imageFileName;
        URL url;
        URLConnection conn;


        imageFileName = String.valueOf(App.BEGIN_ARTICLE_NUMBER) + App.SAVED_FILES_EXTENSION;


        if (link.indexOf("noimage") != -1) { //local file
            try {
                Files.copy(new FileInputStream(link), Paths.get(imageFileName), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (!link.startsWith("http"))
                link = "http:" + link;


            try {


                try (InputStream in = new URL(link).openStream()) {
                    Files.copy(in, Paths.get(imageFileName), StandardCopyOption.REPLACE_EXISTING);
                }


           /* url = new URL(link); //Формирование url-адреса файла
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
            fos.close();*/

            } catch (IOException e) {
                System.err.println("Link : " + link);
                e.printStackTrace();
            }
        }


        return App.IMAGE_FILE_PATH + imageFileName;
    }


}
