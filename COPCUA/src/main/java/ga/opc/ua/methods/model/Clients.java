package ga.opc.ua.methods.model;

public class Clients {
    private String id;
    private String nameTable;
    private String columnGuid;
    private int periodWorker;

    public Clients(String id, String nameTable, String columnGuid, int periodWorker) {
        this.id = id;
        this.nameTable = nameTable;
        this.columnGuid = columnGuid;
        this.periodWorker = periodWorker;
    }

    public String getId() {
        return id;
    }

    public String getNameTable() {
        return nameTable;
    }

    public String getColumnGuid() {
        return columnGuid;
    }

    public int getPeriodWorker() {
        return periodWorker;
    }

    @Override
    public String toString() {
        return "Clients{" +
                "id='" + id + '\'' +
                ", nameTable='" + nameTable + '\'' +
                ", columnGuid='" + columnGuid + '\'' +
                ", periodWorker=" + periodWorker +
                '}';
    }
}
