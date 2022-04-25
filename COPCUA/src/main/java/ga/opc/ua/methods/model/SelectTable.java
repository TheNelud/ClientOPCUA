package ga.opc.ua.methods.model;

public class SelectTable {
    private String name;
    private String columnId;
    private String columnTag;
    private String columnInout;
    private String columnGuid1;
    private String columnGuid2;
    private String columnGuid3;

    public SelectTable(){}

    public SelectTable(String name,
                       String columnId,
                       String columnTag,
                       String columnInout,
                       String columnGuid1,
                       String columnGuid2,
                       String columnGuid3) {
        this.name = name;
        this.columnId = columnId;
        this.columnTag = columnTag;
        this.columnInout = columnInout;
        this.columnGuid1 = columnGuid1;
        this.columnGuid2 = columnGuid2;
        this.columnGuid3 = columnGuid3;
    }

    public String getName() {
        return name;
    }

    public String getColumnId() {
        return columnId;
    }

    public String getColumnTag() {
        return columnTag;
    }

    public String getColumnInout() {
        return columnInout;
    }

    public String getColumnGuid1() {
        return columnGuid1;
    }

    public String getColumnGuid2() {
        return columnGuid2;
    }

    public String getColumnGuid3() {
        return columnGuid3;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public void setColumnTag(String columnTag) {
        this.columnTag = columnTag;
    }

    public void setColumnInout(String columnInout) {
        this.columnInout = columnInout;
    }

    public void setColumnGuid1(String columnGuid1) {
        this.columnGuid1 = columnGuid1;
    }

    public void setColumnGuid2(String columnGuid2) {
        this.columnGuid2 = columnGuid2;
    }

    public void setColumnGuid3(String columnGuid3) {
        this.columnGuid3 = columnGuid3;
    }

    @Override
    public String toString() {
        return "SelectTable{" +
                "name='" + name + '\'' +
                ", columnId='" + columnId + '\'' +
                ", columnTag='" + columnTag + '\'' +
                ", columnInout='" + columnInout + '\'' +
                ", columnGuid1='" + columnGuid1 + '\'' +
                ", columnGuid2='" + columnGuid2 + '\'' +
                ", columnGuid3='" + columnGuid3 + '\'' +
                '}';
    }
}
