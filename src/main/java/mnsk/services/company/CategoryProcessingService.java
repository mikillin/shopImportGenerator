package mnsk.services.company;

import java.util.*;

public class CategoryProcessingService {

    public static final String[] PRODUCT_EXCEPTION_LIST = {
            "пуф",
            "банкетк",
            "обувниц",
            "подсветк",
            "зонт",
            "подставки для тв",
            "телевизор",
            "угловые диваны"
    };


    //todo: add all exceptions
    public static final Map<String, InnerClass> ExceptionsMap =Map.of(
            "MD", new InnerClass("Детские кровати",""),
            "MD", new InnerClass("Тумбы под телевизор",""),
            "Halmar", new InnerClass("Пуфы и банкетки",""),
            "Signal", new InnerClass("Картины",""),
            "Signal", new InnerClass("Пуфы",""),
            "MD", new InnerClass("Мебель для прихожей","Обувницы"),
            "MD", new InnerClass("Кухни","")

    );


    public static boolean checkProductCategoryException(String testCategory) {
        for (String productExceptionItem : PRODUCT_EXCEPTION_LIST) {
            if (testCategory.toLowerCase().indexOf(productExceptionItem) != -1)
                return true;
        }
        return false;
    }

    /**
     * @param originalNames Vendor,CategoryName,SubcategoryName
     * @return
     */
    public static String[] getSiteCategoryNames(String[] originalNamesInfo) {
        //TODO: returns names according to the exceptions


        return null;
    }

    public static class InnerClass {
        public String categoryName;
        public String subcategoryName;

        public InnerClass (String categoryName, String subcategoryName){
            this.categoryName = categoryName;
            this.subcategoryName = subcategoryName;
        }
    }
}
