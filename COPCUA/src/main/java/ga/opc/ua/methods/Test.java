package ga.opc.ua.methods;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws SQLException {
        Map<Integer, Map<Integer,String>> safeMapZoneExternal = new HashMap<>();
        Map<Integer, String> safeMapZoneInside = new HashMap<>();
        String sqlSelect = "SELECT hfrpok, inout, guid_masdu_5min, guid_masdu_hours, guid_masdu_day FROM app_info.test_table WHERE hfrpok IS NOT NULL";
        Connection connectionTest = DriverManager.getConnection("jdbc:postgresql://172.16.205.51:5432/journal_kovikta","postgres","Potok_DU");
        System.out.println("Соединение установлено");
        Statement statement = connectionTest.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlSelect);
        int i = 1;

        while (resultSet.next()){
            while (safeMapZoneExternal.entrySet().iterator().hasNext()){
                Map.Entry<Integer, Map<Integer, String>> entry = safeMapZoneExternal.entrySet().iterator().next();
                int j = 1;
                safeMapZoneInside.put(j++, resultSet.getString("hfrpok"));
                safeMapZoneInside.put(j++, resultSet.getString("inout"));
                safeMapZoneInside.put(j++, resultSet.getString("guid_masdu_5min"));
                safeMapZoneInside.put(j++, resultSet.getString("guid_masdu_hours"));
                safeMapZoneInside.put(j++, resultSet.getString("guid_masdu_day"));
                safeMapZoneExternal.put(i++, safeMapZoneInside);
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

        }
        resultSet.close();
        statement.close();

//        System.out.println(safeMapZoneInside);

//        for(Map.Entry<Integer, Map<Integer,String>> entry : safeMapZoneExternal.entrySet()){
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }

//        Connection connectionLocalhost = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/journal_kovikta", "postgres", "0000");
//        String sqlInsert = "INSERT INTO app_info.tagname_request(hfrpok, inout, guid_masdu_5min, guid_masdu_1hour, guid_masdu_1day) VALUES( ?, ?, ?, ?, ?)";
//        PreparedStatement preparedStatement = connectionLocalhost.prepareStatement(sqlInsert);
//        for(Map.Entry<Integer, String> entry : safeMapZoneInside.entrySet()){
//            preparedStatement.setString(entry.getKey(), entry.getValue());
//            preparedStatement.executeUpdate();
//        }
//        preparedStatement.close();

    }

}
