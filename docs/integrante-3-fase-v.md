# Integrante 3 — Repositorio y Fase V

Rama de entrega: `Kate`. Destino del pull request: `master`, después de las entregas de configuración y entidad.

## Respuestas de la Fase V

### ¿Qué es SessionFactory?

Es el objeto de Hibernate que contiene la configuración y los mapeos de las entidades, y permite crear sesiones. Su creación es costosa; normalmente se mantiene una sola instancia por base de datos durante la ejecución de la aplicación. Se comparte entre hilos. El repositorio obtiene esta instancia mediante `HibernateUtil.getSessionFactory()`; no la cierra después de cada operación.

### ¿Qué es Session?

Representa una unidad de trabajo con Hibernate y mantiene el contexto de persistencia de los objetos administrados. Permite consultar y guardar entidades. No se debe compartir entre hilos. Cada método del repositorio abre su propia sesión y la cierra automáticamente mediante `try-with-resources`, incluso si ocurre una excepción.

### ¿Qué es Transaction?

Agrupa operaciones de base de datos como una unidad que se confirma o se deshace. En `guardar`, `beginTransaction()` inicia la transacción; después se verifica el correo y se registra el estudiante. `commit()` confirma los cambios. La restricción UNIQUE de la base de datos sigue siendo necesaria porque dos transacciones pueden consultar simultáneamente un correo que todavía no existe.

### ¿Qué hace persist?

`session.persist(estudiante)` incorpora un estudiante nuevo al contexto de persistencia para insertarlo en la base de datos. No sustituye a `commit()`. Hibernate sincroniza los cambios con la base de datos al hacer flush, normalmente antes del commit; con un identificador IDENTITY puede necesitar ejecutar el INSERT durante persist para obtener el identificador generado.

### ¿Para qué sirve rollback?

`rollback()` deshace los cambios de una transacción que no se pudo completar. Si falla la verificación, la inserción o la confirmación, el repositorio intenta revertir la transacción si continúa activa y propaga el error. Si también falla el rollback, conserva ese error como excepción suprimida para no perder el error original. No revierte una transacción ya confirmada ni necesariamente restaura los campos del objeto Java.

## Reto 2 — Evitar correos duplicados

`guardar` comprueba el correo dentro de su sesión y transacción. Si ya existe, lanza `CorreoDuplicadoException`. Además, examina las causas y las excepciones SQL encadenadas: cuando PostgreSQL informa SQLState `23505`, transforma el error en `CorreoDuplicadoException`, conservando su causa. Esto cubre la carrera entre dos inserciones del mismo correo.

El tratamiento de `23505` presupone que correo es la única clave única de negocio que puede infringir este INSERT y que el id se genera automáticamente. Si se agregan otras restricciones únicas, será necesario distinguir la restricción infringida antes de atribuir el error al correo.

La comparación usa igualdad y no normaliza mayúsculas ni espacios: debe corresponder con la política de correo y la restricción UNIQUE acordadas para la entidad y la base de datos.

## Reto 3 — Búsqueda por carrera

```java
List<Estudiante> estudiantes = repositorio.buscarPorCarrera("Ingeniería en Sistemas");
```

Se usa HQL sobre la entidad y sus atributos:

```hql
from Estudiante e where e.carrera = :carrera order by e.id
```

`setParameter("carrera", carrera)` enlaza el valor sin concatenarlo con la consulta. La búsqueda devuelve coincidencias exactas; si no encuentra estudiantes, devuelve una lista vacía.

## Contrato de integración

- Configuración (integrante 1): Hibernate y PostgreSQL disponibles en Maven, eliminación de `module-info.java` y método estático `ni.edu.uam.orm.config.HibernateUtil.getSessionFactory()` que devuelve `org.hibernate.SessionFactory`.
- Entidad (integrante 2): `ni.edu.uam.orm.model.Estudiante`, con nombre de entidad predeterminado `Estudiante`, atributos `id`, `correo` y `carrera`, método `getCorreo()` y restricción UNIQUE para correo en PostgreSQL.
- Interfaz (integrante 4): puede crear `new EstudianteRepository()` y llamar a `guardar(Estudiante)`, `listar()`, `buscarPorCarrera(String)` y `existePorCorreo(String)`. Debe ejecutar las operaciones en segundo plano y capturar `ni.edu.uam.orm.repository.CorreoDuplicadoException` para mostrar su mensaje.

## Validación pendiente al integrar las entregas 1 y 2

En la copia local inicial todavía faltan configuración, dependencias de Hibernate y entidad. No se ejecutó compilación ni se generaron capturas de resultados de base de datos. Después de sus merges, actualizar `master` y rebasar esta rama según las reglas del equipo; ejecutar con `./mvnw clean javafx:run` cuando esté disponible la interfaz, o con `Launcher.main` en IntelliJ.

Casos para comprobar y documentar con capturas reales:

1. Guardar un estudiante con correo nuevo y verificar que aparece al listar.
2. Repetir el correo: recibir `CorreoDuplicadoException` y conservar una sola fila.
3. Intentar simultáneamente dos altas con el mismo correo nuevo: una debe guardarse y la otra debe informar correo duplicado. La restricción UNIQUE debe estar instalada.
4. Buscar una carrera con registros y otra sin registros: obtener solo coincidencias o una lista vacía.
5. Buscar una carrera con una comilla, por ejemplo `Carrera 'experimental'`: tratar todo el texto como valor del parámetro.
6. Provocar un error de integridad diferente al correo, por ejemplo un campo obligatorio nulo: propagar el error original, no informar correo duplicado, y comprobar que no quedó una fila parcial.

La entrega se limita a los dos archivos de `repository` y este informe; el push y el pull request se realizan en la rama de trabajo, nunca directamente en `master`.
