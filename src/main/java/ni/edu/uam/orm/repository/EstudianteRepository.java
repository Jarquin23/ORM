package ni.edu.uam.orm.repository;

import ni.edu.uam.orm.config.HibernateUtil;
import ni.edu.uam.orm.model.Estudiante;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Operaciones de persistencia; cada llamada utiliza su propia sesión. */
public class EstudianteRepository {

    public void guardar(Estudiante estudiante) {
        Objects.requireNonNull(estudiante, "El estudiante es obligatorio");
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                transaction = session.beginTransaction();

                if (existePorCorreo(session, estudiante.getCorreo())) {
                    throw new CorreoDuplicadoException(estudiante.getCorreo());
                }

                session.persist(estudiante);
                transaction.commit();
            } catch (RuntimeException error) {
                if (transaction != null && transaction.isActive()) {
                    try {
                        transaction.rollback();
                    } catch (RuntimeException errorRollback) {
                        error.addSuppressed(errorRollback);
                    }
                }

                if (error instanceof CorreoDuplicadoException) {
                    throw error;
                }
                // La restricción UNIQUE protege incluso ante guardados concurrentes.
                if (esCorreoDuplicado(error)) {
                    throw new CorreoDuplicadoException(estudiante.getCorreo(), error);
                }
                throw error;
            }
        }
    }

    public List<Estudiante> listar() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Estudiante e order by e.id", Estudiante.class)
                    .getResultList();
        }
    }

    public List<Estudiante> buscarPorCarrera(String carrera) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "from Estudiante e where e.carrera = :carrera order by e.id",
                            Estudiante.class)
                    .setParameter("carrera", carrera)
                    .getResultList();
        }
    }

    public boolean existePorCorreo(String correo) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return existePorCorreo(session, correo);
        }
    }

    private boolean existePorCorreo(Session session, String correo) {
        return session.createQuery(
                        "select count(e) from Estudiante e where e.correo = :correo",
                        Long.class)
                .setParameter("correo", correo)
                .getSingleResult() > 0;
    }

    private boolean esCorreoDuplicado(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) {
            if (causa instanceof SQLException sqlException) {
                for (SQLException sql = sqlException; sql != null; sql = sql.getNextException()) {
                    if ("23505".equals(sql.getSQLState())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
