package mnsk.services.company;

import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.ImporterService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import mnsk.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: S.Rogachevsky
 * Date: 15.09.17
 * Time: 21:56
 */
public class NMshopService extends ImporterService {

    public static String BASE_URL = "https://nm-shop.by/";

    private String productMaterial = "";
    private String productSize = "";

    @Override
    public void getData() {
        initializeFilesHeaders();
        List<String> categoryLinks = getMenuLinks();
        List<String> assemblingProductsPages = getAssemblingProductsPages(categoryLinks);
        List<String> productsLinks = getProductsLinks(assemblingProductsPages);

        ImportNode in = new ImportNode();
        ProductImporter pi = new ProductImporter();

        String imageURL = "";
        String imageName = "";
        String description = "";

        for (String productLink : productsLinks) {
            Document product = getHTMLDocument(productLink);

            imageURL = product.select(".image-additional div[data-index=0] a").attr("data-zoom-image");
            imageName = ImporterService.saveImageOnDisk(imageURL);

            description = getDescription(product);
            in.setName(product.select("div.breadcrumb+h1").text());
            in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
            in.setImage(imageName);


            pi.setSKU(in.getSku());
            pi.setName(in.getName());
            pi.setMaterial(productMaterial);
            pi.setGabarity(productSize);


            pi.setOpisanie((description));
            pi.setBrand(""); //там куча брендов. на сайте их нет.
            ImporterService.sbExportOne.append(in.toString());
            ImporterService.sbExportTwo.append(pi.toString());
        }
        ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
        ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);

        System.out.println("Hello!");
    }

    private List<String> getMenuLinks() {

        ArrayList<String> al = new ArrayList();
        Document mainPage = getHTMLDocument(BASE_URL);

        Elements menuLinks = mainPage.select("div.accordeon_categ ul li ul li:not(:has(ul)) a"); // без вложение

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

            page = getHTMLDocument(pageLink);

            Elements additionalPagesLinks = page.select(".pagination .links a");

            for (Element additionalPageLink : additionalPagesLinks) {
                if (additionalPageLink.text().matches("\\d")) { //там дальше будет "на страницу дальше" и "в самый конец"
                    productsPages.add(additionalPageLink.attr("href"));
                }
            }
        }

        return productsPages;
    }

    private List<String> getProductsLinks(List<String> assemblingProductsPages) {

        List<String> productsLinks = new ArrayList<>();

        for (String assemblingProductsPage : assemblingProductsPages) {
            Elements products = getHTMLDocument(assemblingProductsPage).select("div.item .name a");

            for (Element product : products) {
                productsLinks.add(product.attr("href"));
            }
        }

        return productsLinks;
    }

    private String getDescription(Document product) {
        String description = "";
        String descriptionValue = "";
        StringBuffer result = new StringBuffer();

        productMaterial = "";
        productSize = "";

        Elements descriptionElements = product.select("table.attribute tr");
        for (Element descriptionElement : descriptionElements) {
            description = descriptionElement.select("td:first-child").text();
            if (description.toLowerCase().contains("описание"))
                continue;

            descriptionValue = descriptionElement.select("td:last-child").text();

            if (description.toLowerCase().contains("материал"))
                productMaterial += descriptionValue + ",";
            if (description.toLowerCase().contains("размер"))
                productSize += descriptionValue + ",";

            result.append(description).append(": ").append(descriptionValue).append(". ");
        }

        if (productMaterial.length() > 0)
            productMaterial = productMaterial.substring(0, productMaterial.length() - 1);//убираем запятую
        if (productSize.length() > 0)
            productSize = productSize.substring(0, productSize.length() - 1);//убираем запятую

        return result.toString();

    }
}

