package mnsk.services;

import mnsk.App;
import mnsk.beans.export.ImportNode;
import mnsk.beans.export.ProductImporter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Author: S.Rogachevsky
 * Date: 05.02.22
 * Time: 20:36
 */
public class DBImportService {

    public static String url = "jdbc:mysql://localhost:3306/furniture";
    public static String username = "root";
    public static String password = "admin";
    public static String sourceCurrentDBData = "c:\\work\\shop\\furniture.csv ";


    static void importDataFromFile() {


    }



}
