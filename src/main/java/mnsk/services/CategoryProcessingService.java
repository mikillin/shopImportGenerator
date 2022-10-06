package mnsk.services;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            "Подцветочница",
            "мойк",
            "вытяжк",
            "подушк",
            "ручка",
            "панель декоративная",
            "колес",
            "подсветка",
            "чехол-простынь",
            "основание",
            "каркас",
            "Спинка стула",
            "Крышка Стола",
            "Подушка",
            "Ручка",
            "Панель декоративная",
            "Колеса",
            "Кровать-машинка",
            "Подсветка дна,фар",
            "Чехол-простынь",
            "Ящик",
            "Подцветочница",
            "Накидка для кровати-домика",
            "Бортик для кровати домика",
            "Бельевой ящик для кровати-домика",
            "Витрина",
            "Крышка стола",
            "Столешница",
            "Рамка для столешницы",
            "Основание для стола",
            "Каркас",
            "Каркас",
            "Спинка",
            "Основание",
            "Стойка",
            "Колеса для компьютерных кресел",
            "Тумба ТВ",
            "для теле",
            "Матрац",
            "Матрас"

    };
    public static final String[] PRODUCT_NAMES_EXCEPTION_COMPLEX_PATTERN_LIST = {

            //ошибки
            "(Набор мебели для гостиной .* (Тумба для телевидеоаппаратуры)) "


    };

    public static final ArrayList<Map<String, InnerClass>> PRODUCT_NAME_CLASSIFICATION_RULES = new ArrayList<Map<String, InnerClass>>() {
        {
            add(Map.of("Спальный гарнитур,Спальня,Набор мебели для спальни,Модульная спальня", new InnerClass("Мебель для спальни", "Спальные гарнитуры")));
            add(Map.of("Стол туалетный,Туалетный стол", new InnerClass("Мебель для спальни", "Туалетные столики")));
            add(Map.of("Кровать,Кровать-диван,Основание кроватное,Без матраца,Ортопедическое основание,Полка для кровати,Полка над кроватью,Кровать двойная,кровать с подъемным механизмом", new InnerClass("Мебель для спальни", "Кровати")));
            add(Map.of("Полка для кровати,Полка над кроватью", new InnerClass("Мебель для спальни", "Полки для кровати")));
            add(Map.of("Комод", new InnerClass("Мебель для спальни", "Комоды")));
            add(Map.of("Тумба прикроватная,Тумбочка прикроватная", new InnerClass("Мебель для спальни", "Тумбы")));
            add(Map.of("Шкаф,Шкаф навесной,Шкаф угловой,Приставка к шкафу,Шкаф с зеркалом,Полки для шкафа,Шкаф Марта-15 Шкаф с зеркалом,Шкаф трехстворчатый,Шкаф двустворчатый,Шкаф пятистворчатый,Шкаф,Надстройка,Шкаф навесной,Полки для шкафа,Приставка к шкафу,Шкаф настенный с зеркалом,Панель с зеркалом", new InnerClass("Мебель для спальни", "Шкафы")));
            add(Map.of("Шкаф-купе", new InnerClass("Мебель для спальни", "Шкафы-купе")));
            add(Map.of("Створки для Пенал,Пенал с зеркалом,Пенал,Пенал открытый,к пеналу", new InnerClass("Мебель для спальни", "Шкафы-пеналы")));
            add(Map.of("Стеллаж", new InnerClass("Мебель для спальни", "Шкафы,стеллажи")));
            add(Map.of("Консоль", new InnerClass("Мебель для спальни", "Консоли")));
            add(Map.of("Антресоль", new InnerClass("Мебель для спальни", "Антресоли")));
            add(Map.of("Угловое окончание", new InnerClass("Мебель для спальни", "Угловые окончания")));

            add(Map.of("Прихожая,Набор мебели для прихожей,Модульная прихожая,Скамья", new InnerClass("Мебель для прихожей", "Прихожие")));
            add(Map.of("Модульная прихожая", new InnerClass("Мебель для прихожей", "Модульные прихожие")));
            add(Map.of("Зеркало,Панель с зеркалом", new InnerClass("Мебель для прихожей", "Зеркала")));
            add(Map.of("Набор мебели для прихожей", new InnerClass("Мебель для прихожей", "Наборы мебели для прихожей")));
            add(Map.of("Вешалка", new InnerClass("Мебель для прихожей", "Вешалки")));
            add(Map.of("Полка для обуви,Тумба для обуви,Тумба,Комплект накладок для Тумбы", new InnerClass("Мебель для прихожей", "Тумбы")));
            add(Map.of("Угловое окончание,для прихожей.*Угловое окончание ", new InnerClass("Мебель для прихожей", "Угловые окончания")));


            add(Map.of("Кухонный гарнитур,Кухня,Кухня линейная,Линейная кухня,Классическая угловая кухня", new InnerClass("Мебель для кухни", "Кухни")));
            add(Map.of("Кухонный уголок,Скамья", new InnerClass("Мебель для кухни", "Кухонные диваны, уголки")));
            add(Map.of("Стол,Стол-рабочий,Стол раздвижной,Стол со стульями,Стол раскладной,Стол-книжка,Стол-трансформер", new InnerClass("Мебель для кухни", "Кухонные столы")));
            add(Map.of("стол обеденный,Обеденная группа", new InnerClass("Мебель для кухни", "Обеденные столы и группы")));
            add(Map.of("Стул,Cтул,Табурет,Табурет обеденный,Стул подъемно-поворотный", new InnerClass("Мебель для кухни", "Стулья для кухни")));
            add(Map.of("Стул барный,Стул полубарный", new InnerClass("Мебель для кухни", "Барные стулья")));
            add(Map.of("Стол барный", new InnerClass("Мебель для кухни", "Барные столы")));
            add(Map.of("Кухонная сушка", new InnerClass("Мебель для кухни", "Кухонные сушки")));

            add(Map.of("Кровать выдвижная,Кровать одинарная,Кровать выдвижная,Кровать двухъярусная,Набор мебели Студент-Люкс,Модульная детская,Детская", new InnerClass("Мебель для детской комнаты", "Наборы детской мебели")));
            add(Map.of("Стол компьютерный,Компьютерный стол", new InnerClass("Мебель для детской комнаты", "Компьютерные столы ")));
            add(Map.of("Кресло компьютерное", new InnerClass("Мебель для детской комнаты", "Компьютерные кресла")));
            add(Map.of("Стол письменный,Письменный стол,Стол 1200,Надстройка на стол,Полка — надстройка,Надстройка для КСТ", new InnerClass("Мебель для детской комнаты", "Письменные столы")));
            add(Map.of("Уголок школьника", new InnerClass("Мебель для детской комнаты", "Уголоки школьника")));
            add(Map.of("Набор для школьников и студентов с рабочим местом", new InnerClass("Мебель для детской комнаты", "Набор для школьников и студентов с рабочим местом")));

            add(Map.of("Диван,Евродиван,Софа", new InnerClass("Мебель для гостиной", "Диваны")));
            add(Map.of("Журнальные столики, Столик журнальный,Стол журнальный,стола журнальны,Стол Квадро,Стол СТЖ,Стол Бруклин", new InnerClass("Мебель для гостиной", "Журнальные столики")));
            add(Map.of("Cтенка,Стенка,Гостиная,Набор мебели для гостиной,Центральная секция,Модульная стенка,Марта-15", new InnerClass("Мебель для гостиной", "Гостиные")));
            add(Map.of("Кресло,Кресло.*подставка,Комплект ножек", new InnerClass("Мебель для гостиной", "Кресла")));
            add(Map.of("Комплект подсветки", new InnerClass("Мебель для гостиной", "Комплекты подсветки")));

            add(Map.of("Гамак", new InnerClass("Сопутствующие товары для дома", "Гамаки")));
            add(Map.of("Декор для дома", new InnerClass("Сопутствующие товары для дома", "Декор для дома")));
            add(Map.of("Полка навесная,Полка,Надстройка,Полка — надстройка", new InnerClass("Сопутствующие товары для дома", "Полки")));
            add(Map.of("Детские качели", new InnerClass("Сопутствующие товары для дома", "Детские качели")));
            add(Map.of("Антресоль", new InnerClass("Сопутствующие товары для дома", "Антресоли")));
            add(Map.of("Ширма", new InnerClass("Сопутствующие товары для дома", "Ширмы")));
            add(Map.of("Шезлонг", new InnerClass("Сопутствующие товары для дома", "Шезлонги")));

        }
    };
    public static final ArrayList<Map<String, InnerClass>> PRODUCT_NAME_CLASSIFICATION_REGEX_RULES = new ArrayList<Map<String, InnerClass>>() {
        {
//            add(Map.of("", new InnerClass("Мебель для спальни", "Спальные гарнитуры")));
//            add(Map.of("", new InnerClass("Мебель для спальни", "Туалетные столики")));
//            add(Map.of("", new InnerClass("Мебель для спальни", "Кровати")));
//            add(Map.of("", new InnerClass("Мебель для спальни", "Полки для кровати")));
            add(Map.of("Комод с .*", new InnerClass("Мебель для спальни", "Комоды")));
//            add(Map.of("", new InnerClass("Мебель для спальни", "Тумбы")));
            add(Map.of("Мебель для гостиной / Набор мебели для гостиной .* (Зеркало)", new InnerClass("Мебель для спальни", "Зеркала")));
            add(Map.of("Шкаф с .*", new InnerClass("Мебель для спальни", "Шкафы")));
//            add(Map.of("", new InnerClass("Мебель для спальни", "Шкафы-купе")));
//            add(Map.of("", new InnerClass("Мебель для спальни", "Шкафы-пеналы")));
//            add(Map.of("", new InnerClass("Мебель для спальни", "Шкафы,стеллажи")));
//
//            add(Map.of("", new InnerClass("Мебель для прихожей", "Прихожие")));
//            add(Map.of("", new InnerClass("Мебель для прихожей", "Модульные прихожие")));
//            add(Map.of("", new InnerClass("Мебель для прихожей", "Наборы мебели для прихожей")));
            add(Map.of("Вешкалка с .*", new InnerClass("Мебель для прихожей", "Вешалки")));
//            add(Map.of("", new InnerClass("Мебель для прихожей", "Тумбы")));
//            add(Map.of("", new InnerClass("Мебель для прихожей", "Угловые окончания")));
//
//
//            add(Map.of("", new InnerClass("Мебель для кухни", "Кухни")));
//            add(Map.of("", new InnerClass("Мебель для кухни", "Кухонные диваны, уголки")));
//            add(Map.of("", new InnerClass("Мебель для кухни", "Кухонные столы")));
//            add(Map.of("", new InnerClass("Мебель для кухни", "Обеденные столы и группы")));
//            add(Map.of("", new InnerClass("Мебель для кухни", "Стулья для кухни")));
//            add(Map.of("", new InnerClass("Мебель для кухни", "Барные стулья")));
//            add(Map.of("", new InnerClass("Мебель для кухни", "Барные столы")));
//            add(Map.of("", new InnerClass("Мебель для кухни", "Кухонные сушки")));
//
//            add(Map.of("", new InnerClass("Мебель для детской комнаты", "Наборы детской мебели")));
//            add(Map.of("", new InnerClass("Мебель для детской комнаты", "Компьютерные столы ")));
//            add(Map.of("", new InnerClass("Мебель для детской комнаты", "Компьютерные кресла")));
//            add(Map.of("", new InnerClass("Мебель для детской комнаты", "Письменные столы")));
//            add(Map.of("", new InnerClass("Мебель для детской комнаты", "Уголоки школьника")));
//            add(Map.of("", new InnerClass("Мебель для детской комнаты", "Набор для школьников и студентов с рабочим местом")));
//
//            add(Map.of("", new InnerClass("Мебель для гостиной", "Диваны")));
//            add(Map.of("", new InnerClass("Мебель для гостиной", "Столы журнальные")));
            add(Map.of("Мебель для гостиной.*Исполнение", new InnerClass("Мебель для гостиной", "Гостиные")));
//            add(Map.of("", new InnerClass("Мебель для гостиной", "Кресла")));
//            add(Map.of("", new InnerClass("Мебель для гостиной", "Комплекты подсветки")));
//            add(Map.of("", new InnerClass("Мебель для гостиной", "Софы")));
//            add(Map.of("", new InnerClass("Мебель для гостиной", "Консоли")));
//            add(Map.of("", new InnerClass("Мебель для гостиной", "Угловые окончания")));
//
//            add(Map.of("", new InnerClass("Сопутствующие товары для дома", "Гамаки")));
//            add(Map.of("", new InnerClass("Сопутствующие товары для дома", "Декор для дома")));
//            add(Map.of("", new InnerClass("Сопутствующие товары для дома", "Полки")));
//            add(Map.of("", new InnerClass("Сопутствующие товары для дома", "Детские качели")));
//            add(Map.of("", new InnerClass("Сопутствующие товары для дома", "Антресоли")));
//            add(Map.of("", new InnerClass("Сопутствующие товары для дома", "Ширмы")));
//            add(Map.of("", new InnerClass("Сопутствующие товары для дома", "Шезлонги")));

        }
    };

    public static final Map<String, ArrayList<Map<String, InnerClass>>> CLASSIFICATION_BRAND_RULES = Map.ofEntries(
            Map.entry("Signal", new ArrayList<Map<String, InnerClass>>() {
                {
                    add(Map.of("Комоды", new InnerClass("Мебель для спальни", "Мебель для спальни")));
                    add(Map.of("Кровати", new InnerClass("Мебель для спальни", "Кровати")));
                    add(Map.of("Шкафы,стеллажи", new InnerClass("Мебель для спальни", "Шкафы,стеллажи")));
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
                    add(Map.of("Шкафы,стеллажи", new InnerClass("Мебель для спальни", "Шкафы,стеллажи")));
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
                    add(Map.of("Шкафы,стеллажи", new InnerClass("Мебель для спальни", "Шкафы,стеллажи")));
                    add(Map.of("Вешалки", new InnerClass("Мебель для прихожей", "Вешалки")));
                    add(Map.of("Зеркала", new InnerClass("Мебель для прихожей", "Зеркала")));
                    add(Map.of("Модульные прихожие", new InnerClass("Мебель для прихожей", "Модульные прихожие")));
                    add(Map.of("Прихожие", new InnerClass("Мебель для прихожей", "Прихожие")));
                    add(Map.of("Кухни", new InnerClass("Мебель для кухни", "Кухни")));
                    add(Map.of("Кухонные диваны,уголки", new InnerClass("Мебель для кухни", "Кухонные диваны, уголки")));
                    add(Map.of("Кухонные столы", new InnerClass("Мебель для кухни", "Кухонные столы")));
                    add(Map.of("Обеденные столы и группы", new InnerClass("Мебель для кухни", "Обеденные столы и группы")));
                    add(Map.of("Сопутствующие товары для кухни", new InnerClass("Мебель для кухни", "Сопутствующие товары для кухни")));
                    add(Map.of("Стулья для кухни", new InnerClass("Мебель для кухни", "Стулья для кухни")));
                    add(Map.of("Барные стулья", new InnerClass("Мебель для кухни", "Барные стулья")));
                    add(Map.of("Компьютерные столы", new InnerClass("Мебель для детской комнаты", "Компьютерные столы")));
                    add(Map.of("Модульная детская мебель", new InnerClass("Мебель для детской комнаты", "Модульная детская мебель")));
                    add(Map.of("Диваны", new InnerClass("Мебель для гостиной", "Диваны")));
                    add(Map.of("Журнальные столики", new InnerClass("Мебель для гостиной", "Столы журнальные")));
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
                    add(new InnerClass("Мебель для кухни", "Кухни"));
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


    //returns -1 in case the pattern is not found in the string
    private static int indexOfRegEx(String strSource, String strRegExPattern) {
        int idx = -1;
        //compile pattern from string
        Pattern p = Pattern.compile(strRegExPattern);
        //create a matcher object
        Matcher m = p.matcher(strSource);
        //if pattern is found in the source string
        if (m.find()) {
            //get the start index using start method of the Matcher class
            idx = m.start();
        }
        return idx;
    }

    public static boolean isProductTypeVorbidden(String name) {

        return isProductTypeVorbidden("", "", "", name);
    }

    public static boolean isProductTypeVorbidden(String brand, String testCategory, String testSubcategory, String name) {

        //check only name
        for (String productExceptionItem : PRODUCT_NAMES_EXCEPTION_PATTERN_LIST) {
            if (name.toLowerCase().indexOf(productExceptionItem.toLowerCase()) != -1)
                return true;
        }

        //check only patterns
        for (String productExceptionItem : PRODUCT_NAMES_EXCEPTION_COMPLEX_PATTERN_LIST) {
            if (indexOfRegEx(name.toLowerCase(), productExceptionItem.toLowerCase()) != -1)
                return true;
        }

        ArrayList<InnerClass> allExceptions = new ArrayList();
        if (BRAND_EXCEPTIONS_MAP.get(brand) != null && !BRAND_EXCEPTIONS_MAP.get(brand).isEmpty())
            allExceptions.addAll(BRAND_EXCEPTIONS_MAP.get(brand));
        if (!BRAND_EXCEPTIONS_MAP.get("all").isEmpty())
            allExceptions.addAll(BRAND_EXCEPTIONS_MAP.get("all"));

        for (InnerClass exceptionsItem : allExceptions) {
            if (exceptionsItem.categoryName.equalsIgnoreCase(testCategory.toLowerCase()) &&
                    exceptionsItem.subcategoryName.equalsIgnoreCase(testSubcategory.toLowerCase()))
                return true;

        }


        return false;
    }

    public static boolean isProductTypeVorbidden(String brand, String testCategory, String testSubcategory) {
        return isProductTypeVorbidden(brand, testCategory, testSubcategory, "");
    }

    public static InnerClass getSiteCategoryNames(String name) {
        return getSiteCategoryNames("", "", "", name);
    }

    /**
     * @param brand Vendor,CategoryName,SubcategoryName
     * @return
     */

    public static InnerClass getSiteCategoryNames(String brand, String category, String subcategory, String name) {
        //TODO: returns names according to the exceptions

        //check names classification

        String[] names = null;
        ArrayList<String> listOfClassificationKeyProperties = new ArrayList();
        String theFirstNameInListHasTheHighetAccuracy = "";


        //go throught regex
        //make a list of all classification names
        for (Map<String, InnerClass> entry : PRODUCT_NAME_CLASSIFICATION_REGEX_RULES) {
            listOfClassificationKeyProperties.addAll(Arrays.asList((entry).entrySet().iterator().next().getKey().toUpperCase().split(",")));

        }

        // the longer name/description the more important it is  and has higher priority fot checking.
        Collections.sort(listOfClassificationKeyProperties, (a, b) -> Integer.compare(b.length(), a.length()));

        //find the first coincidence of and then will take according
        for (String classificationRegex : listOfClassificationKeyProperties) {
            if (indexOfRegEx(name.toUpperCase(), classificationRegex.toUpperCase().trim()) != -1) {
                theFirstNameInListHasTheHighetAccuracy = classificationRegex;
                break;
            }
        }
        //search for the first met name in the list of prioritized names and search relative categories and subcategories
        //todo: doesn't work
        for (Map<String, InnerClass> entry : PRODUCT_NAME_CLASSIFICATION_REGEX_RULES) {
            names = (entry).entrySet().iterator().next().getKey().toUpperCase().split(",");
            for (String classificationName : names)
                if (classificationName.trim().equalsIgnoreCase((theFirstNameInListHasTheHighetAccuracy))) {
                    return new InnerClass((entry).entrySet().iterator().next().getValue().categoryName, (entry).entrySet().iterator().next().getValue().subcategoryName);
                }
        }


        //go through the ordinary words
        listOfClassificationKeyProperties.clear();
        //make a list of all classification names
        for (Map<String, InnerClass> entry : PRODUCT_NAME_CLASSIFICATION_RULES) {
            listOfClassificationKeyProperties.addAll(Arrays.asList((entry).entrySet().iterator().next().getKey().toUpperCase().split(",")));

        }

        // the longer name/description the more important it is  and has higher priority fot checking.
        Collections.sort(listOfClassificationKeyProperties, (a, b) -> Integer.compare(b.length(), a.length()));

        //find the first coincidence of and then will take according
        for (String classificationName : listOfClassificationKeyProperties) {
            if (name.toUpperCase().indexOf(classificationName.toUpperCase().trim()) != -1) {
                theFirstNameInListHasTheHighetAccuracy = classificationName;
                break;
            }
        }
        //search for the first met name in the list of prioritized names and search relative categories and subcategories
        for (Map<String, InnerClass> entry : PRODUCT_NAME_CLASSIFICATION_RULES) {
            names = (entry).entrySet().iterator().next().getKey().toUpperCase().split(",");
            for (String classificationName : names)
                if (classificationName.trim().equalsIgnoreCase((theFirstNameInListHasTheHighetAccuracy))) {
                    return new InnerClass((entry).entrySet().iterator().next().getValue().categoryName, (entry).entrySet().iterator().next().getValue().subcategoryName);
                }
        }


        ArrayList<Map<String, InnerClass>> categoryForBrand;
        categoryForBrand = CLASSIFICATION_BRAND_RULES.get(brand);
        if (categoryForBrand == null || categoryForBrand.equals("") || categoryForBrand.isEmpty()) {
            System.err.println("classification error for brand: brand - " + brand + ",category - " + category + ",subcategory - " + subcategory + ",name: " + name);
            return new InnerClass("", "");
        }

        for (Map<String, InnerClass> data : categoryForBrand) {
            if (data.containsKey(category)) {
                return data.get(category);
            }
        }
        System.err.println("classification error for category: brand - " + brand + ",category - " + category + ",subcategory - " + subcategory + ",name: " + name);
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
