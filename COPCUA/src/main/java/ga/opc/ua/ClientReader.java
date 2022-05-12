package ga.opc.ua;

import com.google.common.collect.ImmutableList;
import ga.opc.ua.methods.Distributor;
import ga.opc.ua.methods.model.Config;
import ga.opc.ua.methods.model.DataBase;
import ga.opc.ua.methods.model.SelectTable;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ClientReader implements Client {
    private static final Logger logger = LoggerFactory.getLogger(java.io.FileReader.class);

    private final int id; //id потока
    private final String nameTable; //имя таблицы
    private final String nameColumnGuid; //имя колонки таблицы guid
    private final int period; // номер выполнения потока

    private final Distributor distributor = new Distributor();
    private final Config config = distributor.parse();
    private final List<DataBase> listDB =  new ArrayList<>(config.getDataBaseList());

    private final Map <Integer, Map<Integer, String>> fullValueMap = new HashMap<>();
    private final Map <Integer, String> noFullValueMap = new HashMap<>();

    private final Map<Integer, Map<Integer, String>> mapFullSelect = new HashMap<>();
    private final Map<Integer, String> mapTagsNamesRead = new HashMap<>();

    private final String valueStr =  "0.0"; // default value if is empty

    private final String sqlSelect = null;
    private Statement statement = null;
    private Connection connection = null;
    private Connection connectionLocalHost = null;





    public ClientReader(int id, String nameTable, String nameColumnGuid, int period) {
        this.id = id;
        this.nameTable = nameTable;
        this.nameColumnGuid = nameColumnGuid;
        this.period = period;
    }

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {

        client.connect().get(); //создаем подлючение к серверу opc

        while (true) {

            System.out.println("Potok: " + id);
            logger.info("Create client - "+ id + " : successfully");


            int count = 1;
            for (Map.Entry<Integer, Map<Integer, String>> entryExternal : sqlSelect().entrySet()) {
                List<NodeId> nodeIds = ImmutableList.of(new NodeId(entryExternal.getKey(), entryExternal.getValue().get(1)));   // какой тег будем слушать
                CompletableFuture<DataValue> read = client.readValue(0, TimestampsToReturn.Both, nodeIds.get(0));       // начинаем слушать
                Variant variantValue = read.get().getValue();                                                                   // вытаскиваем value
                logger.info(entryExternal.getValue().get(1) + " -> " + variantValue.getValue());                                 // забиваем лог  Имя_тега -> Value
                entryExternal.getValue().put(4, String.valueOf(variantValue.getValue()));                                       // кладем в нашу мапу Value {1:hfrpok , 2:inout, 3:guid_masdu_ ,4:value}
                //_________________________________________________________________________//
                noFullValueMap.put(0, entryExternal.getValue().get(1));
                noFullValueMap.put(1, String.valueOf(variantValue.getValue()));
                fullValueMap.put(count++, new HashMap<>(noFullValueMap));
            }
            sqlInsert(fullValueMap);
            Thread.sleep(period * 60_000L);
        }
//        future.complete(client);
    }


    /** Фомирование входный данных с бд
     * 1. Создаем мапы для дальнейшего использования с опс сервером
     * 2. Из конфига вытаскиваем параметры и создаем подключене к бд и создаем sql - запрос
     * 3. Полученый select кладем в мапу
     * 4. Закрываем поключение в бд , чтоб невисело
     * 5. Возвращаем мапу для дальнейшей работы с ней
     */

    private Map<Integer, Map<Integer, String>> sqlSelect() {
//        Map<Integer, Map<Integer, String>> mapFullSelect = new HashMap<>();
//        Map<Integer, String> mapTagsNamesRead = new HashMap<>();
        int count = 1;
        String sqlSelect = null;
//        Statement statement = null;
//        Connection connection = null;
        for (DataBase dataBase : listDB) {
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://" + dataBase.getIp() + ":" + dataBase.getPort() + "/" + dataBase.getName(), dataBase.getUser(), dataBase.getPassword());
                statement = connection.createStatement();
                logger.info("Connection in database: successfully");
            } catch (SQLException e) {
                logger.error("Error in connection: " +e.getSQLState() + e.getMessage());
//                System.err.println("Error in connection: " + e.getSQLState() + e.getMessage());
            }
            for (SelectTable selectTable : dataBase.getSelectTableList()) {
                sqlSelect = "SELECT " + selectTable.getColumnId() + ",  " +
                        selectTable.getColumnTag() + ", " +
                        selectTable.getColumnInout() + ", " +
                        selectTable.getColumnGuid1() + ", " +
                        selectTable.getColumnGuid2() + ", " +
                        selectTable.getColumnGuid3() + " FROM " +
                        selectTable.getName() + " WHERE " +
                        selectTable.getColumnTag() + " IS NOT NULL";
            }
        }
        try {
            ResultSet resultSelect = statement.executeQuery(sqlSelect);
            while (resultSelect.next()) {
                mapTagsNamesRead.put(1, resultSelect.getString("hfrpok"));
                mapTagsNamesRead.put(2, resultSelect.getString("inout"));
                mapTagsNamesRead.put(3, resultSelect.getString(nameColumnGuid));
                mapFullSelect.put(count++, new HashMap<>(mapTagsNamesRead));
            }
        } catch (SQLException e) {
            logger.error("Error in request: " +e.getSQLState() + e.getMessage());
            System.err.println("Error in request: " + e.getSQLState() + e.getMessage());
        }

        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.error("Error in close: " +e.getSQLState() + e.getMessage());
//            System.err.println("Error in close "+ e.getMessage() + e.getSQLState());
        }

        return mapFullSelect;
    }

    /**Метод для записи пришедшей памы с данными в базу данных*/
    private void sqlInsert(Map<Integer, Map <Integer, String>> map){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        String sqlInsert = "INSERT INTO app_info.\"5min_params\" (val,timestamp,hfrpok_id) VALUES ( ?, ?, ?);";
        String sqlInsert = "INSERT INTO "+nameTable+" (val,timestamp,hfrpok_id) VALUES ( ?, ?, ?);";

        for (DataBase dataBase : listDB) {
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://" +
                        dataBase.getIp() + ":" +
                        dataBase.getPort() + "/" +
                        dataBase.getName(),
                        dataBase.getUser(),
                        dataBase.getPassword());
                connectionLocalHost = DriverManager.getConnection("jdbc:postgresql://localhost:5432/journal_kovikta", "postgres", "postgres");
                logger.info("Connection in database: successfully");
            } catch (SQLException e) {
                logger.error("Error in connection: " + e.getMessage());
            }
        }
        PreparedStatement preparedStatement = null;
        for (Map.Entry<Integer, Map<Integer, String>> entryExternal : map.entrySet()) {
            try {
                preparedStatement = connectionLocalHost.prepareStatement(sqlInsert);
                preparedStatement.setTimestamp(2, timestamp);
                preparedStatement.setString(3, entryExternal.getValue().get(0)); // тег

                System.err.println("Tag name: "+entryExternal.getValue().get(0)+"\nValue: "+ entryExternal.getValue().get(1));

                if (String.valueOf(entryExternal.getValue().get(1)) == null) {
                    preparedStatement.setDouble(1, Double.parseDouble(entryExternal.getValue().get(1)));
                }else{
                    preparedStatement.setDouble(1, 0); // его значение
                }

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                logger.error("Error in prepare statement: " +e.getSQLState() + e.getMessage());

            }
        }

        try {
            connectionLocalHost.close();
        } catch (SQLException e) {
            logger.error("Error in close: " +e.getSQLState() + e.getMessage());
//            System.err.println("Error in close "+ e.getMessage() + e.getSQLState());
        }



    }
}