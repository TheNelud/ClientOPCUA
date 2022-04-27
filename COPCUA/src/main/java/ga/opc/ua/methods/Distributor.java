package ga.opc.ua.methods;


import ga.opc.ua.methods.model.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

}

