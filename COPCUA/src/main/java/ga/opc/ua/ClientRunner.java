package ga.opc.ua;

import ga.opc.ua.methods.Distributor;
import ga.opc.ua.methods.model.Config;
import ga.opc.ua.methods.model.OpcServer;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.client.security.DefaultClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class ClientRunner implements Runnable {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

//    private final Logger logger = LoggerFactory.getLogger(java.io.FileReader.class);
     private final Logger logger = Logger.getLogger(FileReader.class);

    private final CompletableFuture<OpcUaClient> future = new CompletableFuture<>();

    private final Client client;

    private DefaultTrustListManager trustListManager;

    private String ip, port; //read conf.xml

    private final Distributor distributor = new Distributor();
    private final Config config = distributor.parse();
    private final List<OpcServer> listOpc = new ArrayList<>(config.getOpcServerList());

    private int counter_exit;



    public ClientRunner(Client client) throws Exception{
        this.client = client;
    }

    public ClientRunner(Client client, String ip , String port){
        this.client = client;
        this.ip = ip;
        this.port = port;
    }

    private OpcUaClient createClient(String ip , String port) throws Exception {
        Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "client", "security");
        Files.createDirectories(securityTempDir);

        if (!Files.exists(securityTempDir)) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }

        File pkiDir = securityTempDir.resolve("pki").toFile();

        trustListManager = new DefaultTrustListManager(pkiDir);

        DefaultClientCertificateValidator certificateValidator =
                new DefaultClientCertificateValidator(trustListManager);

        return OpcUaClient.create(
                client.getEndpointUrl(ip, port),
                endpoints ->
                        endpoints.stream()
                                .findFirst(),
                configBuilder ->
                        configBuilder
                                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                                .setApplicationUri("urn:eclipse:milo:examples:client")
                                .setCertificateValidator(certificateValidator)
                                .setIdentityProvider(client.getIdentityProvider())
                                .setRequestTimeout(uint(5000))
                                .build()
        );
    }


    /** ?????????????????????? ?? ??????????????, ?? ?????????????????????? ?????? ??????????
     * 1. ?????????????????? ?? ?????????????? ???????? ?? ???????? ??????????????
     * 2. ?????????????? ??????????????
     * 3. ???????? ?????? ????, ???? ????????????, ?????????????????? ???????????? ...
     * 4. ???????? ??????????????, ?????????????????? ???????????????????? ????????????
     * 5. ???????? ?? ?? ???????????? ?????? ???????? ??????????????, ?????????????? ???????????????? ?? ???????? ???????????? ?????? ????????
     * 6. ???????????????????? ?????? ???? ???????????? ?? ??????????????*/


    @Override
    public void run() {
        /**???????????????? ?????????? ???????????????????? ???????????? ??????????*/
        future.whenComplete((clientUA, ex) -> {
            if (clientUA != null) {
                try {
                    clientUA.disconnect().get();
                    Stack.releaseSharedResources();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error disconnecting: "+ e);
                }
            } else {
                System.err.println("Error running:" +  ex.getMessage());//this
                logger.error("ERROR running: {}"+ ex);
                Stack.releaseSharedResources();
            }
            try {
                Thread.sleep(1000);
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        connectToServer("master");

        try {
            Thread.sleep(999_999_999);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void connectToServer(String role){
        for (OpcServer str : listOpc){
            if (str.getType().equals(role)){
                ip = str.getIp();
                port = str.getPort();
            }
        }
        System.err.println("Connecting to the "+ role +" server.");
        logger.info("Connecting to the " + role +" server.");

        OpcUaClient clientUA = null;

        try {
            clientUA = createClient(ip, port);
            try {
                client.run(clientUA, future);
                logger.info("Connecting to the " + role + " : successfulle");
                future.get(10, TimeUnit.DAYS);
            } catch (Throwable t) {
                logger.error("Error running client: " +  t);
                future.complete(clientUA);
            }

        } catch (Throwable t) {
            logger.error(t.getMessage());
//            System.err.println("Error: "+t.getMessage());
            counter_exit++;
            if (counter_exit == 2)
                future.completeExceptionally(t);
            connectToServer("slave");

        }

    }
}
