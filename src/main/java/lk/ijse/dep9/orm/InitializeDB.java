package lk.ijse.dep9.orm;

import lk.ijse.dep9.orm.annotation.PrimaryKey;
import lk.ijse.dep9.orm.annotation.Table;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitializeDB {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize(String host,
                                  String port,
                                  String database,
                                  String username,
                                  String password,
                                  String ... packagesToScan) {
        String url = "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true";
        url = String.format(url, host, port, database);
        Connection connection;
        try {
            connection = DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        List<String> classNames = new ArrayList<>();

        for (int i = 0; i < packagesToScan.length; i++) {
            System.out.println(packagesToScan[i]);
            String entityPackage = packagesToScan[i].replaceAll("\\.", "/");
            System.out.println(entityPackage);
            URL resource = InitializeDB.class.getResource("/".concat(entityPackage));
            try {
                File file = new File(resource.toURI());
                String[] list = file.list();
                for (String name : list) {
                    classNames.add(packagesToScan[i].concat(".").concat(name.replace(".class","")));
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        classNames.forEach(System.out::println);

        for (String className : classNames) {
            try {
                Class<?> loadedClass = Class.forName(className);
                Table tableAnnotation = loadedClass.getDeclaredAnnotation(Table.class);
                if (tableAnnotation != null) {
                    createTable(loadedClass, connection);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage().concat(" class was not found!"));
            }
        }
    }

    private static void createTable(Class<?> classObj, Connection connection) {

    }
}
