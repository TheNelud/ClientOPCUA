package ga.opc.ua;

import ga.opc.ua.methods.DistributorJdbc;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start {
    public static void main(String[] args) throws Exception {

        DistributorJdbc distributorJdbc = new DistributorJdbc();
        distributorJdbc.readConfig(new File("config.xml"));
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        ClientReader clientRunner = new ClientReader(1, "five_min_result", "guid_masdu_5min", 5);
        ClientReader clientRunner1 = new ClientReader(2, "one_hour_result", "guid_masdu_hours", 60);
        ClientReader clientRunner2 = new ClientReader(3, "one_day_result", "guid_masdu_day", 60 * 24);

        new ClientRunner(clientRunner).run();
        new ClientRunner(clientRunner1).run();
        new ClientRunner(clientRunner2).run();


    }
}


