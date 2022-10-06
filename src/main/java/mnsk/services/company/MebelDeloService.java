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

/**
 * 1. Upload new DB file from the reg.ru
 * check , . ; which are here needed
 */
public class MebelDeloService extends ImporterService {

    private final static int PRODUCT_CODE_SOURCE_CSV_INDEX = 0;
    public static final String BASE_URL = "https://mebel-delo.by/";
    public static final String SELECTOR_FOR_PRODUCT_TYPES_URL = ".blog-category-list .level2 a";
    public static final String SELECTOR_FOR_PAGINATION_LAST_ELEMENT_LINK = ".pagination-item:last-of-type>a";
    public static final String SELECTOR_FOR_PRODUCTS = ".product-thumb";
    public static final String BRAND = "MD";
    static HashMap<String, String> dbData = new HashMap<>(); //main base with old existing data



    //todo: in the 2nd file got products, which had only another price. why? they shouldn#t be there
    public static void getProductData() {

        dbData = fillDBDataHSForExactBrand(BRAND);


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


            //todo: delete>
//            productTypesPagesHS.clear();
//            productTypesPagesHS.add("/katalog/kuhonnye_garnitury/kuhonnye_garnitury1.html");
            //todo: delete<
            Set<String> productsPagesLinks = new HashSet<>();
            for (String page : productTypesPagesHS) {

//                Thread.sleep(400);
                preview = ImporterService.getHTMLDocument(BASE_URL + page);
                elements = preview.select(SELECTOR_FOR_PAGINATION_LAST_ELEMENT_LINK);

                if (elements.size() == 0) {
                    productsPagesLinks.add(page);
                } else {
                    int lastElementIndex = Integer.parseInt(elements.attr("title"));
                    for (int pageIndex = 1; pageIndex <= lastElementIndex; pageIndex++) {
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


            ArrayList<String> allProductNames = new ArrayList<>();
            //fill files
            for (String productLink : productLinks) {
                preview = ImporterService.getHTMLDocument(productLink);
//                Thread.sleep(400);

                processProduct(preview);

                String name = "";
                if (preview.select(".block-order.box h1[itemprop=name]") != null)
                    name = preview.select(".block-order.box h1[itemprop=name]").text();
                else
                    System.err.println(">>> achtung! : ");
                allProductNames.add(name);

            }

            processDeleteObsoleteProducts(allProductNames);

            System.out.printf("---");
            ImporterService.saveTOFile(ImporterService.sbExportOne, App.FILE_EXPORT_FIRST);
            ImporterService.saveTOFile(ImporterService.sbExportTwo, App.FILE_EXPORT_SECOND);


        } catch (Exception ex) {
            System.out.printf(">>> Error:: " + ex);

        }
    }


    private static void processDeleteObsoleteProducts(ArrayList<String> allProductNames) {
        ArrayList<String> deleteProducts = new ArrayList<>();
        boolean isExists;
        for (String key : dbData.keySet()) {

            isExists = false;
            for (String productName : allProductNames) {
                if (key.equals(productName.replaceAll(",", ".").replaceAll("\"", "").trim())) {
                    isExists = true;
                    break;
                }
            }
            if (!isExists) {
                deleteProducts.add(dbData.get(key));
            }

        }

        //todo: is it the same ??
        // deleteProducts = deleteFromDBNotExisting (allProductNames)
        setAsDeletedObsoleteProducts(deleteProducts);

    }

    private static void processProduct(Document product) {

        if (product != null) {
            try {

                String name = "";
                if (product.select(".block-order.box h1[itemprop=name]") != null)
                    name = product.select(".block-order.box h1[itemprop=name]")
                            .text()
                            .replaceAll(",",".")
                            .replaceAll("\"","");
                else
                    System.err.println(">>> achtung! : ");

                CategoryProcessingService.InnerClass siteCategoryInfo = CategoryProcessingService.getSiteCategoryNames(name);
                if (CategoryProcessingService.isProductTypeVorbidden(BRAND, siteCategoryInfo.categoryName, siteCategoryInfo.subcategoryName, name))
                    return;

                ImportNode in = new ImportNode();
                ProductImporter pi = new ProductImporter();


                if (dbData.get(name) != null) {
                    String[] dbSplittedData = dbData.get(name).split(";", -1);
                    in.setSku(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_SKU]);
                    in.setName(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_NAME]);
                    in.setImage(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_IMAGE].replaceAll("_0", ""));

                    in.setPRICE_CURRENCY(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_CURRENCY]);
                    pi.setSKU(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_SKU]);
                    pi.setName(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_NAME]);
                    pi.setBrand(BRAND);
                    pi.setStatus(PRODUCT_TO_PUBLISH_STATUS);
                    pi.setMaterial(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_MATERIAL]);
                    pi.setGabarity(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_SIZE]);
                    pi.setOpisanie(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_DESCRIPTION]);

                    pi.setCategory(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_CATEGORY]);
                    pi.setPod_category(dbSplittedData[DB_FILE_LINE_INDEX_FIELD_SUBCATEGORY]);
                } else {

                    String description = product.select("div.product-description").text();

                    String cathegoryMainFromVendorSite = "";
                    if (product.select("span.B_crumbBox .B_crumb").size() > 0)
                        cathegoryMainFromVendorSite = product.select("span.B_crumbBox .B_crumb").get(1).text();
                    else
                        System.err.println(">>> achtung! : ");


                    String subCathegoryFromVendorSite = "";
                    if (product.select("span.B_crumbBox .B_crumb").size() > 1) {
                        subCathegoryFromVendorSite = product.select("span.B_crumbBox .B_crumb").get(2).text();
                    } else {
                        System.err.println(">>> achtung! : ");
                    }
                    //

                    String material = "";
                    String gabarity = "";


                    String tmpImageNames = saveImageOnDisk(BASE_URL + product.select("div.fotorama>a#fotoRam").attr("href"), BRAND);


                    in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
                    in.setName(name);
                    in.setImage(tmpImageNames);
                    in.setPRICE_CURRENCY(PRICE_CURRENCY);
                    pi.setSKU(in.getSku());
                    pi.setName(in.getName());
                    pi.setBrand(BRAND);
                    pi.setStatus(PRODUCT_TO_PUBLISH_STATUS);
                    pi.setMaterial(material);
                    pi.setGabarity(gabarity);
                    pi.setOpisanie(description);

                    pi.setCategory(siteCategoryInfo.categoryName);
                    pi.setPod_category(siteCategoryInfo.subcategoryName);
                }

                String price = "";
                if (product.select("span.new-price [itemprop=price]") != null)
                    price = product.select("span.new-price [itemprop=price]").text();
                else
                    System.err.println(">>> achtung! : ");

                in.setPrice(String.valueOf(Integer.parseInt(price.replaceAll(" ", "")) * 10000));

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
