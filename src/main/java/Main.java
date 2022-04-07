import points.EntryPoint;
import points.entities.TagName;


import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> tagNameList =new EntryPoint().getTag();

        System.out.println(tagNameList);



    }
}
