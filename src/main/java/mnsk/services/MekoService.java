package mnsk.services;

import mnsk.App;
import mnsk.beans.ImportNode;
import mnsk.beans.ProductImporter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.List;

/**
 * Author: S.Rogachevsky
 * Date: 17.02.17
 * Time: 15:56
 */


public class MekoService extends ImporterService {


    public static String BASE_URL = "https://meko.by/";


    @Override
    public void getData() {
        initializeFilesHeaders();

        List<String> categoryLinks = getMenuLinks();

        List<String> assemblingProductsPages = getAssemblingProductsPages(categoryLinks);

        List<String> productsLinks = getProductsLinks(assemblingProductsPages);


        ImportNode in = new ImportNode();
        ProductImporter pi = new ProductImporter();
        String price = "";
        String productName = "";
        String imageURL = "";
        Elements breads;
        String subCathegory = "";
        String cathegoryMain = "";
        String imageName = "";
        String description = "";

        for (String productsLink : productsLinks) {
            subCathegory = "";

            Document product = getHTMLDocument(getLink(productsLink));

            price = product.select(".catalog-product-price span[itemprop='price'").text().replaceAll(",", "") + "00";
            productName = product.select(".catalog-product-title").text() + " Меко-" + App.BEGIN_ARTICLE_NUMBER;
            imageURL = product.select(".product-image-cnt>a").attr("href");
            breads = product.select(".item-bread");
            if (breads.size() > 2) {
                subCathegory = product.select(".item-bread").last().text();
                cathegoryMain = product.select(".item-bread").last().previousElementSibling().text();
            } else {
                cathegoryMain = product.select(".item-bread").last().text();
            }
            imageName = ImporterService.saveImageOnDisk(getLink(imageURL));
            description = product.select(".spacer").text();

            in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
            in.setName(productName);
            in.setImage(imageName);
            in.setPrice(price.trim());


            pi.setSKU(in.getSku());
            pi.setName(in.getName());
            //   pi.setMaterial(material);
            //   pi.setGabarity(size);

            pi.setCategory(cathegoryMain);
            pi.setPod_category(subCathegory);
            pi.setOpisanie(enhanceDescription(description));
            pi.setBrand("Меко мебель");
            ImporterService.sbExportOne.append(in.toString());
            ImporterService.sbExportTwo.append(pi.toString());
        }
        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        System.out.println("Hello!");

    }


    private ArrayList<String> getMenuLinks() {
        ArrayList<String> al = new ArrayList();
        Document mainPage = getHTMLDocument(BASE_URL);

        Elements menuLinks = mainPage.select("div.catalog-menu-list li.main:not(.has-sub)>a"); // без вложение

        menuLinks.addAll(mainPage.select("div.catalog-menu-list ul.sub_menu a"));// вложения

        for (Element menuLink : menuLinks) {
            al.add(menuLink.attr("href"));
        }

        return al;
    }

    private List<String> getAssemblingProductsPages(List<String> categoryLinks) {
        List<String> productsPages = new ArrayList();
        Document page;

        for (String pageLink : categoryLinks) {
            productsPages.add(pageLink); //добавим первую страницу, которая совпадает с номером "1"

            page = getHTMLDocument(getLink(pageLink));

            Elements additionalPagesLinks = page.select(".pagination-block .text-center a");

            for (Element additionalPageLink : additionalPagesLinks) {
                productsPages.add(additionalPageLink.attr("href"));
            }
        }

        return productsPages;
    }

    private List<String> getProductsLinks(List<String> assemblingProductsPages) {

        List<String> productsLinks = new ArrayList<>();

        for (String assemblingProductsPage : assemblingProductsPages) {
            Elements products = getHTMLDocument(getLink(assemblingProductsPage)).select(".hit-product-image-cnt");

            for (Element product : products) {
                productsLinks.add(product.attr("href"));
            }
        }

        return productsLinks;
    }


    private String enhanceDescription(String source) {

        return source.replaceAll("ОСНОВНЫЕ", "ОСНОВНЫЕ ПАРАМЕТРЫ:")
                .replaceAll("ГАБАРИТНЫЕ РАЗМЕРЫ СТОЛА", "ГАБАРИТНЫЕ РАЗМЕРЫ СТОЛА:")
                .replaceAll("(мес)", "(мес).")
                .replaceAll("ДСП", "ДСП.")
                .replaceAll(", мм", ", мм:")
                .replaceAll("(мм)", "(мм).");
    }

    private String getLink(String shortLink) {
        return BASE_URL + shortLink;

    }

}
