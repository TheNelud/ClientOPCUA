package ga.opc.ua;

import com.google.common.collect.ImmutableList;
import ga.opc.ua.methods.Test;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ClientReader implements Client{

    private Map<String, String> mapTypeVariable = new HashMap<>();
    private Map<String, String> map = new HashMap<>();
    private Map<String, String> mapNamespace = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public static void main(String[] args) throws Exception {
        ClientReader clientRunner = new ClientReader();
        new ClientRunner(clientRunner).run();


    }

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {
        client.connect().get();
        Test test =new Test();
        List<String> stringList = test.used();

//        List<NodeId> nodeIds = ImmutableList.of(new NodeId()





        ////////////////////////////////////////////////Рабочий кусок//////////////////////////////////////////////
                                     ///////////////Читает один выбранный элемент///////////////
//        List<NodeId> nodeIds = ImmutableList.of(new NodeId(2, Constant.TAG)); // Channel1.Device1.Tag1
//        CompletableFuture<DataValue> read = client.readValue(0, TimestampsToReturn.Both, nodeIds.get(0));
//        logger.info("read=" + read.get().getValue().toString()+"Что это?" + read.get().getClass().toString());

        future.complete(client);
    }


    private void browseNode(String indent, OpcUaClient client, NodeId browseRoot, String path) {
        try {
            List<? extends UaNode> nodes = client.getAddressSpace().browseNodes(browseRoot);
            for (UaNode node : nodes) {
                node.getBrowseName();
                String id = node.getNodeId().getIdentifier().toString();
                Object obj = node.readAttribute(AttributeId.DataType).getValue().getValue();
                String s;
                String name = node.getBrowseName().getName();

                String source = (path + "." + name).substring(1);
                System.out.println(source + " -> " + id);
                if (obj != null) {
                    s = obj.toString();
                    s = s.substring(s.lastIndexOf("id=") + 3, s.lastIndexOf("}"));
                } else s = "";
                mapTypeVariable.put(source, s);
                mapNamespace.put(source, node.getNodeId().getNamespaceIndex().toString());
                map.put(source, id);

                // recursively browse to children
                browseNode(indent + "  ", client, node.getNodeId(), path + "." + name);
            }
        } catch (UaException e) {
            logger.error("Browsing nodeId={} failed: {}", browseRoot, e.getMessage(), e);
        }
    }
}
