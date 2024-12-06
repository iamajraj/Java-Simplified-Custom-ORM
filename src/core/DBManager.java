package core;

import annotations.Column;
import annotations.Id;
import annotations.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;

public class DBManager {
    private static Connection connection = null;

    private static final String url = "jdbc:mysql://localhost:3306/mydb";
    private static final String user = "root";
    private static final String password = "";

    private static final String LOG_MSG = "DB_MANAGER";

    public DBManager(){
        initializeConnection();
    }

    private void initializeConnection(){
        if(connection == null){
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
            }catch (ClassNotFoundException e){
                System.out.println(getLogText("initializeConnection") + e.getMessage());
            }

            try{
                connection = DriverManager.getConnection(url, user, password);
            }catch (SQLException e){
                System.out.println(getLogText("initializeConnection") + e.getMessage());
            }
        }
    }

    public Connection getConnection(){
        return connection;
    }

    public void dropTable(Class<?> tableToDrop){
        if(tableToDrop.isAnnotationPresent(Table.class)){
            Table table = tableToDrop.getAnnotation(Table.class);
            String tableName = table.name().isEmpty() ? tableToDrop.getName() : table.name();
            String query = "DROP TABLE IF EXISTS " + tableName + ";";
            try{
                Statement statement = connection.createStatement();
                statement.execute(query);
            }catch(SQLException e){
                System.out.println(getLogText("dropTable") + e.getMessage());
            }
        }
    }

    public void migrateTables(Class<?>[] classes, boolean dropIfExists, boolean log){
        for(Class<?> _class : classes){
            if(dropIfExists){
                dropTable(_class);
            }

            createTable(_class, log);
        }
    }
    
    public void createTable(Class<?> obj, boolean log){
        String query = parseTable(obj);
        if(!query.isEmpty()){
            try{
                Statement statement = connection.createStatement();
                statement.execute(query);
                if(log){
                    System.out.println(query);
                    System.out.println("annotations.Table has been created");
                }
            }catch (SQLException e){
                System.out.println(getLogText("createTable") + e.getMessage());
            }
        }
    }

    private String parseTable(Class<?> obj){
        StringBuilder query = new StringBuilder("");
        if(obj.isAnnotationPresent(Table.class)){
            query.append("CREATE TABLE ");
            Table table = obj.getAnnotation(Table.class);
            String name = table.name();
            query.append(name).append(" (\n");
            Field[] fields = obj.getDeclaredFields();
            int idx = 0;
            for (Field field : fields){
                idx++;
                String fieldName = field.getName();

                if(field.isAnnotationPresent(Column.class)){
                    Column column = field.getAnnotation(Column.class);
                    if(!column.name().isEmpty()){
                        fieldName = column.name();
                    }
                    if (field.getType().equals(String.class)) {
                        query.append("   ").append(fieldName).append(" VARCHAR(255)");
                    }
                    if(field.getType().equals(int.class)){
                        query.append("   ").append(fieldName).append(" INT");
                    }

                    if(field.isAnnotationPresent(Id.class)){
                        query.append(" PRIMARY KEY AUTO_INCREMENT");
                    }

                    if(fields.length != idx){
                        query.append(",");
                    }
                    query.append("\n");
                }
            }
            query.append(")");
        }
        return query.toString();
    }

    public <T> ArrayList<T> selectAll(Class<T> obj){
        ArrayList<T> objects = new ArrayList<>();
        if(obj.isAnnotationPresent(Table.class)){
            Table table = obj.getAnnotation(Table.class);
            Field[] fields = obj.getDeclaredFields();
            String tableName = table.name().isEmpty() ? obj.getName() : table.name();
            StringBuilder query = new StringBuilder("SELECT * FROM ");
            query.append(tableName).append(";");

            try{
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query.toString());

                while(resultSet.next()){
                    T instance = obj.getDeclaredConstructor().newInstance();

                    for(Field field: fields){
                        if(field.isAnnotationPresent(Column.class)){
                            Column column = field.getAnnotation(Column.class);
                            String fieldName = column.name().isEmpty() ? field.getName() : column.name();
                            if (field.getType().equals(String.class)) {
                                String fieldValue = resultSet.getString(fieldName);
                                field.set(instance, fieldValue);
                            }
                            if(field.getType().equals(int.class)){
                                int fieldValue = resultSet.getInt(fieldName);
                                field.set(instance, fieldValue);
                            }
                        }
                    }

                    objects.add(instance);
                }
            }catch (Exception e){
                System.out.println(getLogText("selectAll") + e.getMessage());
            }
        }

        return objects;
    }

    private String getLogText(String text){
        return LOG_MSG + "." + text + ": ";
    }
}
