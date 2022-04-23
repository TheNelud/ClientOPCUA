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

    private static final String TAG_MAIN_CIENTS = "clients";
    private static final String TAG_CLIENT= "client";
    private static final String TAG_ID_CIENT = "id";
    private static final String TAG_NAMETB_CIENT = "name_table";
    private static final String TAG_CGUID_CIENT = "column_guid";
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





    public Config getConfig(){
        return config;
    }


    @Override
    public void startDocument() throws SAXException {}

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTagName = qName;
        if(currentTagName.equals(TAG_MAIN_DB)){
            isDataBase = true;
        }//тут я остановился)))
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        System.out.println("Start element " + qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        System.out.println("characters: " + new String(ch, start, length));
    }
}
