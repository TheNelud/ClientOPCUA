package points;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import points.entities.FromServerToDb;
import points.entities.TableNamesTag;

import java.util.ArrayList;
import java.util.List;

public class EntryPoint {
    private final SessionFactory factory =  new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(TableNamesTag.class)
                .addAnnotatedClass(FromServerToDb.class)
                .buildSessionFactory();
    private final Session session = factory.getCurrentSession();

    /**Метод получения тегов из бд
     *создаем динамический массив в который в последющем будем класть наши теги.
     * начинаем транзакцию, создаем запрос в бд на просмотр таблицы,
     * кладем полученные даные в массив в виде строки.
     * Закрываем транзакцию. Незабываем вернуть все в свет.
     * Идем на обед.
     * */
    public List<String> getTag() {
        List<String> tagList = new ArrayList<>();
        try {
            session.beginTransaction();

//            tagList = Collections.singletonList(String.valueOf(session.createQuery("from TagName", TagName.class)
//                    .getResultList()));

            List<TableNamesTag> tableNamesTagList = session.createQuery("from TagName", TableNamesTag.class)
                    .getResultList();
            for (TableNamesTag tableNamesTag : tableNamesTagList){
                tagList.add(String.valueOf(tableNamesTag));
            }
            session.getTransaction().commit();
        } finally {
            factory.close();
        }
        return tagList;
    }

    /**Метод сохраняет в бд переданные в него данные
     * */
    public void putValuesInDB(FromServerToDb fromServerToDb){
        try {
            session.beginTransaction();
            session.save(fromServerToDb);
            session.getTransaction().commit();
        }finally {
            factory.close();
        }
    }

}
