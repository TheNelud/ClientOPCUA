package ga.opc.ua.methods;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.*;

public class DistributorJdbc {

    private static String USER_DB;
    private static String PASS_DB;
    private static String URL_DB;
    Connection connection = DriverManager.getConnection(URL_DB,USER_DB,PASS_DB);

    public DistributorJdbc() throws SQLException {
    }


    public ResultSet selectFromBdTags() throws SQLException {
        String sqlSelectTagsNames = "SELECT id,  hfrpok, inout FROM app_info.test_table WHERE hfrpok IS NOT NULL";
        Statement statement = connection.createStatement();
        return statement.executeQuery(sqlSelectTagsNames);
    }


    public static void readConfig(File file){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try{
            document = documentBuilderFactory.newDocumentBuilder().parse(file);

            Node root =document.getFirstChild();
            NodeList paramsNodes = root.getChildNodes();

            for (int i = 0; i < paramsNodes.getLength(); i++){
                switch (paramsNodes.item(i).getNodeName()){
                    case "USER_DB" -> USER_DB = paramsNodes.item(i).getTextContent();
                    case "PASS_DB" -> PASS_DB = paramsNodes.item(i).getTextContent();
                    case "URL_DB" -> URL_DB = paramsNodes.item(i).getTextContent();
                }
            }

            if (USER_DB.equals("") | PASS_DB.equals("") | URL_DB.equals("")){
                System.err.println("Отсутствуют необходимые параметры в конфигурационном файле config.xml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
