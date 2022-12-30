package lk.ijse.dep9.orm;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                    classNames.add(packagesToScan[0].concat(".").concat(name.replace(".class","")));
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        classNames.forEach(System.out::println);
    }
}
