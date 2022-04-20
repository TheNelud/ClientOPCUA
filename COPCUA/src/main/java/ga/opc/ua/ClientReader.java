package ga.opc.ua;

import com.google.common.collect.ImmutableList;
import ga.opc.ua.methods.DistributorJdbc;
import ga.opc.ua.methods.Thread_1hour;
import ga.opc.ua.methods.Thread_5min;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.hibernate.dialect.Ingres9Dialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ClientReader implements Client{
    private static int count = 1 ; //счетчик
    /*одна крутая мапа
    1:{1:hfrpok , 2:inout, 3:guid_masdu_5min,4:guid_masdu_1hour, 5:guid_masdu_1day},
    2:{1:hfrpok , 2:inout, 3:guid_masdu_5min,4:guid_masdu_1hour, 5:guid_masdu_1day}
    ...
    */
    private static final Map<Integer, Map<Integer, String>> mapFullSelect = new HashMap<>();

    //две мапы с {1:hfrpok , 2:inout, 3:guid_masdu_5min,4:guid_masdu_1hour, 5:guid_masdu_1day}
    private static final Map<Integer, String> mapTagsNamesRead = new HashMap<>();
    private static final Map<Integer,String> mapTagsNamesWrite = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(ClientReader.class);

    public static void main(String[] args) throws Exception {
        DistributorJdbc.readConfig(new File("config.xml"));

        ClientReader clientRunner = new ClientReader();
        new ClientRunner(clientRunner).run();

    }

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get(); //создаем подлючение к серверу opc
        DistributorJdbc distributorJdbc = new DistributorJdbc(); //создаем подлючение к бд
        ResultSet resultSelect = distributorJdbc.selectFromBdTags();
        ExecutorService executorService = Executors.newFixedThreadPool(3); //создание пул потоков

        // зачем разделение на входные и выходные
        while(resultSelect.next()){
            if (resultSelect.getString("inout").equals("ВХОД")){
                mapTagsNamesRead.put(1, resultSelect.getString("hfrpok"));
                mapTagsNamesRead.put(2, resultSelect.getString("inout"));
                mapTagsNamesRead.put(3, resultSelect.getString("guid_masdu_5min"));
                mapTagsNamesRead.put(4, resultSelect.getString("guid_masdu_hours"));
                mapTagsNamesRead.put(5, resultSelect.getString("guid_masdu_day"));
                mapFullSelect.put(count++, new HashMap<>(mapTagsNamesRead));

            }else if(resultSelect.getString("inout").equals("ВЫХОД")){
                mapTagsNamesWrite.put(1, resultSelect.getString("hfrpok"));
                mapTagsNamesWrite.put(2, resultSelect.getString("inout"));
                mapTagsNamesWrite.put(3, resultSelect.getString("guid_masdu_5min"));
                mapTagsNamesWrite.put(4, resultSelect.getString("guid_masdu_hours"));
                mapTagsNamesWrite.put(5, resultSelect.getString("guid_masdu_day"));
                mapFullSelect.put(count++, new HashMap<>(mapTagsNamesWrite));

            }
        }

        System.out.println("\n\n\n");
        readServerOpcGuid(client,mapFullSelect);
        System.out.println("\n\n\n");

        for (Map.Entry<Integer, Map<Integer, String>> entry : mapFullSelect.entrySet()){
            System.out.println(entry.getKey() +" : "+ entry.getValue());
        }


        future.complete(client);
    }

    public static void readServerOpcGuid(OpcUaClient client, Map<Integer,Map<Integer,String>> map) throws ExecutionException, InterruptedException {
        for (Map.Entry<Integer, Map<Integer,String>> entryExternal : map.entrySet()){
            List<NodeId> nodeIds = ImmutableList.of(new NodeId(entryExternal.getKey(), entryExternal.getValue().get(1)));   // какой тег будем слушать
            CompletableFuture<DataValue> read = client.readValue(0, TimestampsToReturn.Both, nodeIds.get(0));       // начинаем слушать
            Variant variantValue = read.get().getValue();                                                                   // вытаскиваем value
            logger.info(entryExternal.getValue().get(1) +" -> " + variantValue.getValue());                                 // забиваем лог  Имя_тега -> Value
            entryExternal.getValue().put(6, String.valueOf(variantValue.getValue()));                                       // кладем в нашу мапу Value {1:hfrpok , 2:inout, 3:guid_masdu_5min,4:guid_masdu_1hour, 5:guid_masdu_1day, 6:value}
        }
    }












}

///////////////////////////////////////////////////Рабочий кусок////////////////////////////////////////////////////////
///////////////////////////////////////////////////Читает один выбранный элемент////////////////////////////////////////
//                                                                                                                 /////
//        List<NodeId> nodeIds = ImmutableList.of(new NodeId(2, Constant.TAG)); // Channel1.Device1.Tag1           /////
//        CompletableFuture<DataValue> read = client.readValue(0, TimestampsToReturn.Both, nodeIds.get(0));        /////
//        logger.info("read=" + read.get().getValue().toString()+"Что это?" + read.get().getClass().toString());   /////
//                                                                                                                 /////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
