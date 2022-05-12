package ga.opc.ua.methods;


import ga.opc.ua.methods.model.*;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Distributor {
    private final Logger logger = Logger.getLogger(java.io.FileReader.class);

    public Config parse(){
        Logger logger = Logger.getLogger(java.io.FileReader.class);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        ParserHandler handler = new ParserHandler();

        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        }catch (Exception e){
            logger.error("Start parsing error" + e);
//            System.out.println("Start parsing error" + e.toString());
            return null;
        }

        File file = new File("src/main/resources/conf.xml");
        try {
            parser.parse(file, handler);
        }catch (SAXException | IOException e) {
            logger.error("Start parsing error" + e);
            System.out.println("SAX PARSING ERROR :" + e.toString());
            return null;
        }

        return handler.getConfig();
    }

}

