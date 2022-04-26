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
            "телевиз",
            "угловые диваны",
            "мойк",
            "вытяжк",
            "матрац",
            "подушк",
            "Панель декоративная",
            "Ручка",
            "Колеса",
            "Кровать-машинка",
            "Подсветка дна,фар",
            "Чехол-простынь",
            "Сидение",
            "Переходник",
            "Чехол",
            "Корзина",
            "Подцветочница"


    };

    public static final ArrayList<Map<String, InnerClass>> PRODUCT_NAME_CLASSIFICATION_RULES = new ArrayList<Map<String, InnerClass>>() {
        {
            add(Map.of("Комод", new InnerClass("Мебель для спальни", "Комоды")));
            add(Map.of("Кровать-диван,Кровать,Основание кроватное,Без матраца", new InnerClass("Мебель для спальни", "Кровати")));
            add(Map.of("Модульная спальня,Модульная система", new InnerClass("Мебель для спальни", "Модульные спальни")));
            add(Map.of("Ортопедическое основание", new InnerClass("Мебель для спальни", "Ортопедические основания")));
            add(Map.of("Спальный гарнитур,Спальня", new InnerClass("Мебель для спальни", "Спальные гарнитуры")));
            add(Map.of("Туалетный стол", new InnerClass("Мебель для спальни", "Туалетные столики")));
            add(Map.of("Тумба,Тумба прикроватная,Комплект накладок для Тумбы", new InnerClass("Мебель для спальни", "Тумбы")));
            add(Map.of("Шкаф,Угловое окончание,Надстройка,Шкаф навесной,Полки для шкафа,Приставка к шкафу", new InnerClass("Мебель для спальни", "Шкафы")));
            add(Map.of("Шкаф-купе", new InnerClass("Мебель для спальни", "Шкафы-купе")));
            add(Map.of("Пенал,Пенал открытый,к пеналу", new InnerClass("Мебель для спальни", "Шкафы-пеналы")));
            add(Map.of("Стеллаж", new InnerClass("Мебель для спальни", "Шкафы,стеллажи")));
            add(Map.of("без матраца", new InnerClass("Мебель для спальни", "Кровати")));
            add(Map.of("Полка для кровати,над кроватью", new InnerClass("Мебель для спальни", "Полки для кровати")));

            add(Map.of("Вешалка", new InnerClass("Мебель для прихожей", "Вешалки")));
            add(Map.of("Зеркало", new InnerClass("Мебель для прихожей", "Зеркала")));
            add(Map.of("Модульная прихожая", new InnerClass("Мебель для прихожей", "Модульные прихожие")));
            add(Map.of("Прихожая", new InnerClass("Мебель для прихожей", "Прихожие")));
            add(Map.of("Прихожая", new InnerClass("Мебель для прихожей", "Набор мебели для прихожей")));


            add(Map.of("Кухонный гарнитур,Кухня", new InnerClass("Мебель для кухни", "Кухни")));
            add(Map.of("Кухонный уголок", new InnerClass("Мебель для кухни", "Кухонные диваны,уголки")));
            add(Map.of("Стол,Стол раздвижной", new InnerClass("Мебель для кухни", "Кухонные столы")));
            add(Map.of("Крышка Стола,Стол-рабочий,Стол-трансформер,Стол-книжка,Стол", new InnerClass("Мебель для кухни", "Столы")));
            add(Map.of("стол обеденный,Обеденная группа", new InnerClass("Мебель для кухни", "Обеденные столы и группы")));
            add(Map.of("Стул,Табурет", new InnerClass("Мебель для кухни", "Стулья для кухни")));
            add(Map.of("Стол барный", new InnerClass("Мебель для кухни", "Барные стулья")));

            add(Map.of("Набор для школьников и студентов с рабочим местом", new InnerClass("Мебель для детской комнаты", "Набор для школьников и студентов с рабочим местом")));
          //todo: change to plural
            add(Map.of("Стол компьютерный", new InnerClass("Мебель для детской комнаты", "Компьютерный стол ")));
            add(Map.of("Модульная детская,Детская", new InnerClass("Мебель для детской комнаты", "Модульная детская мебель")));
            add(Map.of("Кресло компьютерное", new InnerClass("Мебель для детской комнаты", "Компьютерные кресла")));
            add(Map.of("Письменный стол", new InnerClass("Мебель для детской комнаты", "Письменный стол")));
            add(Map.of("Надстройка на стол,Полка — надстройка", new InnerClass("Мебель для детской комнаты", "Надстройка на стол")));
            add(Map.of("Уголок школьника", new InnerClass("Мебель для детской комнаты", "Уголоки школьника")));

            add(Map.of("Диван,Евродиван", new InnerClass("Мебель для гостиной", "Диваны")));
            add(Map.of("Журнальные столики,Стол журнальный,стола", new InnerClass("Мебель для гостиной", "Журнальные столики")));
            add(Map.of("Cтенка", new InnerClass("Мебель для гостиной", "Стенки")));
            add(Map.of("кресло", new InnerClass("Мебель для гостиной", "Стулья и кресла")));
            add(Map.of("Комплект подсветки", new InnerClass("Мебель для гостиной", "Комплекты")));
            add(Map.of("Стол журнальный", new InnerClass("Мебель для гостиной", "Столы журнальные")));
            add(Map.of("Центральная секция,Модульная стенка", new InnerClass("Мебель для гостиной", "Модульные системы")));
            add(Map.of("Гостиная,Набор мебели для гостиной ", new InnerClass("Мебель для гостиной", "Набор мебели для гостиной")));

            add(Map.of("Гамак", new InnerClass("Сопутствующие товары для дома", "Гамаки")));
            add(Map.of("Декор для дома", new InnerClass("Сопутствующие товары для дома", "Декор для дома")));
            add(Map.of("Полка навесная,Полка", new InnerClass("Сопутствующие товары для дома", "Полки")));
            add(Map.of("Детские качели", new InnerClass("Сопутствующие товары для дома", "Детские качели")));
            add(Map.of("Скамья", new InnerClass("Сопутствующие товары для дома", "Скамьи")));
            add(Map.of("Консоль", new InnerClass("Сопутствующие товары для дома", "Консоли")));
            add(Map.of("Антресоль", new InnerClass("Сопутствующие товары для дома", "Антресоли")));

        }
    };

    public static final Map<String, ArrayList<Map<String, InnerClass>>> CLASSIFICATION_RULES = Map.ofEntries(
            Map.entry("Signal", new ArrayList<Map<String, InnerClass>>() {
                {
                    add(Map.of("Комоды", new InnerClass("Мебель для спальни", "Мебель для спальни")));
                    add(Map.of("Кровати", new InnerClass("Мебель для спальни", "Кровати")));
                    add(Map.of("Шкафы, стеллажи", new InnerClass("Мебель для спальни", "Шкафы, стеллажи")));
                    add(Map.of("Вешалки", new InnerClass("Мебель для спальни", "Вешалки")));
                    add(Map.of("Столы", new InnerClass("Мебель для кухни", "Столы")));
                    add(Map.of("Барные стулья", new InnerClass("Мебель для кухни", "Барные стулья")));
                    add(Map.of("Компьютерные кресла", new InnerClass("Мебель для детской комнаты", "Компьютерные кресла")));
                    add(Map.of("Диваны", new InnerClass("Мебель для гостиной", "Диваны")));
                    add(Map.of("Стулья", new InnerClass("Мебель для гостиной", "Стулья и кресла")));
                    add(Map.of("Стулья и кресла", new InnerClass("Мебель для гостиной", "Стулья и кресла")));
                }
            }),

            Map.entry("Halmar", new ArrayList<Map<String, InnerClass>>() {
                {
                    add(Map.of("Кровати", new InnerClass("Мебель для спальни", "Кровати")));
                    add(Map.of("Тумбы", new InnerClass("Мебель для спальни", "Тумбы")));
                    add(Map.of("Шкафы, стеллажи", new InnerClass("Мебель для спальни", "Шкафы, стеллажи")));
                    add(Map.of("Вешалки", new InnerClass("Мебель для прихожей", "Вешалки")));
                    add(Map.of("Прихожие", new InnerClass("Мебель для прихожей", "Прихожие")));
                    add(Map.of("Столы", new InnerClass("Мебель для кухни", "Обеденные столы и группы")));
                    add(Map.of("Компьютерные кресла", new InnerClass("Мебель для детской комнаты", "Компьютерные кресла")));
                    add(Map.of("Диваны", new InnerClass("Мебель для гостиной", "Диваны")));
                    add(Map.of("Комплекты", new InnerClass("Мебель для гостиной", "Гостиные комплекты")));
                    add(Map.of("Кресла", new InnerClass("Мебель для гостиной", "Стулья и кресла")));
                    add(Map.of("Стулья и кресла", new InnerClass("Мебель для гостиной", "Стулья и кресла")));
                    add(Map.of("Ширмы", new InnerClass("Сопутствующие товары для дома", "Ширмы")));


                }
            }),
            Map.entry("MD", new ArrayList<Map<String, InnerClass>>() {
                {

                    add(Map.of("Комоды", new InnerClass("Мебель для спальни", "Комоды")));
                    add(Map.of("Кровати", new InnerClass("Мебель для спальни", "Кровати")));
                    add(Map.of("Модульные спальни", new InnerClass("Мебель для спальни", "Модульные спальни")));
                    add(Map.of("Ортопедические основания", new InnerClass("Мебель для спальни", "Ортопедические основания")));
                    add(Map.of("Спальные гарнитуры", new InnerClass("Мебель для спальни", "Спальные гарнитуры")));
                    add(Map.of("Туалетные столики", new InnerClass("Мебель для спальни", "Туалетные столики")));
                    add(Map.of("Тумбы", new InnerClass("Мебель для спальни", "Тумбы")));
                    add(Map.of("Угловые шкафы", new InnerClass("Мебель для спальни", "Угловые шкафы")));
                    add(Map.of("Шкафы", new InnerClass("Мебель для спальни", "Шкафы")));
                    add(Map.of("Шкафы-купе", new InnerClass("Мебель для спальни", "Шкафы-купе")));
                    add(Map.of("Шкафы-пеналы", new InnerClass("Мебель для спальни", "Шкафы-пеналы")));
                    add(Map.of("Шкафы, стеллажи", new InnerClass("Мебель для спальни", "Шкафы, стеллажи")));
                    add(Map.of("Вешалки", new InnerClass("Мебель для прихожей", "Вешалки")));
                    add(Map.of("Зеркала", new InnerClass("Мебель для прихожей", "Зеркала")));
                    add(Map.of("Модульные прихожие", new InnerClass("Мебель для прихожей", "Модульные прихожие")));
                    add(Map.of("Прихожие", new InnerClass("Мебель для прихожей", "Прихожие")));
                    add(Map.of("Кухни", new InnerClass("Мебель для кухни", "Кухни")));
                    add(Map.of("Кухонные диваны, уголки", new InnerClass("Мебель для кухни", "Кухонные диваны, уголки")));
                    add(Map.of("Кухонные столы", new InnerClass("Мебель для кухни", "Кухонные столы")));
                    add(Map.of("Обеденные столы и группы", new InnerClass("Мебель для кухни", "Обеденные столы и группы")));
                    add(Map.of("Сопутствующие товары для кухни", new InnerClass("Мебель для кухни", "Сопутствующие товары для кухни")));
                    add(Map.of("Стулья для кухни", new InnerClass("Мебель для кухни", "Стулья для кухни")));
                    add(Map.of("Барные стулья", new InnerClass("Мебель для кухни", "Барные стулья")));
                    add(Map.of("Компьютерные столы", new InnerClass("Мебель для детской комнаты", "Компьютерные столы")));
                    add(Map.of("Модульная детская мебель", new InnerClass("Мебель для детской комнаты", "Модульная детская мебель")));
                    add(Map.of("Диваны", new InnerClass("Мебель для гостиной", "Диваны")));
                    add(Map.of("Журнальные столики", new InnerClass("Мебель для гостиной", "Журнальные столики")));
                    add(Map.of("Стенки", new InnerClass("Мебель для гостиной", "Стенки")));
                    add(Map.of("Гамаки", new InnerClass("Сопутствующие товары для дома", "Гамаки")));
                    add(Map.of("Декор для дома", new InnerClass("Сопутствующие товары для дома", "Декор для дома")));
                    add(Map.of("Разное", new InnerClass("Сопутствующие товары для дома", "Разное")));
                    add(Map.of("Полки", new InnerClass("Сопутствующие товары для дома", "Полки")));
                    add(Map.of("Детские качели", new InnerClass("Сопутствующие товары для дома", "Детские качели")));

                }
            }),
            Map.entry("Собственное производство", new ArrayList<Map<String, InnerClass>>() {
                {
                    add(Map.of("Кухни", new InnerClass("Мебель для кухни", "Кухни")));
                }
            }),

            Map.entry("Монтанья", new ArrayList<Map<String, InnerClass>>() {
                {
                    add(Map.of("Кухни", new InnerClass("Мебель для кухни", "Кухни")));
                }
            })
    );


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


    public static boolean isProductTypeVorbidden(String brand, String testCategory, String testSubcategory, String name) {
        for (String productExceptionItem : PRODUCT_NAMES_EXCEPTION_PATTERN_LIST) {
            if (testCategory.toLowerCase().indexOf(productExceptionItem.toLowerCase()) != -1
                    || testSubcategory.toLowerCase().indexOf(productExceptionItem.toLowerCase()) != -1
                    || name.toLowerCase().indexOf(productExceptionItem.toLowerCase()) != -1)
                return true;
        }

        ArrayList<InnerClass> allExceptions = new ArrayList();
        if (BRAND_EXCEPTIONS_MAP.get(brand) != null && !BRAND_EXCEPTIONS_MAP.get(brand).isEmpty())
            allExceptions.addAll(BRAND_EXCEPTIONS_MAP.get(brand));
        if (!BRAND_EXCEPTIONS_MAP.get("all").isEmpty())
            allExceptions.addAll(BRAND_EXCEPTIONS_MAP.get("all"));

        for (InnerClass exceptionsItem : allExceptions) {
            if (exceptionsItem.categoryName.toLowerCase().equals(testCategory.toLowerCase()) &&
                    exceptionsItem.subcategoryName.toLowerCase().equals(testSubcategory.toLowerCase()))
                return true;

        }

        return false;
    }

    public static boolean isProductTypeVorbidden(String brand, String testCategory, String testSubcategory) {
        return isProductTypeVorbidden(brand, testCategory, testSubcategory, "");
    }

    /**
     * @param brand Vendor,CategoryName,SubcategoryName
     * @return
     */
    public static InnerClass getSiteCategoryNames(String brand, String category, String subcategory, String name) {
        //TODO: returns names according to the exceptions

        //check names classification

        String tmpSubcategory = "", tmpCategory = "";
        String[] names = null;
        ArrayList<String> tmp = new ArrayList();
        String theFirstNameInList = "";
        for (Map<String, InnerClass> entry : PRODUCT_NAME_CLASSIFICATION_RULES) {
            tmp.addAll(Arrays.asList((entry).entrySet().iterator().next().getKey().toUpperCase().split(",")));

        }

        Collections.sort(tmp, (a, b) -> Integer.compare(b.length(), a.length()));

        for (String classificationName : tmp) {
            if (name.toUpperCase().indexOf(classificationName.toUpperCase()) != -1) {
                theFirstNameInList = classificationName;
                break;
            }
        }
        for (Map<String, InnerClass> entry : PRODUCT_NAME_CLASSIFICATION_RULES) {
            names = (entry).entrySet().iterator().next().getKey().toUpperCase().split(",");
            for (String classificationName : names)
                if (classificationName.equalsIgnoreCase((theFirstNameInList))) {
                    return new InnerClass((entry).entrySet().iterator().next().getValue().categoryName, (entry).entrySet().iterator().next().getValue().subcategoryName);
                }
        }


        ArrayList<Map<String, InnerClass>> categoryForBrand;
        categoryForBrand = CLASSIFICATION_RULES.get(brand);
        if (categoryForBrand == null || categoryForBrand.equals("") || categoryForBrand.isEmpty()) {
            System.err.println("classification error for brand: brand - " + brand + ", category - " + category + ", subcategory - " + subcategory + ", name: " + name);
            return new InnerClass("", "");
        }

        for (Map<String, InnerClass> data : categoryForBrand) {
            if (data.containsKey(category)) {
                return data.get(category);
            }
        }
        System.err.println("classification error for category: brand - " + brand + ", category - " + category + ", subcategory - " + subcategory + ", name: " + name);
        return new InnerClass(category, subcategory);
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
