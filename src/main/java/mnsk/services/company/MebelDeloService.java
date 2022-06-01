package mnsk.services.company;


import mnsk.App;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.CategoryProcessingService;
import mnsk.services.ImporterService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;


public class MebelDeloService extends ImporterService {

    private final static int PRODUCT_CODE_SOURCE_CSV_INDEX = 0;
    public static final String BASE_URL = "https://mebel-delo.by/";
    public static final String SELECTOR_FOR_PRODUCT_TYPES_URL = ".blog-category-list .level2 a";
    public static final String SELECTOR_FOR_PAGINATION_LAST_ELEMENT_LINK = ".pagination-item:last-of-type>a";
    public static final String SELECTOR_FOR_PRODUCTS = ".product-thumb";
    public static final String BRAND = "MD";

    public static void getProductData() {


        try {
            ImporterService.initializeFilesHeaders();

            Document preview = ImporterService.getHTMLDocument(BASE_URL);
            Set<String> productTypesPagesHS = new HashSet<>();
            Elements elements = preview.select(SELECTOR_FOR_PRODUCT_TYPES_URL);
            if (elements.size() == 0) {
                System.err.println(">>>> no product types on the page: " + BASE_URL);
            }

            for (Element subURL : elements) {
                productTypesPagesHS.add(subURL.attr("href"));
            }

            Set<String> productsPagesLinks = new HashSet<>();
            for (String page : productTypesPagesHS) {

//                Thread.sleep(400);
                preview = ImporterService.getHTMLDocument(BASE_URL + page);
                elements = preview.select(SELECTOR_FOR_PAGINATION_LAST_ELEMENT_LINK);

                if (elements.size() == 0) {
                    productsPagesLinks.add(page);
                } else {
                    int lastElementIndex = Integer.parseInt(elements.attr("title"));
                    for (int pageIndex = 0; pageIndex < lastElementIndex; pageIndex++) {
                        productsPagesLinks.add(page + "?cat_page=" + pageIndex + "#cat");

                    }
                }


            }

            List<String> productLinks = new ArrayList<>();
            for (String page : productsPagesLinks) {
//                Thread.sleep(400);
                preview = ImporterService.getHTMLDocument(BASE_URL + page);
                if (preview == null) {
                    System.err.println(">>> preview is null :" + BASE_URL + page);
                    continue;
                }
                elements = preview.select(SELECTOR_FOR_PRODUCTS);
                if (elements.size() == 0) {
                    System.err.println(">>> error: no such products:" + BASE_URL + page);
                } else {
                    String link = "";
                    for (Element element : elements) {
                        if (element.select("a").size() > 0)
                            link = element.select("a").get(0).attr("href");
                        else
                            System.err.println(">>> error: no such products:" + BASE_URL + page + "size ==0");
                        productLinks.add(BASE_URL + link);
                    }
                }
            }


            //fill files
            for (String productLink : productLinks) {
                preview = ImporterService.getHTMLDocument(productLink);
//                Thread.sleep(400);

                processProduct(preview);

            }


            System.out.printf("---");
            ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
            ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);


        } catch (Exception ex) {
            System.out.printf(">>> Error:: " + ex);

        }
    }


    private static void processProduct(Document product) {

        if (product != null) {
            try {
                ImportNode in = new ImportNode();
                ProductImporter pi = new ProductImporter();


                String description = product.select("div.product-description").text();

                String category = "";
                if (product.select("span.B_crumbBox .B_crumb").size() > 0)
                    category = product.select("span.B_crumbBox .B_crumb").get(1).text();
                else
                    System.err.println(">>> achtung! : ");


                String podcategory = "";
                if (product.select("span.B_crumbBox .B_crumb").size() > 1) {
                    podcategory = product.select("span.B_crumbBox .B_crumb").get(2).text();
                } else {
                    System.err.println(">>> achtung! : ");
                }
                //
                if (CategoryProcessingService.isProductTypeVorbidden("MD", category, podcategory))
                    return;
                String material = "";
                String gabarity = "";
                String price = "";
                if (product.select("span.new-price [itemprop=price]") != null)
                    price = product.select("span.new-price [itemprop=price]").text();
                else
                    System.err.println(">>> achtung! : ");
                String name = "";
                if (product.select(".block-order.box h1[itemprop=name]") != null)
                    name = product.select(".block-order.box h1[itemprop=name]").text();
                else
                    System.err.println(">>> achtung! : ");


                String tmpImageNames = saveImageOnDisk(BASE_URL + product.select("div.fotorama>a#fotoRam").attr("href"));


                in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
                in.setName(name);
                in.setImage(tmpImageNames);
                in.setPrice(String.valueOf(Integer.parseInt(price) * 10000));
                in.setPRICE_CURRENCY("BYR");
                pi.setSKU(in.getSku());
                pi.setName(in.getName());
                pi.setBrand(BRAND);
                pi.setStatus("1");
                pi.setMaterial(material);
                pi.setGabarity(gabarity);
                pi.setOpisanie(description);

                pi.setCategory(category);
                pi.setPod_category(podcategory);
                sbExportOne.append(in.toString());
                sbExportTwo.append(pi.toString());

            } catch (Exception exc) {
                System.err.println(">> not right link: " + product.baseUri());
            }
        } else {
            System.err.println(">> product was null  ");
        }
    }


    @Override
    public void getData() {

    }
}
