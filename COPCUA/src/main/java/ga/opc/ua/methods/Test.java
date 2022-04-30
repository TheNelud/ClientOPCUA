package ga.opc.ua.methods;


import ga.opc.ua.methods.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Test {
    public static void main(String[] args) throws SQLException {
        distributor();
    }

    public static void distributor(){
        Distributor distributor = new Distributor();
        Config config = distributor.parse();

        List<DataBase> listDB =  new ArrayList<>(config.getDataBaseList());
        for (DataBase str : listDB){
            System.out.println(str.toString());
            for (SelectTable selectTable : str.getSelectTableList()){
                System.out.println(selectTable.getName());
            }
        }

        String ip = null;
        String port = null;

        List<OpcServer> listOpc = new ArrayList<>(config.getOpcServerList());
        for (OpcServer str : listOpc){
            if (str.getType().equals("master")){
                ip = str.getIp();
                port = str.getPort();
            }
        }
        System.out.println(ip + " : " + port );

        String ch = null;
        List<Clients> listClientMaster = new ArrayList<>(config.getClientsList());
        for (Clients str : listClientMaster){
            if (Integer.parseInt(str.getId()) == 1 )
                System.out.println(str.getId() + " : " + str.getNameTable());
        }
        System.out.println(ch);

        System.out.println(config.toString());

    }


    /*
    * Это просто невообразимо круто получается типо:
    * 1 : {1:name1
    *     2:surname1}
    * 2 : {1:name2
    *      2:surname2}
    * и тд.*/
    public static void perfectMagicMaps() throws SQLException{
        int count = 1;
        Map<Integer, Map<Integer, String>> externalMap = new HashMap<>();

        Map<Integer, String> insideMapIn = new HashMap<>();
        Map<Integer, String> insideMapOut = new HashMap<>();

        String sqlSelect = "SELECT hfrpok, inout, guid_masdu_5min, guid_masdu_1hour, guid_masdu_1day FROM app_info.tagname_request WHERE hfrpok IS NOT NULL";
        Connection connectionTest = DriverManager.getConnection("jdbc:postgresql://localhost:5432/journal_kovikta", "postgres", "0000");
        Statement statement = connectionTest.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlSelect);

        while(resultSet.next()){
            if (resultSet.getString("inout").equals("ВХОД")){
                insideMapIn.put(1, resultSet.getString("hfrpok"));
                insideMapIn.put(2, resultSet.getString("inout"));
                insideMapIn.put(3, resultSet.getString("guid_masdu_5min"));
                insideMapIn.put(4, resultSet.getString("guid_masdu_1hour"));
                insideMapIn.put(5, resultSet.getString("guid_masdu_1day"));
                externalMap.put(count++, new HashMap<>(insideMapIn));
            }else if (resultSet.getString("inout").equals("ВЫХОД")){
                insideMapOut.put(1, resultSet.getString("hfrpok"));
                insideMapOut.put(2, resultSet.getString("inout"));
                insideMapOut.put(3, resultSet.getString("guid_masdu_5min"));
                insideMapOut.put(4, resultSet.getString("guid_masdu_1hour"));
                insideMapOut.put(5, resultSet.getString("guid_masdu_1day"));
                externalMap.put(count++, new HashMap<>(insideMapOut));
            }


        }

        for (Map.Entry<Integer, Map<Integer, String>> entryExturnal : externalMap.entrySet()){
            System.out.println(entryExturnal.getValue().get(1));
//            for (Map.Entry<Integer, String> entryInside : entryExturnal.getValue().entrySet()){
//                System.out.println(entryInside.getValue());
//            }
        }
        resultSet.close();
        statement.close();
    }


    /*Просто перекладываем из одной таблицы в другую*/
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
