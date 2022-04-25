package ga.opc.ua;

import com.google.common.collect.ImmutableList;
import ga.opc.ua.methods.Distributor;
import ga.opc.ua.methods.model.Config;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ClientReader implements Client {
    private static final Logger logger = LoggerFactory.getLogger(ClientReader.class);

    private int id; //id потока
    private String nameTable; //имя таблицы
    private String nameColumnGuid; //имя колонки таблицы guid
    private int period; // период выполнения потока

    private static String USER_DB;
    private static String PASS_DB;
    private static String URL_DB;

    public ClientReader() {
    }

    public ClientReader(int id, String nameTable, String nameColumnGuid, int period) {
        this.id = id;
        this.nameTable = nameTable;
        this.nameColumnGuid = nameColumnGuid;
        this.period = period;
    }

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        Distributor distributor = new Distributor();
        Config config = distributor.parse();

        client.connect().get(); //создаем подлючение к серверу opc

        while (true) {
            int count = 1;
            Map<Integer, Map<Integer, String>> mapFullSelect = new HashMap<>();
            Map<Integer, String> mapTagsNamesRead = new HashMap<>();

            Connection connection = DriverManager.getConnection("jdbc:postgresql://172.16.205.51:5432/journal_kovikta", "postgres", "Potok_DU");
            Connection connectionLocalhost = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/journal_kovikta", "postgres", "postgres");

            String sqlSelect = "SELECT id,  hfrpok, inout, guid_masdu_5min, guid_masdu_hours, guid_masdu_day FROM app_info.test_table WHERE hfrpok IS NOT NULL";
            Statement statement = connection.createStatement();
            ResultSet resultSelect = statement.executeQuery(sqlSelect);

            System.out.println("Potok: " + id);
            // зачем разделение на входные и выходные ///переделать
            while (resultSelect.next()) {
                mapTagsNamesRead.put(1, resultSelect.getString("hfrpok"));
                mapTagsNamesRead.put(2, resultSelect.getString("inout"));
                mapTagsNamesRead.put(3, resultSelect.getString(nameColumnGuid));
                mapFullSelect.put(count++, new HashMap<>(mapTagsNamesRead));
            }
            connection.close();

            for (Map.Entry<Integer, Map<Integer, String>> entryExternal : mapFullSelect.entrySet()) {
                List<NodeId> nodeIds = ImmutableList.of(new NodeId(entryExternal.getKey(), entryExternal.getValue().get(1)));   // какой тег будем слушать
                CompletableFuture<DataValue> read = client.readValue(0, TimestampsToReturn.Both, nodeIds.get(0));       // начинаем слушать
                Variant variantValue = read.get().getValue();                                                                   // вытаскиваем value
                logger.info(entryExternal.getValue().get(1) + " -> " + variantValue.getValue());                                 // забиваем лог  Имя_тега -> Value
                entryExternal.getValue().put(4, String.valueOf(variantValue.getValue()));                                       // кладем в нашу мапу Value {1:hfrpok , 2:inout, 3:guid_masdu_5min,4:guid_masdu_1hour, 5:guid_masdu_1day, 6:value}
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String sqlInsert = "INSERT INTO app_info." + nameTable + " (timestamp, " + nameColumnGuid + ",hfrpok, value) VALUES ( ?, ?, ?, ?)";


            for (Map.Entry<Integer, Map<Integer, String>> entryExternal : mapFullSelect.entrySet()) {
                PreparedStatement preparedStatement = connectionLocalhost.prepareStatement(sqlInsert);
                preparedStatement.setTimestamp(1, timestamp);
                preparedStatement.setString(2, entryExternal.getValue().get(3));// guid
                preparedStatement.setString(3, entryExternal.getValue().get(1)); // тег
                preparedStatement.setString(4, entryExternal.getValue().get(4));  // его значение
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            connectionLocalhost.close();

            Thread.sleep(period * 60_000L);
        }
//        future.complete(client);
    }
}