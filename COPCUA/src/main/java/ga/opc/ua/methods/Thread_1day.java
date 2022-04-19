package ga.opc.ua.methods;

import java.sql.SQLException;

public class Thread_1day extends DistributorJdbc implements Runnable{
    private final String guid_masdu_1day;
    private final String hfrpok;
    private final String value;

    public Thread_1day(String guid_masdu_1day, String hfrpok, String value) throws SQLException {
        this.guid_masdu_1day = guid_masdu_1day;
        this.hfrpok = hfrpok;
        this.value = value;
    }

    @Override
    public void run() {
        try{
            insertInDb1day(guid_masdu_1day, hfrpok, value);
            Thread.sleep(10 * 60_000L); //поменять на 1 день
        }catch (InterruptedException | SQLException e){
            e.printStackTrace();
        }
    }
}
