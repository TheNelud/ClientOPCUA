package ga.opc.ua.methods;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DistributorJdbc {

    private static String USER_DB;
    private static String PASS_DB;
    private static String URL_DB;

    Connection connection = DriverManager.getConnection(URL_DB,USER_DB,PASS_DB);
    Connection connectionLocalhost = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/journal_kovikta", "postgres", "0000");

    public DistributorJdbc() throws SQLException {
    }

    //как сделать красиво?!/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void insertInDb5min(String guid_masdu_5min, String hfrpok, String value) throws SQLException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlInsertTagValues = "INSERT INTO app_info.five_min_result(timestamp, guid_masdu_5min, hfrpok, value) VALUES( ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connectionLocalhost.prepareStatement(sqlInsertTagValues);
        preparedStatement.setTimestamp(1,timestamp);
        preparedStatement.setString(2, guid_masdu_5min);
        preparedStatement.setString(3, hfrpok);
        preparedStatement.setString(4, value);
        preparedStatement.executeUpdate();
        preparedStatement.close();

    }

    public void insertInDb1hour(String guid_masdu_1hour, String hfrpok, String value) throws SQLException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlInsertTagValues = "INSERT INTO app_info.one_hour_result(timestamp, guid_masdu_1hour, hfrpok, value) VALUES( ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connectionLocalhost.prepareStatement(sqlInsertTagValues);
        preparedStatement.setTimestamp(1,timestamp);
        preparedStatement.setString(2, guid_masdu_1hour);
        preparedStatement.setString(3, hfrpok);
        preparedStatement.setString(4, value);
        preparedStatement.executeUpdate();
        preparedStatement.close();

    }
    public void insertInDb1day(String guid_masdu_1day, String hfrpok, String value) throws SQLException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlInsertTagValues = "INSERT INTO app_info.one_hour_day(timestamp, guid_masdu_1day, hfrpok, value) VALUES( ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connectionLocalhost.prepareStatement(sqlInsertTagValues);
        preparedStatement.setTimestamp(1,timestamp);
        preparedStatement.setString(2, guid_masdu_1day);
        preparedStatement.setString(3, hfrpok);
        preparedStatement.setString(4, value);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void insertFromDBTags( String hfrpok, String value) throws SQLException{
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlInsertTagValues = "INSERT INTO app_info.answer_result(hfrpok, value, timestamp) VALUES( ?, ?, ?)";
        PreparedStatement preparedStatement = connectionLocalhost.prepareStatement(sqlInsertTagValues);
        preparedStatement.setString(1, hfrpok);
        preparedStatement.setString(2, value);
        preparedStatement.setTimestamp(3, timestamp);
        preparedStatement.executeUpdate();
        preparedStatement.close();
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
