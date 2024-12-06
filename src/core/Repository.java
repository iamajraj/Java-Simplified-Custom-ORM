package core;

import annotations.Column;
import annotations.Id;
import annotations.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;

public class Repository <T>{
    private final Class<T> type;
    private final Connection connection;
    private static final String LOG_MSG = "REPO";

    public Repository(Class<T> type, Connection connection){
        this.type = type;
        this.connection = connection;
    }

    public boolean insert(T obj){
        boolean isInserted = false;
        if(type.isAnnotationPresent(Table.class)) {
            StringBuilder query = new StringBuilder("INSERT INTO ");
            Table table = type.getAnnotation(Table.class);
            String tableName = table.name().isEmpty() ? type.getName() : table.name();
            query.append(tableName).append(" (");

            Field[] fields = type.getDeclaredFields();
            int idx = 0;
            StringBuilder valuesQuery = new StringBuilder(" VALUES (");
            for(Field field : fields){
                idx++;
                if(field.isAnnotationPresent(Column.class)){
                    if(field.isAnnotationPresent(Id.class)){
                        continue;
                    }
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column.name().isEmpty() ? field.getName() : column.name();

                    query.append(columnName);
                    valuesQuery.append("?");

                    if(fields.length != idx){
                        query.append(",");
                        valuesQuery.append(", ");
                    }
                }
            }
            query.append(")");
            valuesQuery.append(");");

            query.append(valuesQuery);

            try{
                PreparedStatement preparedStatement = connection.prepareStatement(query.toString());
                int valueIdx = 0;
                for(Field field: obj.getClass().getDeclaredFields()){
                    if(field.isAnnotationPresent(Id.class)){
                        continue;
                    }
                    valueIdx++;
                    if(field.getType().equals(String.class)){
                        String value = (String) field.get(obj);
                        preparedStatement.setString(valueIdx, value);
                    }
                    if(field.getType().equals(int.class)){
                        int value = (int) field.get(obj);
                        preparedStatement.setInt(valueIdx, value);
                    }
                }
                preparedStatement.execute();
                int updateCount = preparedStatement.getUpdateCount();
                isInserted = updateCount > 0;
            }catch (Exception e){
                System.out.println(getLogText("insert") + e.getMessage());
            }
        }

        return isInserted;
    }

    public ArrayList<T> getAll(){
        ArrayList<T> objects = new ArrayList<>();

        if(type.isAnnotationPresent(Table.class)){
            String query = "SELECT * FROM ";
            Table table = type.getAnnotation(Table.class);
            String tableName = table.name().isEmpty() ? type.getName() : table.name();
            query += tableName + ";";

            Field[] fields = type.getDeclaredFields();
            try{
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()){
                    T instance = type.getDeclaredConstructor().newInstance();
                    for(Field field : fields){
                        if(field.isAnnotationPresent(Column.class)){
                            Column column = field.getAnnotation(Column.class);
                            String columnName = column.name().isEmpty() ? field.getName() : column.name();

                            if(field.getType().equals(String.class)){
                                String fieldValue = resultSet.getString(columnName);
                                field.set(instance, fieldValue);
                            }

                            if(field.getType().equals(int.class)){
                                int fieldValue = resultSet.getInt(columnName);
                                field.set(instance, fieldValue);
                            }
                        }
                    }
                    objects.add(instance);
                }
            }catch (Exception e){
                System.out.println(getLogText("getAll") + e.getMessage());
            }
        }

        return objects;
    }

    public int count(){
        int count = 0;
        if(type.isAnnotationPresent(Table.class)) {
            String query = "SELECT count(*) as count FROM ";
            Table table = type.getAnnotation(Table.class);
            String tableName = table.name().isEmpty() ? type.getName() : table.name();
            query += tableName + ";";

            try{
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                resultSet.next();
                count = resultSet.getInt("count");
            }catch (SQLException e){
                System.out.println(getLogText("count") + e.getMessage());
            }
        }

        return count;
    }

    private String getLogText(String text){
        return LOG_MSG + "." + text + ": ";
    }
}
