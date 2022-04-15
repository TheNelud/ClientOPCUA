package ga.opc.ua.methods;

import java.sql.*;
import java.util.List;

import static sun.net.www.http.KeepAliveCache.result;

public class Test {

    private static String DRIVER = "org.postgresql.Driver";
    private static String DB_URL = "jdbc:postgresql://172.16.205.51:5432/journal_kovikta";
    private static String USER = "postgres";
    private static String PASS = "Potok_DU";



    public static void main(String[] args) {
        Test test = new Test();
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            test.select(connection);


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> used() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
        String sql ="SELECT * FROM app_info.test_table";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        List<String> stringList = null;
        while(resultSet.next()){
            stringList.add(resultSet.getString("hfrpok");
        }
        resultSet.close();
        statement.close();
        return  stringList;
    }

    private static void select(Connection connection) throws SQLException {
        String sql ="SELECT * FROM app_info.test_table";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()){
            System.out.println(resultSet.getString("hfrpok"));
        }
        resultSet.close();
        statement.close();
    }
}
