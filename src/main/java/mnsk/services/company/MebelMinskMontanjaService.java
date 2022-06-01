package mnsk.services.company;


import mnsk.App;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import mnsk.services.CategoryProcessingService;
import mnsk.services.ImporterService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;



//todo:::: doesn't work
// timeout ?

//


public class MebelMinskMontanjaService extends ImporterService {

    public static final String BASE_URL = "https://www.mebelminsk.by/";
        public static final String CATEGORY_URL = "categories/kuhni?opt=kuhni-montanya";
    public static final String BRAND = "Монтанья";

    public static void getProductData() {


        try {
            ImporterService.initializeFilesHeaders();

            Document preview = ImporterService.getHTMLDocument(BASE_URL + CATEGORY_URL);
            Set<String> productPagesHS = new HashSet<>();
            Elements elements = preview.select("div.row.catalog").first().select("div.item").not(".with-banner").select("figure a");
            if (elements.size() == 0) {
                System.err.println(">>>> no product types on the page: " + BASE_URL + CATEGORY_URL);
            }

            for (Element subURL : elements) {
                productPagesHS.add(subURL.attr("href"));
            }

            for (String page : productPagesHS) {

                preview = ImporterService.getHTMLDocument(BASE_URL + page);

                if (preview == null) {
                    System.err.println(">>> preview is null :" + BASE_URL + page);
                    continue;
                }
                processProduct(preview);
            }


            System.out.printf("---Succesful---");
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


                String description = product.select("p.summary").text();
                String name = product.select("div.product__card div.line-h1 h1").text();
                String price = product.select("div.price meta[itemprop=lowPrice]") == null ? "-1" : product.select("div.price meta[itemprop=lowPrice]").attr("content").replaceAll(" ", "").replaceAll(".00", "").concat("0000");

                CategoryProcessingService.InnerClass siteCategoryInfo = CategoryProcessingService.getSiteCategoryNames(name);


                String category = siteCategoryInfo.categoryName;

                String podcategory = siteCategoryInfo.subcategoryName;


                if (CategoryProcessingService.isProductTypeVorbidden("Собственное производство", category, podcategory))
                    return;
                String material = "";
                String gabarity = "";


                String tmpImageNames = saveImageOnDisk(product.select("div.product__card .img-block a"), "href");


                in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
                in.setName(name);
                in.setImage(tmpImageNames);
                in.setPrice(price);
                in.setPRICE_CURRENCY("BYR");
                pi.setSKU(in.getSku());
                pi.setName(in.getName());
                pi.setBrand(BRAND);

                pi.setMaterial(material);
                pi.setGabarity(gabarity);
                pi.setOpisanie(description);
                pi.setStatus("1");

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
        getProductData();
    }
}
