package ga.opc.ua.methods;

import java.sql.SQLException;

public class Thread_1hour extends DistributorJdbc implements Runnable{
    private final String guid_masdu_1hour;
    private final String hfrpok;
    private final String value;

    public Thread_1hour(String guid_masdu_1hour, String hfrpok, String value) throws SQLException {
        this.guid_masdu_1hour = guid_masdu_1hour;
        this.hfrpok = hfrpok;
        this.value = value;
    }

    @Override
    public void run() {
        try{
            insertInDb1hour(guid_masdu_1hour, hfrpok, value);
//            Thread.sleep(60_000L); //поменять на 1 час
        }catch (SQLException e){
            e.printStackTrace();
        }

    }
}
