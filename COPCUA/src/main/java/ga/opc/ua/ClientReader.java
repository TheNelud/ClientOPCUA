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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ClientReader implements Client{
    private static final Map<Integer, String> mapTagsNamesRead = new HashMap<>();
    private static final Map<Integer,String> mapTagsNamesWrite = new HashMap<>();
    //временно пока не стану умней
    private static final Map<Integer,String> mapGui5min = new HashMap<>();
    private static final Map<Integer,String> mapGui1hour= new HashMap<>();
    private static final Map<Integer,String> mapGui1day = new HashMap<>();

    private static final Map<String, String> mapTagAndValue = new HashMap<>(); //для хранения имени тега и его значения
    private static final Logger logger = LoggerFactory.getLogger(ClientReader.class);

    public static void main(String[] args) throws Exception {
        DistributorJdbc.readConfig(new File("config.xml"));

        ClientReader clientRunner = new ClientReader();
        new ClientRunner(clientRunner).run();

    }

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get(); //создаем подлючение к серверу opc

        DistributorJdbc distributorJdbc = new DistributorJdbc();
        ResultSet resultSelectTagsNames = distributorJdbc.selectFromBdTags();

        ExecutorService executorService = Executors.newFixedThreadPool(3); //создание пул потоков

        while(resultSelectTagsNames.next()){
            if (resultSelectTagsNames.getString("inout").equals("ВХОД")){
                mapTagsNamesRead.put(resultSelectTagsNames.getInt("id"),resultSelectTagsNames.getString("hfrpok"));
                mapGui5min.put(resultSelectTagsNames.getInt("id"), resultSelectTagsNames.getString("guid_masdu_5min"));
                mapGui1hour.put(resultSelectTagsNames.getInt("id"), resultSelectTagsNames.getString("guid_masdu_hours"));
                mapGui1day.put(resultSelectTagsNames.getInt("id"), resultSelectTagsNames.getString("guid_masdu_day"));
            }else if(resultSelectTagsNames.getString("inout").equals("ВЫХОД")){
                mapTagsNamesWrite.put(resultSelectTagsNames.getInt("id"),resultSelectTagsNames.getString("hfrpok"));
                mapGui5min.put(resultSelectTagsNames.getInt("id"), resultSelectTagsNames.getString("guid_masdu_5min"));
                mapGui1hour.put(resultSelectTagsNames.getInt("id"), resultSelectTagsNames.getString("guid_masdu_hours"));
                mapGui1day.put(resultSelectTagsNames.getInt("id"), resultSelectTagsNames.getString("guid_masdu_day"));
            }
        }

        //запихнуть в поток
        while (true) {
            System.out.println("\n\n\nВходные параметры: ");
            readServerOpc(client,mapTagsNamesRead);

            System.out.println("\n\n\nВыходные параметры: ");
            readServerOpc(client,mapTagsNamesWrite);

            for(Map.Entry<String, String> entry : mapTagAndValue.entrySet()){
//                distributorJdbc.insertInDb1day();
//                distributorJdbc.insertInDb1hour();
//                distributorJdbc.insertInDb5min();

                distributorJdbc.insertFromDBTags(entry.getKey(), entry.getValue());

            }


        }

//        future.complete(client);
    }

    public static void readServerOpc(OpcUaClient client, Map<Integer,String> map) throws ExecutionException, InterruptedException {
        for (Map.Entry<Integer, String> entry : map.entrySet()){
            List<NodeId> nodeIds = ImmutableList.of(new NodeId(entry.getKey(), entry.getValue()));                      // какой тег будем слушать
            CompletableFuture<DataValue> read = client.readValue(0, TimestampsToReturn.Both, nodeIds.get(0));   // начинаем слушать
            Variant variantValue = read.get().getValue();                                                               // вытаскиваем value
            logger.info(entry.getValue() +" -> " + variantValue.getValue());                                            // забиваем лог  Имя_тега -> Value
            mapTagAndValue.put(entry.getValue(), (String) variantValue.getValue());
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
