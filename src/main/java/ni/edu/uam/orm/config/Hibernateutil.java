package ni.edu.uam.orm.config;

import ni.edu.uam.orm.model.Estudiante;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

public class Hibernateutil {
    private static final SessionFactory FACTORY = crear();
    private Hibernateutil(){}

    private static SessionFactory crear() {
        try (InputStream in = Hibernateutil.class.getClassLoader()
                .getResourceAsStream("hibernate.properties")) {

            if (in == null) {
                throw new IllegalStateException("No se encontró hibernate.properties en src/main/resources");
            }

            Properties p = new Properties();
            p.load(in);

            return new Configuration()
                    .addProperties(p)
                    .addAnnotatedClass(Estudiante.class)
                    .buildSessionFactory();

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static SessionFactory getSessionFactory() {
        return FACTORY;
    }
}
