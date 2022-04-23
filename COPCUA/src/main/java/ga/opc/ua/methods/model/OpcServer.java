package ga.opc.ua.methods.model;

public class OpcServer {
    private String ip;
    private String port;

    public OpcServer(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "OpcServer{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}