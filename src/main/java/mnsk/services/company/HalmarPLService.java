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
 * Date: 05.12.16
 * Time: 20:36
 */
public class HalmarPLService extends ImporterService {

    public static final String TAG_ATTRIBUTE_WITH_PATH = "href";
    private static final String BASE_URL = "http://www.halmar.pl";
    public static final String SELECTOR_SECTIONS_LINKS = "span.accordeonck_outer:not(.toggler)>a.accordeonck[href*='/pl']";
    public static final String BRAND = "HALMAR";


    @Override
    public void getData() {
        initializeFilesHeaders();

        List<String> sectionLinks = getSectionLinks();
        List<String> pagesOFProducts = new ArrayList<>();

        for (String sectionLink : sectionLinks) {

            Document doc = getHTMLDocument(sectionLink);
            Elements productTypesLinks = doc.select(SELECTOR_SECTIONS_LINKS);

            for (Element productTypesLink : productTypesLinks) {
                pagesOFProducts.clear();
                //TODO: удалить исключения
//                if (!("/pl/hurt-oferta/stoly/blaty-szklo-i-mdf").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/stoly/stoly-z-blatami-laminowanymi").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/stoly/zestawy-stolowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/stoly/stoly-drewniane").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/komody-i-mebloscianki").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/kuchnie").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/lawy").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/meble-mlodziezowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/sypialnie").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/meble-wypoczynkowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/przedpokoje").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/wieszaki").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/stoliki-barowe-regaly-i-gazetniki").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/stoliki-rtv").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/meble-barowe-i-restauracyjne").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/meble-ogrodowe-i-tarasowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/biurka").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/fotele/gabinetowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/fotele/pracownicze").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/fotele/młodzieżowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/meble-nowy-styl").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/krzesla/metalowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-oferta/krzesla/drewniane").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/stoly/blaty-szklane-i-mdf").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/stoly/zestawy-stolowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/stoly/stoly-drewniane").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/komody-i-meblościanki").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/kuchnie").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/lawy").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/meble-mlodziezowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/sypialnie").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/wieszaki").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/meble-barowe-i-restauracyjne").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/meble-ogrodowe-i-tarasowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/biurka").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/fotele/gabinetowe").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/fotele/pracownicze").equals(productTypesLink.attr("href"))
//                        && !("/pl/hurt-nowosci/fotele/mlodziezowe").equals(productTypesLink.attr("href"))
//                        && !("").equals(productTypesLink.attr("href"))
//                        && !("").equals(productTypesLink.attr("href"))
//                        && !("").equals(productTypesLink.attr("href"))
//                        && !("").equals(productTypesLink.attr("href"))
//                        && !("").equals(productTypesLink.attr("href"))
//                        && !("").equals(productTypesLink.attr("href"))
//                        && !("").equals(productTypesLink.attr("href"))
//                        ) {
//                }

                String firstPageOFProductsLink = productTypesLink.attr("href");
                pagesOFProducts.add(firstPageOFProductsLink);
                for (Element otherHTMLPagesLinks : getHTMLDocument(firstPageOFProductsLink).select("a.pagenav:not(.hasTooltip)"))
                    pagesOFProducts.add(otherHTMLPagesLinks.attr(TAG_ATTRIBUTE_WITH_PATH));


                processProductsPagesOFOneCategory(pagesOFProducts, getCategory(getHTMLDocument(firstPageOFProductsLink)));


            }
        }

        saveTOFile(sbExportOne, App.FILE_EXPORT_FIRST);
        saveTOFile(sbExportTwo, App.FILE_EXPORT_SECOND);
        System.out.println("Hello!");
    }


    private List<String> getSectionLinks() {
        List<String> result = new ArrayList<>();
        result.add("/pl/hurt-oferta");
        return result;
    }

    private static void processProductsPagesOFOneCategory(List<String> pagesWithproductsLink, String category) {
        List<String> productsLinks = new ArrayList<>();
        String tempPageLink = "";
        for (String pageWithProductsLink : pagesWithproductsLink) {
            for (Element product : getHTMLDocument(pageWithProductsLink).select(".spacer>a")) {
                tempPageLink = product.attr(TAG_ATTRIBUTE_WITH_PATH);
                if (!"/pl/hurt-oferta/lawy/livia-kwadrat-cherry-ant.-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/lawy/martina-cherry-ant.-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/wieszaki/w27-cherry-ant.-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/wieszaki/w28-cherry-ant.-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/albert-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/citrone-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/dominik-czereśnia-ant.-casilda-beż-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/franco-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/gerard-3-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/gerard-6-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/gerard-7-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/hubert-2-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/jakub-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/konrad-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/sylwek-1-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/krzesla/drewniane/sylwek-4-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/albert-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/stoly/zestawy-stolowe/zestaw-record-prostokąt-stół-4-krzesła-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/dominik-czereśnia-ant.-casilda-beż-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/citrone-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/franco-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/gerard-3-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/gerard-6-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/gerard-7-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/hubert-2-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/sylwek-1-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/sylwek-4-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-nowosci/krzesla/drewniane/konrad-czereśnia-ant.-ii-mesh-1-detale".equals(tempPageLink) &&
                        !"/pl/hurt-oferta/lawy/vita-h-cherry-ant.-detale".equals(tempPageLink))
                    productsLinks.add(tempPageLink);

            }

        }


        processProductPages(productsLinks, category);


    }

    private static void processProductPages(List<String> productsLinks, String category) {
        for (String productLink : productsLinks) {
            processProduct(getHTMLDocument(productLink), category);

        }
    }

    private static void processProduct(Document product, String category) {
        ImportNode in = new ImportNode();
        ProductImporter pi = new ProductImporter();

        Elements multiImages = product.select("div.additional-images a");
        String tmpImageNames = "";

        int i = 0;
        if (multiImages.size() > 0) {
            for (Element image : multiImages) {
                tmpImageNames += (tmpImageNames.equals("")) ? "" : ",";
                tmpImageNames += saveImageOnDisk(BASE_URL + image.attr("href"), i, true);
                i++;
            }
        } else {
            tmpImageNames = saveImageOnDisk(product.select("div.main-image>a").attr("href"));
        }
        String description = product.select("div.product-description").text();

        String material = getMaterial(description);

        String name = getName(category, description, product.select("span.title").text());
        String gabarity = getGabarity(description);

        in.setSku(String.valueOf(App.BEGIN_ARTICLE_NUMBER++));
        in.setName(name);
        in.setImage(tmpImageNames);


        pi.setSKU(in.getSku());
        pi.setName(in.getName());
        pi.setBrand(BRAND);

        pi.setMaterial(material);
        pi.setGabarity(gabarity);

        pi.setCategory(category);
        sbExportOne.append(in.toString());
        sbExportTwo.append(pi.toString());


    }

    private static String getCategory(Document htmlPage) {
        return htmlPage.select(".category_count>a").text();

    }

    public static Document getHTMLDocument(String htmlLink) {
        return ImporterService.getHTMLDocument(BASE_URL + htmlLink);
    }


    private static String getMaterial(String description) {
        String material = "";
        try {
            material = description.substring(getMaterialBeginIndex(description), getMaterialEndIndex(description));

        } catch (StringIndexOutOfBoundsException e) {
            System.err.println(">>>> " + description);
            System.err.println(e);
        }
        return material;
    }

    private static String getName(String category, String description, String productName) {
        String productDescription = " ";
        int beginIndexProductDescription = description.indexOf(productName) + productName.length();
        int endIndexProductDescription = getGabarityBeginIndex(description) - getGabarityWordLength(description);
        if (beginIndexProductDescription != productName.length() - 1 || endIndexProductDescription != -1)
            try {
                productDescription = " " + description.substring(beginIndexProductDescription, endIndexProductDescription).trim() + " ";
            } catch (StringIndexOutOfBoundsException ex) {
                System.err.println(">>>>>" + ex);
                System.err.println(">>>>>" + category + " >> " + description);
            }

        return category + productDescription + productName;
    }

    private static String getGabarity(String description) {
        return description.substring(getGabarityBeginIndex(description), getGabarityEndIndex(description));
    }

    private static int getGabarityWordLength(String description) {

        int index = -1;
        if (description.contains("wymiary")) {
            return "wymiary:".length();
        } else if (description.contains("wymiarach")) {
            return "wymiarach:".length();
        } else if (description.contains("wymiar")) {
            return "wymiar:".length();
        } else if (description.contains("długość zestawu:")) {
            return "długość zestawu:".length();
        } else if (description.contains("wysokość:")) {
            return "wysokość".length();
        } else if (description.contains("średnica trzpienia:")) {
            return "średnica trzpienia:".length();
        } else if (description.contains("rozmiary:")) {
            return "rozmiary:".length();
        } else if (description.contains("długość zestawu:")) {
            return "długość zestawu:".length();
        }
        return index;
    }


    private static int getGabarityBeginIndex(String description) {

        int index = -1;
        if (description.contains("wymiary")) {
            index = description.indexOf("wymiary:") + "wymiary:".length();
        } else if (description.contains("wymiarach")) {
            index = description.indexOf("wymiarach:") + "wymiarach:".length();
        } else if (description.contains("wymiar")) {
            index = description.indexOf("wymiar:") + "wymiar:".length();
        } else if (description.contains("długość zestawu:")) {
            index = description.indexOf("długość zestawu:") + "długość zestawu:".length();
        } else if (description.contains("wysokość:")) {
            index = description.indexOf("wysokość:") + "wysokość:".length();
        } else if (description.contains("średnica trzpienia:")) {
            index = description.indexOf("średnica trzpienia:") + "średnica trzpienia:".length();
        } else if (description.contains("rozmiary:")) {
            index = description.indexOf("rozmiary:") + "rozmiary:".length();
        } else if (description.contains("długość zestawu:")) {
            index = description.indexOf("długość zestawu:") + "długość zestawu:".length();
        }
        return index;
    }

    private static int getMaterialBeginIndex(String description) {

        return description.indexOf("materiał:") + "materiał:".length();

    }

    private static int getMaterialBeginWordIndex(String description) {

        return description.indexOf("materiał:");

    }

    private static int getColorBeginIndex(String description) {

        return description.indexOf("kolor:");

    }

    private static int getGabarityEndIndex(String description) {
        int index;

        int materialBeginIndex = getMaterialBeginWordIndex(description);
        int colorBeginIndex = getColorBeginIndex(description);
        int gabarityBeginIndex = getGabarityBeginIndex(description);

        if (gabarityBeginIndex > colorBeginIndex
                && gabarityBeginIndex > materialBeginIndex)
            index = description.length();
        else if (gabarityBeginIndex < colorBeginIndex
                && gabarityBeginIndex < materialBeginIndex) {
            if (colorBeginIndex > materialBeginIndex)
                index = materialBeginIndex;
            else index = colorBeginIndex;
        } else if (colorBeginIndex > materialBeginIndex)
            index = colorBeginIndex;
        else
            index = gabarityBeginIndex;

        return index;
    }

    private static int getMaterialEndIndex(String description) {

        int index;

        int materialBeginIndex = getMaterialBeginIndex(description);
        int colorBeginIndex = getColorBeginIndex(description);
        int gabarityBeginIndex = getGabarityBeginIndex(description);

        if (materialBeginIndex > colorBeginIndex
                && materialBeginIndex > gabarityBeginIndex)
            index = description.length();
        else if (materialBeginIndex < colorBeginIndex
                && materialBeginIndex < gabarityBeginIndex) {
            if (colorBeginIndex > gabarityBeginIndex)
                index = gabarityBeginIndex;
            else index = colorBeginIndex;
        } else if (colorBeginIndex > gabarityBeginIndex)
            index = colorBeginIndex;
        else
            index = gabarityBeginIndex;

        return index;
    }


}
