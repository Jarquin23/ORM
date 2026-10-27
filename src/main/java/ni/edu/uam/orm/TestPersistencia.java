package ni.edu.uam.orm;

import ni.edu.uam.orm.model.Estudiante;
import ni.edu.uam.orm.repository.EstudianteRepository;

public class TestPersistencia {
    public static void main(String[] args) {
        EstudianteRepository repo = new EstudianteRepository();

        System.out.println("--- Guardando estudiante ---");
        repo.guardar(new Estudiante("Ana", "López", "ana@uam.edu.ni", "Ingeniería de Sistemas"));

        System.out.println("--- Listando ---");
        repo.listar().forEach(System.out::println);
    }
}