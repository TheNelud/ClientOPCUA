package ga.opc.ua.methods;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Test {
    public static void main(String[] args) throws SQLException {


    }




    /*
    * Это просто неообразимо круто получается типо:
    * 1 : {1:name1
    *     2:surname1}
    * 2 : {1:name2
    *      2:surname2}
    * и тд.*/
    public void perfectMagicMaps() throws SQLException{
        int count = 1;
        Map<Integer, Map<Integer, String>> externalMap = new HashMap<>();
        Map<Integer, String> insideMap = new HashMap<>();
        String sqlSelect = "SELECT hfrpok, inout, guid_masdu_5min, guid_masdu_1hour, guid_masdu_1day FROM app_info.tagname_request WHERE hfrpok IS NOT NULL";
        Connection connectionTest = DriverManager.getConnection("jdbc:postgresql://localhost:5432/journal_kovikta", "postgres", "0000");
        Statement statement = connectionTest.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlSelect);

        while(resultSet.next()){
            insideMap.put(1, resultSet.getString("hfrpok"));
            insideMap.put(2, resultSet.getString("inout"));
            insideMap.put(3, resultSet.getString("guid_masdu_5min"));
            insideMap.put(4, resultSet.getString("guid_masdu_1hour"));
            insideMap.put(5, resultSet.getString("guid_masdu_1day"));
            externalMap.put(count++, new HashMap<>(insideMap));
        }

        for (Map.Entry<Integer, Map<Integer, String>> entryExturnal : externalMap.entrySet()){
            System.out.println(entryExturnal.getKey() + " : " + entryExturnal.getValue());
            for (Map.Entry<Integer, String> entryInside : entryExturnal.getValue().entrySet()){
                System.out.println(entryInside.getKey() + " : " + entryInside.getValue());
            }
        }
        resultSet.close();
        statement.close();
    }


    /*Просто перекладываем изодной таблицы в другую*/
    public void magicSelectInsert() throws SQLException {
        String sqlSelect = "SELECT hfrpok, inout, guid_masdu_5min, guid_masdu_hours, guid_masdu_day FROM app_info.test_table WHERE hfrpok IS NOT NULL";
        String sqlInsert = "INSERT INTO app_info.tagname_request(hfrpok, inout, guid_masdu_5min, guid_masdu_1hour, guid_masdu_1day) VALUES( ?, ?, ?, ?, ?)";
        Connection connectionTest = DriverManager.getConnection("jdbc:postgresql://172.16.205.51:5432/journal_kovikta", "postgres", "Potok_DU");
        Connection connectionLocalhost = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/journal_kovikta", "postgres", "0000");
        System.out.println("Соединение установлено");
        Statement statement = connectionTest.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlSelect);

        while (resultSet.next()) {
            PreparedStatement preparedStatement = connectionLocalhost.prepareStatement(sqlInsert);
            preparedStatement.setString(1,resultSet.getString("hfrpok"));
            preparedStatement.setString(2, resultSet.getString("inout"));
            preparedStatement.setString(3,resultSet.getString("guid_masdu_5min"));
            preparedStatement.setString(4,resultSet.getString("guid_masdu_hours"));
            preparedStatement.setString(5,resultSet.getString("guid_masdu_day"));
            preparedStatement.executeUpdate();;
            preparedStatement.close();
        }

        resultSet.close();
        statement.close();

    }

}
