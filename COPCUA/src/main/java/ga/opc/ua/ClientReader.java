package ga.opc.ua;

import com.google.common.collect.ImmutableList;
import ga.opc.ua.methods.DistributorJdbc;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ClientReader implements Client{
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

        DistributorJdbc distributorJdbc = new DistributorJdbc();
        ResultSet resultSelectTagsNames = distributorJdbc.selectFromBdTags();
        while(resultSelectTagsNames.next()){
            if (resultSelectTagsNames.getString("inout").equals("ВХОД")){
                mapTagsNamesRead.put(resultSelectTagsNames.getInt("id"),resultSelectTagsNames.getString("hfrpok"));
            }else if(resultSelectTagsNames.getString("inout").equals("ВЫХОД")){
                mapTagsNamesWrite.put(resultSelectTagsNames.getInt("id"),resultSelectTagsNames.getString("hfrpok"));
            }
        }

        System.out.println("\n\n\nВходные параметры: ");
        readServerOpc(client,mapTagsNamesRead);

        System.out.println("\n\n\nВыходные параметры: ");
        readServerOpc(client,mapTagsNamesWrite);

        future.complete(client);
    }
        
    public static void readServerOpc(OpcUaClient client, Map<Integer,String> map) throws ExecutionException, InterruptedException {
        for (Map.Entry<Integer, String> entry : map.entrySet()){
            List<NodeId> nodeIds = ImmutableList.of(new NodeId(entry.getKey(), entry.getValue()));                      // какой тег будем слушать
            CompletableFuture<DataValue> read = client.readValue(0, TimestampsToReturn.Both, nodeIds.get(0));   // начинаем слушать
            Variant variantValue = read.get().getValue();                                                               // вытаскиваем value
            logger.info(entry.getValue() +" -> " + variantValue.getValue());                                            // забиваем лог  Имя_тега -> Value
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
