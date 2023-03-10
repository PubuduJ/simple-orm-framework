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
            String entityPackage = packagesToScan[i].replaceAll("\\.", "/");
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

        for (String className : classNames) {
            try {
                Class<?> loadedClass = Class.forName(className);
                Table tableAnnotation = loadedClass.getDeclaredAnnotation(Table.class);
                if (tableAnnotation != null) {
                    createTable(loadedClass, connection);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getMessage().concat(" class was not found."));
            }
        }
    }

    private static void createTable(Class<?> classObj, Connection connection) {
        Map<Class<?>, String> supportedTypes = new HashMap<>();
        supportedTypes.put(String.class, "VARCHAR(256)");
        supportedTypes.put(int.class, "INT");
        supportedTypes.put(Integer.class, "INT");
        supportedTypes.put(double.class, "DOUBLE(10,2)");
        supportedTypes.put(Double.class, "DOUBLE(10,2)");
        supportedTypes.put(BigDecimal.class, "DECIMAL(10,2)");
        supportedTypes.put(Date.class, "DATE");
        supportedTypes.put(Time.class, "TIME");

        StringBuilder ddlBuilder = new StringBuilder();
        ddlBuilder.append("CREATE TABLE IF NOT EXISTS `").append(classObj.getSimpleName()).append("` (");

        int primaryKeyCount = 0;
        Field[] fields = classObj.getDeclaredFields();
        for (Field field : fields) {
            String stringField = field.toGenericString();
            if (stringField.contains(" static ")) throw new RuntimeException("static variables cannot be mapped in to table columns");
            String name = field.getName();
            Class<?> fieldType = field.getType();
            PrimaryKey primaryKey = field.getDeclaredAnnotation(PrimaryKey.class);
            if (primaryKey != null) primaryKeyCount++;
            if (!supportedTypes.containsKey(fieldType)) {
                throw new RuntimeException(fieldType + " is not supported.");
            }
            ddlBuilder.append("`").append(name)
                    .append("` ")
                    .append(supportedTypes.get(fieldType));
            if (primaryKey != null) ddlBuilder.append(" PRIMARY KEY,");
            else ddlBuilder.append(",");

        }
        ddlBuilder.deleteCharAt(ddlBuilder.length()-1);
        ddlBuilder.append(");");
        if (primaryKeyCount != 1) throw new RuntimeException(classObj + " should only have one @PrimaryKey annotation.");
        System.out.println(ddlBuilder.toString());

        try {
            connection.createStatement().execute(ddlBuilder.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
