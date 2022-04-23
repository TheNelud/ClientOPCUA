package ga.opc.ua.methods.model;

import java.util.List;

public class Config {
    private List<DataBase> dataBaseList;
    private List<OpcServer> opcServerList;
    private List<Clients> clientsList;

    public List<DataBase> getDataBaseList() {
        return dataBaseList;
    }

    public void setDataBaseList(List<DataBase> dataBaseList) {
        this.dataBaseList = dataBaseList;
    }

    public List<OpcServer> getOpcServerList() {
        return opcServerList;
    }

    public void setOpcServerList(List<OpcServer> opcServerList) {
        this.opcServerList = opcServerList;
    }

    public List<Clients> getClientsList() {
        return clientsList;
    }

    public void setClientsList(List<Clients> clientsList) {
        this.clientsList = clientsList;
    }

    @Override
    public String toString() {
        return "Config{" +
                "dataBaseList=" + dataBaseList +
                ", opcServerList=" + opcServerList +
                ", clientsList=" + clientsList +
                '}';
    }
}
