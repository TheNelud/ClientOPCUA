package ga.opc.ua.methods;

import ga.opc.ua.methods.model.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class ParserHandler extends DefaultHandler {
    /**Набор тегов*/
    private static final String TAG_MAIN_DB = "database";
    private static final String TAG_NAME_DB = "name";
    private static final String TAG_IP_DB = "ip";
    private static final String TAG_PORT_DB = "port";
    private static final String TAG_USER_DB = "user";
    private static final String TAG_PASSWORD_DB = "password";
    private static final String TAG_TABLE_DB = "table";
    private static final String TAG_NAME_TABLE = "name";
    private static final String TAG_CID_TABLE = "column_id";
    private static final String TAG_CTAG_TABLE = "column_tag";
    private static final String TAG_CINOUT_TABLE = "column_inout";
    private static final String TAG_CGUID1_TABLE = "column_guid_1";
    private static final String TAG_CGUID2_TABLE = "column_guid_2";
    private static final String TAG_CGUID3_TABLE = "column_guid_3";

    private static final String TAG_MAIN_OPC= "opc_servers";
    private static final String TAG_OPC_SERVER = "opc_server";
    private static final String TAG_IP_OPC = "ip";
    private static final String TAG_PORT_OPC = "port";

    private static final String TAG_MAIN_CLIENTS = "clients";
    private static final String TAG_CLIENT= "client";
    private static final String TAG_ID_CLIENT = "id";
    private static final String TAG_NAMETB_CLIENT = "name_table";
    private static final String TAG_CGUID_CLIENT = "column_guid";
    private static final String TAG_PERIOD= "period_worker";

    /**Сюда все будем класть*/
    private Config config = new Config();

    /**Текущий тег*/
    private String currentTagName;

    /**Куда будем сохранять*/
    private List<DataBase> dataBaseList = new ArrayList<>();
    private List<SelectTable> selectTableList = new ArrayList<>();
    private List<OpcServer> opcServerList = new ArrayList<>();
    private List<Clients> clientsList = new ArrayList<>();

    /**Проверка, нах ли мы тут?*/
    private boolean isDataBase = false;
    private boolean isSelectTable = false;
    private boolean isOpcServers = false;
    private boolean isOpcServer = false;
    private boolean isClients= false;
    private boolean isClient = false;

    /**Переменные (потом сохранять set) */
    String ip_db, port_db, user_db, password_db, name_db;
    String name_tb, column_id, column_tag,column_inout, column_guid_1,column_guid_2, column_guid_3;
    String opc_ip, opc_port;
    String id_client,nameTb_client, columnGuid_client;
    int periodWorker;


    public Config getConfig(){
        return config;
    }


    @Override
    public void startDocument() throws SAXException {}

    @Override
    public void endDocument() throws SAXException {
        config.setDataBaseList(dataBaseList);
        config.setOpcServerList(opcServerList);
        config.setClientsList(clientsList);

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTagName = qName;
        switch (currentTagName) {
            case TAG_MAIN_DB -> isDataBase = true;
            case TAG_TABLE_DB -> isSelectTable = true;
            case TAG_MAIN_OPC -> isOpcServers = true;
            case TAG_OPC_SERVER -> isOpcServer = true;
            case (TAG_MAIN_CLIENTS) -> isClients = true;
            case TAG_CLIENT -> isClient = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        /**возвращаем местоположение текущего тега
         * для базы дынных*/
        if (qName.equals(TAG_MAIN_DB)){
            isDataBase = false;
            DataBase dataBase = new DataBase(ip_db, port_db, user_db, password_db, name_db , selectTableList);
            dataBaseList.add(dataBase);
        }else if(qName.equals(TAG_TABLE_DB)){
            isSelectTable = false;
            SelectTable selectTable = new SelectTable(name_tb,column_id, column_tag,column_inout, column_guid_1,column_guid_2, column_guid_3);
            selectTableList.add(selectTable);
        }
        /**возвращаем местоположение текущего тега
         * для opc сервера*/
        if (qName.equals(TAG_MAIN_OPC)){
            isOpcServers = false;
        }else if (qName.equals(TAG_OPC_SERVER)){
            isOpcServer = false;
            OpcServer  opcServer = new OpcServer(opc_ip, opc_port);
            opcServerList.add(opcServer);
        }
        /**возвращаем местоположение текущего тега
         * для клиентов*/
        if (qName.equals(TAG_MAIN_CLIENTS)){
            isClients = false;
        }else if (qName.equals(TAG_CLIENT)){
            isClient = false;
            Clients clients = new Clients(id_client,nameTb_client, columnGuid_client, periodWorker);
            clientsList.add(clients);
        }

        currentTagName = null;

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentTagName == null){
            return;
        }
        /**Определяем текущее положение тега в кофигурации базы данных
         * и записываем его в соответствующюю переменную*/
        if(!isSelectTable && isDataBase){
            switch (currentTagName){
                case TAG_NAME_DB -> name_db = new String(ch, start, length);
                case TAG_IP_DB -> ip_db = new String(ch, start, length);
                case TAG_PORT_DB -> port_db = new String(ch, start, length);
                case TAG_USER_DB -> user_db = new String(ch, start, length);
                case TAG_PASSWORD_DB -> password_db = new String(ch, start, length);
            }
        }else if (isDataBase && isSelectTable) {
            switch (currentTagName) {
                case TAG_NAME_TABLE -> name_tb = new String(ch, start, length);
                case TAG_CID_TABLE -> column_id = new String(ch, start, length);
                case TAG_CTAG_TABLE -> column_tag = new String(ch, start, length);
                case TAG_CINOUT_TABLE -> column_inout = new String(ch, start, length);
                case TAG_CGUID1_TABLE -> column_guid_1 = new String(ch, start, length);
                case TAG_CGUID2_TABLE -> column_guid_2 = new String(ch, start, length);
                case TAG_CGUID3_TABLE -> column_guid_3 = new String(ch, start, length);
            }
        }

        /**Определяем текущее положение тега в кофигурации opc сервера
         * и записываем его в соответствующюю переменную*/
        if (isOpcServers && isOpcServer){
            switch (currentTagName){
                case TAG_IP_OPC -> opc_ip = new String(ch, start, length);
                case TAG_PORT_OPC -> opc_port = new String(ch, start, length);
            }
        }

        /**Определяем текущее положение тега в кофигурации клиента
         * и записываем его в соответствующюю переменную*/
        if (isClients && isClient){
            switch (currentTagName){
                case TAG_ID_CLIENT -> id_client = new String(ch, start, length);
                case TAG_NAMETB_CLIENT -> nameTb_client = new String(ch, start, length);
                case TAG_CGUID_CLIENT -> columnGuid_client = new String(ch, start, length);
                case TAG_PERIOD -> periodWorker = Integer.parseInt(new String(ch, start, length));
            }
        }
    }
}
