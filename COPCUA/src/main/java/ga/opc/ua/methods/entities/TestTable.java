package ga.opc.ua.methods.entities;

import javax.persistence.*;


@Entity
@Table (name = "app_info.test_table")
public class TestTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "hfrpok")
    private String hfrpok;

    @Column(name = "inout")
    private String inout;

    public TestTable() {}

    public TestTable(String hfrpok, String inout) {
        this.hfrpok = hfrpok;
        this.inout = inout;
    }

    public int getId() {
        return id;
    }

    public String getHfrpok() {
        return hfrpok;
    }

    public String getInout() {
        return inout;
    }

    @Override
    public String toString() {
        return "TestTable{" +
                "id=" + id +
                ", hfrpok='" + hfrpok + '\'' +
                '}';
    }
}
