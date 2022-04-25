package ga.opc.ua;

import ga.opc.ua.methods.Distributor;
import ga.opc.ua.methods.model.Config;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TestConnectOPC implements Client {
    public static void main(String[] args) throws Exception {
        TestConnectOPC testConnectOPC = new TestConnectOPC();
        new ClientRunner(testConnectOPC).run();

    }

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception {

        client.connect().get(); //создаем подлючение к серверу opc

        System.out.println("\n\nCONNECT\n\n");

        future.complete(client);

    }
}
