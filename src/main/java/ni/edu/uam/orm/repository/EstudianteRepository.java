package ni.edu.uam.orm.repository;

import ni.edu.uam.orm.config.Hibernateutil;
import ni.edu.uam.orm.model.Estudiante;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EstudianteRepository {
    public void guardar(Estudiante estudiante) {
        Transaction tx = null;
        try (Session session = Hibernateutil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(estudiante);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<Estudiante> listar() {
        try (Session session = Hibernateutil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Estudiante", Estudiante.class).getResultList();
        }
    }

    public Estudiante buscarPorCorreo(String correo) {
        try (Session session = Hibernateutil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Estudiante e WHERE e.correo = :correo", Estudiante.class)
                    .setParameter("correo", correo)
                    .getSingleResult();
        }
    }

    public List<Estudiante> listarPorCarrera(String carrera) {
        try (Session session = Hibernateutil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Estudiante e WHERE e.carrera = :carrera", Estudiante.class)
                    .setParameter("carrera", carrera)
                    .getResultList();
        }
    }
}

