package ga.opc.ua;

import ga.opc.ua.methods.Distributor;
import ga.opc.ua.methods.DistributorJdbc;
import ga.opc.ua.methods.model.Config;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Start {
    public static void main(String[] args) throws Exception {

        Distributor distributor = new Distributor();
        Config config = distributor.parse();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        ClientReader clientRunner = new ClientReader(1, "five_min_result", "guid_masdu_5min", 1);
        ClientReader clientRunner1 = new ClientReader(2, "one_hour_result", "guid_masdu_hours", 5);
        ClientReader clientRunner2 = new ClientReader(3, "one_day_result", "guid_masdu_day", 10);

        executorService.submit(new ClientRunner(clientRunner));
//        executorService.submit(new ClientRunner(clientRunner1));
//        executorService.submit(new ClientRunner(clientRunner2));

        executorService.shutdown();
        System.out.println("\n\n\nAll tasks submitted\n\n\n");
        executorService.awaitTermination(1, TimeUnit.DAYS);



    }
}


