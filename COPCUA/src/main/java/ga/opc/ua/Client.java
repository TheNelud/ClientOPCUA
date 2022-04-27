package ga.opc.ua;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;

import java.util.concurrent.CompletableFuture;

public interface Client {
    default String getEndpointUrl(String ip, String port) {
        return "opc.tcp://"+ip+":"+port;
    }


    default SecurityPolicy getSecurityPolicy() {
        return SecurityPolicy.None;
    }

    default IdentityProvider getIdentityProvider() {
        return new AnonymousProvider();
    }

    void run(OpcUaClient client, CompletableFuture<OpcUaClient> future) throws Exception;
}
