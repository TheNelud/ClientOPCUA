package ga.opc.ua.methods;

import ga.opc.ua.methods.entities.TestTable;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class Distributor {
    private final SessionFactory factory =  new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(TestTable.class)
            .buildSessionFactory();
    private final Session session = factory.getCurrentSession();


    public List<String> getTagList() {
        List<String> tagList = new ArrayList<>();
        try {
            session.beginTransaction();

            List<TestTable> tableNamesTagList = session.createQuery("from TestTable", TestTable.class)
                    .getResultList();

            for (TestTable tableNamesTag : tableNamesTagList){
                tagList.add(String.valueOf(tableNamesTag));
            }

            session.getTransaction().commit();
        } finally {
            factory.close();
        }
        return tagList;
    }
}
