package ga.opc.ua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestConnectOPC implements Client {
    public static void main(String[] args) throws Exception {
        TestConnectOPC testConnectOPC = new TestConnectOPC();
        new ClientRunner(testConnectOPC).run();

    }

    @Override
    public void run(OpcUaClient client, CompletableFuture<OpcUaClient> future)  {


        while (true){
            try {
                client.connect().get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            System.out.println("\n\nCONNECT\n\n");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            future.complete(client);
        }


//        future.complete(client);

    }
}
