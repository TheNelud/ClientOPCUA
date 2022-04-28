package ga.opc.ua;

import ga.opc.ua.methods.Distributor;
import ga.opc.ua.methods.model.Config;
import ga.opc.ua.methods.model.OpcServer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.client.security.DefaultClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

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

        LoggerFactory.getLogger(getClass())
                .info("security dir: {}", securityTempDir.toAbsolutePath());
        LoggerFactory.getLogger(getClass())
                .info("security pki dir: {}", pkiDir.getAbsolutePath());

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


    /** подключение к серверу, в зависимости кто живой
     * 1. Считываем с конфига айпи и порт сервера
     * 2. Создаем клиента
     * 3. Если все ок, то ништяк, запускаем клиент ...
     * 4. Если неочень, запускаем резервного чувака
     * 5. Если и у чувака все тоже неочень, пробуем потыкать в него палкой три раза
     * 6. Записываем что за ошибка и выходим*/


    @Override
    public void run() {
        /**Проверка когда резервному чуваку плохо*/
        future.whenComplete((clientUA, ex) -> {
            if (clientUA != null) {
                try {
                    clientUA.disconnect().get();
                    Stack.releaseSharedResources();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error disconnecting:", e.getMessage(), e);
                }
            } else {
                System.err.println("Error running:" +  ex.getMessage());//this
                logger.error("ERROR running: {}", ex.getMessage(), ex);
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
        System.err.println("Connect in "+ role);
        OpcUaClient clientUA = null;

        try {
            clientUA = createClient(ip, port);
            try {
                client.run(clientUA, future);
                future.get(10, TimeUnit.DAYS);
            } catch (Throwable t) {
                logger.error("Error running client example: {}", t.getMessage(), t);
                future.complete(clientUA);
            }

        } catch (Throwable t) {
            System.err.println("Error: "+t.getMessage());
            counter_exit++;
            if (counter_exit == 3)
                future.completeExceptionally(t);
            connectToServer("slave");

        }

    }
}
