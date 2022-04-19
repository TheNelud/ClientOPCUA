package ga.opc.ua.methods;

import java.sql.*;

public class Thread_5min extends DistributorJdbc implements Runnable{
    private final String guid_masdu_5min;
    private final String hfrpok;
    private final String value;


    public Thread_5min(String guid_masdu_5min, String hfrpok , String value) throws SQLException {
        this.guid_masdu_5min = guid_masdu_5min;
        this.hfrpok = hfrpok;
        this.value = value;
    }

    @Override
    public void run() {
        try{
            insertInDb5min(guid_masdu_5min, hfrpok, value);
            Thread.sleep(5 * 60_000L);
        }catch (InterruptedException | SQLException e){
            e.printStackTrace();
        }
    }
}
