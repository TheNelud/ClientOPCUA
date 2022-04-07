package points;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import points.entities.TagName;

import java.util.ArrayList;
import java.util.List;

public class EntryPoint {
    private final SessionFactory factory =  new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(TagName.class)
                .buildSessionFactory();
    private final Session session = factory.getCurrentSession();

    /**Метод получения тегов из бд
     *создаем динамический массив в который в последющем будем класть наши теги.
     * начинаем транзакцию, запросом считываем таблицу бд, кладем во временый массив сущности tagNameList
     * перекладываем в наш массив tagList. Закрываем транзакцию. Незабываем вернуть все в свет.
     * Идем на обед.
     * */
    public List<String> getTag() {
        List <String> tagList = new ArrayList<>();
        try {
            session.beginTransaction();
            List<TagName> tagNameList = session.createQuery("from TagName", TagName.class)
                    .getResultList();
            for (TagName tagName : tagNameList){
                tagList.add(String.valueOf(tagName));
            }
            session.getTransaction().commit();
        } finally {
            factory.close();
        }
        return tagList;
    }

}
