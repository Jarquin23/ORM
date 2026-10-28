package ni.edu.uam.orm.repository;

/** Indica que ya existe un estudiante con el correo proporcionado. */
public class CorreoDuplicadoException extends RuntimeException {

    public CorreoDuplicadoException(String correo) {
        super("Ya existe un estudiante con el correo: " + correo);
    }

    public CorreoDuplicadoException(String correo, Throwable causa) {
        super("Ya existe un estudiante con el correo: " + correo, causa);
    }
}
