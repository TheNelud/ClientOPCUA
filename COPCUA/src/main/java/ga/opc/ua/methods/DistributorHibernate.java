package ga.opc.ua.methods;

import ga.opc.ua.methods.entities.TestTable;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class DistributorHibernate {
    private final SessionFactory factory =  new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(TestTable.class)
            .buildSessionFactory();
    private final Session session = factory.getCurrentSession();


    public void getTagList() {
        List<String> tagList = new ArrayList<>();
        try {
            session.beginTransaction();

            List<TestTable> tableNamesTagList = session.createQuery("from TestTable", TestTable.class)
                    .getResultList();

            for (TestTable testTable : tableNamesTagList){
                System.out.println(testTable);
            }

            session.getTransaction().commit();
        } finally {
            factory.close();
        }
    }
}
