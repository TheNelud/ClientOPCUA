package points.entities;

import javax.persistence.*;

@Entity
@Table(name = "from_server_to_db")
public class ServerDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "d_time")
    private String dTime;

    public ServerDB(){}

    public ServerDB(String dTime) {
        this.dTime = dTime;
    }

    @Override
    public String toString() {
        return "ServerDB{" +
                "id=" + id +
                ", dTime='" + dTime + '\'' +
                '}';
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
}
