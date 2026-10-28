module ni.edu.uam.orm {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;
    requires java.naming;
    requires org.postgresql.jdbc;

    opens ni.edu.uam.orm to javafx.fxml;
    opens ni.edu.uam.orm.model to org.hibernate.orm.core;
    exports ni.edu.uam.orm;
}