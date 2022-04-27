package ga.opc.ua.methods.model;

public class OpcServer {
    private String type;
    private String ip;
    private String port;

    public OpcServer(){}

    public OpcServer(String type,String ip, String port) {
        this.type = type;
        this.ip = ip;
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "OpcServer{" +
                "type='" + type + '\'' +
                ", ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
