package mnsk.services.company;

import java.util.*;

public class CategoryProcessingService {

    public static final String[] PRODUCT_NAMES_EXCEPTION_PATTERN_LIST = {
            "пуф",
            "банкетк",
            "обувниц",
            "подсветк",
            "зонт",
            "подставки для тв",
            "телевизор",
            "угловые диваны"
    };

    public static final Map<String, Map<String, InnerClass>> CLASSIFICATION_RULES = Map.of(
            "Signal", Map.of("Комоды", new InnerClass("Мебель для спальни", "Мебель для спальни")),
            "Signal", Map.of("Кровати", new InnerClass("Мебель для спальни", "Кровати")),
            "Signal", Map.of("Шкафы, стеллажи", new InnerClass("Мебель для спальни", "Шкафы, стеллажи")),
            "Signal", Map.of("Вешалки", new InnerClass("Мебель для спальни", "Вешалки")),
            "Signal", Map.of("Столы", new InnerClass("Мебель для кухни", "Столы")),
            "Signal", Map.of("Барные стулья", new InnerClass("Мебель для кухни", "Барные стулья")),
            "Signal", Map.of("Компьютерные кресла", new InnerClass("Мебель для детской комнаты", "Компьютерные кресла")),
            "Signal", Map.of("Диваны", new InnerClass("Мебель для гостиной", "Диваны")),
            "Signal", Map.of("Стулья", new InnerClass("Мебель для гостиной", "Стулья и кресла")),
            "Signal", Map.of("Стулья и кресла", new InnerClass("Мебель для гостиной", "Стулья и кресла")),
            "Signal", Map.of("Стулья и кресла", new InnerClass("Мебель для гостиной", "Стулья и кресла")),

            "Halmar", Map.of("Кровати", new InnerClass("Мебель для спальни", "Кровати")),
            "Halmar", Map.of("Тумбы", new InnerClass("Мебель для спальни", "Тумбы")),
            "Halmar", Map.of("Тумбы", new InnerClass("Мебель для спальни", "Тумбы")),
            "Halmar", Map.of("Шкафы, стеллажи", new InnerClass("Мебель для спальни", "Шкафы, стеллажи")),
            "Halmar", Map.of("Вешалки", new InnerClass("Мебель для прихожей", "Вешалки")),
            "Halmar", Map.of("Прихожие", new InnerClass("Мебель для прихожей", "Прихожие")),
            "Halmar", Map.of("Столы", new InnerClass("Мебель для кухни", "Обеденные столы и группы")),
            "Halmar", Map.of("Компьютерные кресла", new InnerClass("Мебель для детской комнаты", "Компьютерные кресла")),
            "Halmar", Map.of("Диваны", new InnerClass("Мебель для гостиной", "Диваны")),
            "Halmar", Map.of("Комплекты", new InnerClass("Мебель для гостиной", "Гостиные комплекты")),
            "Halmar", Map.of("Кресла", new InnerClass("Мебель для гостиной", "Стулья и кресла")),
            "Halmar", Map.of("Кресла", new InnerClass("Мебель для гостиной", "Стулья и кресла")),
            "Halmar", Map.of("Стулья и кресла", new InnerClass("Мебель для гостиной", "Стулья и кресла")),
            "Halmar", Map.of("Ширмы", new InnerClass("Сопутствующие товары для дома", "Ширмы")),


            "MD", Map.of("Комоды", new InnerClass("Мебель для спальни", "Комоды")),
            "MD", Map.of("Кровати", new InnerClass("Мебель для спальни", "Кровати")),
            "MD", Map.of("Модульные спальни", new InnerClass("Мебель для спальни", "Модульные спальни")),
            "MD", Map.of("Ортопедические основания", new InnerClass("Мебель для спальни", "Ортопедические основания")),
            "MD", Map.of("Спальные гарнитуры", new InnerClass("Мебель для спальни", "Спальные гарнитуры")),
            "MD", Map.of("Туалетные столики", new InnerClass("Мебель для спальни", "Туалетные столики")),
            "MD", Map.of("Тумбы", new InnerClass("Мебель для спальни", "Тумбы")),
            "MD", Map.of("Угловые шкафы", new InnerClass("Мебель для спальни", "Угловые шкафы")),
            "MD", Map.of("Шкафы", new InnerClass("Мебель для спальни", "Шкафы")),
            "MD", Map.of("Шкафы-купе", new InnerClass("Мебель для спальни", "Шкафы-купе")),
            "MD", Map.of("Шкафы-пеналы", new InnerClass("Мебель для спальни", "Шкафы-пеналы")),
            "MD", Map.of("Шкафы, стеллажи", new InnerClass("Мебель для спальни", "Шкафы, стеллажи")),
            "MD", Map.of("Вешалки", new InnerClass("Мебель для прихожей", "Вешалки")),
            "MD", Map.of("Зеркала", new InnerClass("Мебель для прихожей", "Зеркала")),
            "MD", Map.of("Модульные прихожие", new InnerClass("Мебель для прихожей", "Модульные прихожие")),
            "MD", Map.of("Прихожие", new InnerClass("Мебель для прихожей", "Прихожие")),
            "MD", Map.of("Кухни", new InnerClass("Мебель для кухни", "Кухни")),
            "MD", Map.of("Кухонные диваны, уголки", new InnerClass("Мебель для кухни", "Кухонные диваны, уголки")),
            "MD", Map.of("Кухонные столы", new InnerClass("Мебель для кухни", "Кухонные столы")),
            "MD", Map.of("Обеденные столы и группы", new InnerClass("Мебель для кухни", "Обеденные столы и группы")),
            "MD", Map.of("Сопутствующие товары для кухни", new InnerClass("Мебель для кухни", "Сопутствующие товары для кухни")),
            "MD", Map.of("Стулья для кухни", new InnerClass("Мебель для кухни", "Стулья для кухни")),
            "MD", Map.of("Барные стулья", new InnerClass("Мебель для кухни", "Барные стулья")),
            "MD", Map.of("Компьютерные столы", new InnerClass("Мебель для детской комнаты", "Компьютерные столы")),
            "MD", Map.of("Модульная детская мебель", new InnerClass("Мебель для детской комнаты", "Модульная детская мебель")),
            "MD", Map.of("Диваны", new InnerClass("Мебель для гостиной", "Диваны")),
            "MD", Map.of("Журнальные столики", new InnerClass("Мебель для гостиной", "Журнальные столики")),
            "MD", Map.of("Стенки", new InnerClass("Мебель для гостиной", "Стенки")),
            "MD", Map.of("Гамаки", new InnerClass("Сопутствующие товары для дома", "Гамаки")),
            "MD", Map.of("Декор для дома", new InnerClass("Сопутствующие товары для дома", "Декор для дома")),
            "MD", Map.of("Разное", new InnerClass("Сопутствующие товары для дома", "Разное")),
            "MD", Map.of("Полки", new InnerClass("Сопутствующие товары для дома", "Полки")),
            "MD", Map.of("Детские качели", new InnerClass("Сопутствующие товары для дома", "Детские качели")),


            "Собственное производство", Map.of("Кухни", new InnerClass("Мебель для кухни", "Кухни")),

            "Монтанья", Map.of("Кухни", new InnerClass("Мебель для кухни", "Кухни"))
    );

    //    public static final Map<String, Map<String, InnerClass>> CLASSIFICATION_RULES = Map.of(
//            "Signal", Map<String, InnerClass>() = Map.of(
//                    "Комоды", new InnerClass("Детские кровати", "")
//            )
//
//    );
    //todo: add all exceptions
    public static final Map<String, List<InnerClass>> BRAND_EXCEPTIONS_MAP = Map.of(
            "MD", new ArrayList<InnerClass>() {
                {
                    add(new InnerClass("Детские кровати", ""));
                    add(new InnerClass("Тумбы под телевизор", ""));
                    add(new InnerClass("Мебель для прихожей", "Обувницы"));
                    add(new InnerClass("Кухни", ""));
                }
            },
            "Halmar", new ArrayList<InnerClass>() {
                {
                    add(new InnerClass("Пуфы и банкетки", ""));
                }
            },
            "Signal", new ArrayList<InnerClass>() {
                {
                    add(new InnerClass("Картины", ""));
                    add(new InnerClass("Пуфы", ""));
                }
            },
            "all", new ArrayList<InnerClass>() {
                {
                    add(new InnerClass("Картины", "Картины"));
                    add(new InnerClass("", "Картины"));
                    add(new InnerClass("Матрасы", "Матрасы"));
                    add(new InnerClass("", "Матрасы"));
                }
            }
    );


    public static boolean isProductTypeVorbidden(String brand, String testCategory, String testSubcategory) {
        for (String productExceptionItem : PRODUCT_NAMES_EXCEPTION_PATTERN_LIST) {
            if (testCategory.toLowerCase().indexOf(productExceptionItem) != -1
                    || testSubcategory.toLowerCase().indexOf(productExceptionItem) != -1)
                return true;
        }

        ArrayList<InnerClass> allExceptions = new ArrayList();
        if (!BRAND_EXCEPTIONS_MAP.get(brand).isEmpty())
            allExceptions.addAll(BRAND_EXCEPTIONS_MAP.get(brand));
        if (!BRAND_EXCEPTIONS_MAP.get("all").isEmpty())
            allExceptions.addAll(BRAND_EXCEPTIONS_MAP.get("all"));

        for (InnerClass exceptionsItem : allExceptions) {
            if (exceptionsItem.categoryName.equals(testCategory) &&
                    exceptionsItem.subcategoryName.equals(testSubcategory))
                return true;

        }

        return false;
    }

    /**
     * @param originalNamesInfo Vendor,CategoryName,SubcategoryName
     * @return
     */
    public static String[] getSiteCategoryNames(String[] originalNamesInfo) {
        //TODO: returns names according to the exceptions


        return null;
    }

    public static class InnerClass {
        public String categoryName;
        public String subcategoryName;

        public InnerClass(String categoryName, String subcategoryName) {
            this.categoryName = categoryName;
            this.subcategoryName = subcategoryName;
        }
    }
}
