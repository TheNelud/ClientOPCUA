package ga.opc.ua;

import ga.opc.ua.methods.Distributor;
import ga.opc.ua.methods.model.Clients;
import ga.opc.ua.methods.model.Config;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Start {
    final static Logger logger = Logger.getLogger(java.io.FileReader.class);


    public static void main(String[] args) throws Exception {
        /**Создаем 4 потока, (всегда на +1 кол-ва клиентов)
         * 1 main
         * 3 clients */
        createThreads(2);

    }

    public static void createThreads(int num_threads) throws Exception {

        Distributor distributor = new Distributor();
        Config config = distributor.parse();
        logger.info("Read config: successfully");

        ExecutorService executorService = Executors.newFixedThreadPool(num_threads);

        for (int i = 0; i < num_threads; i++){
            List<Clients> clientsList = new ArrayList<>(config.getClientsList());
            for (Clients str : clientsList){
                if (Integer.parseInt(str.getId()) == i){
                    executorService.submit(new ClientRunner(new ClientReader(Integer.parseInt(str.getId()), str.getNameTable(), str.getColumnGuid(), str.getPeriodWorker())));

                }
            }
        }
        executorService.shutdown();
        System.out.println("\n\n\nAll tasks submitted\n\n\n");
        executorService.awaitTermination(1, TimeUnit.DAYS);
        logger.info("Close programm....");
    }
}


