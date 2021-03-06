package ga.opc.ua.methods.model;

import java.util.List;

public class DataBase {
    private String ip;
    private String port;
    private String user;
    private String password;
    private String name;
    private List<SelectTable> selectTableList;

    public DataBase(){};

    public DataBase(String ip, String port, String user, String password, String name, List<SelectTable> selectTableList) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
        this.name = name;
        this.selectTableList = selectTableList;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public List<SelectTable> getSelectTableList() {
        return selectTableList;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DataBase{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", selectTableList=" + selectTableList +
                '}';
    }
}
