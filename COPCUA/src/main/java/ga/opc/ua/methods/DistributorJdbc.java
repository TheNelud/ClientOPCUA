package ga.opc.ua.methods;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DistributorJdbc{

    private String USER_DB;
    private String PASS_DB;
    private String URL_DB;

    public String getUserDb() {
        return USER_DB;
    }

    public String getPassDb() {
        return PASS_DB;
    }

    public String getUrlDb() {
        return URL_DB;
    }

    public void setUSER_DB(String USER_DB) {
        this.USER_DB = USER_DB;
    }

    public void setPASS_DB(String PASS_DB) {
        this.PASS_DB = PASS_DB;
    }

    public void setURL_DB(String URL_DB) {
        this.URL_DB = URL_DB;
    }

    public DistributorJdbc() throws SQLException {
    }

    public void readConfig(File file){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try{
            document = documentBuilderFactory.newDocumentBuilder().parse(file);

            Node root =document.getFirstChild();
            NodeList paramsNodes = root.getChildNodes();

            for (int i = 0; i < paramsNodes.getLength(); i++){
                switch (paramsNodes.item(i).getNodeName()){
                    case "USER_DB" -> setUSER_DB(paramsNodes.item(i).getTextContent());
                    case "PASS_DB" -> setPASS_DB(paramsNodes.item(i).getTextContent());
                    case "URL_DB" -> setURL_DB(paramsNodes.item(i).getTextContent());
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
