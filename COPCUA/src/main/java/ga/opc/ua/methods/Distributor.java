package ga.opc.ua.methods;


import ga.opc.ua.methods.model.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Distributor {
    public Config parse(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        ParserHandler handler = new ParserHandler();

        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        }catch (Exception e){
            System.out.println("Start parsing error" + e.toString());
            return null;
        }

        File file = new File("conf.xml");
        try {
            parser.parse(file, handler);
        }catch (SAXException | IOException e) {
            System.out.println("SAX PARSING ERROR :" + e.toString());
            return null;
        }

        return handler.getConfig();
    }

    public void distrib(){
        Distributor distributor = new Distributor();
        Config config = distributor.parse();

//        DataBase dataBase = new DataBase();
//        OpcServer opcServer = new OpcServer();
//        Clients clients = new Clients();
//        SelectTable selectTable = new SelectTable();


        List<DataBase> listDB =  new ArrayList<>(config.getDataBaseList());
        for (DataBase str : listDB){
            System.out.println(str);
        }

        List<OpcServer> listOpc = new ArrayList<>(config.getOpcServerList());
        for (OpcServer str : listOpc){
            System.out.println(str.getIp() + " : " + str.getPort());
        }

        List<Clients> listClient = new ArrayList<>(config.getClientsList());
        for (Clients str : listClient){
//            System.out.println(str);
        }





        System.out.println(config.toString());



    }

}

