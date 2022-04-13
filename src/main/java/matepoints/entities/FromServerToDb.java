package points.entities;

import javax.persistence.*;

@Entity
@Table(name = "from_server_to_db")
public class FromServerToDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "d_time")
    private String dTime;

    @Column(name = "tag_id")
    private String tagId;

    @Column(name = "value")
    private int value;

    public FromServerToDb(){}

    public FromServerToDb(String dTime, String tagId, int value) {
        this.dTime = dTime;
        this.tagId = tagId;
        this.value = value;
    }



    @Override
    public String toString() {
        return "FromServerToDb{" +
                "id=" + id +
                ", dTime='" + dTime + '\'' +
                ", tagId='" + tagId + '\'' +
                ", value=" + value + '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getdTime() {
        return dTime;
    }

    public void setdTime(String dTime) {
        this.dTime = dTime;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}


