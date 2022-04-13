package points.entities;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tb_tag_name")
public class TableNamesTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "in_out")
    private String inOut;

    public TableNamesTag(){}

    public TableNamesTag(String tagName, String inOut) {
        this.tagName = tagName;
        this.inOut = inOut;
    }

    @Override
    public String toString() {
        return "TableNamesTag{" +
                "id=" + id +
                ", tagName='" + tagName + '\'' +
                ", inOut='" + inOut + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getInOut() {
        return inOut;
    }

    public void setInOut(String inOut) {
        this.inOut = inOut;
    }




    @Override
    public int hashCode() {
        return Objects.hash(id, tagName, inOut);
    }
}
