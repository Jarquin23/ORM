module ni.edu.uam.orm {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.orm to javafx.fxml;
    exports ni.edu.uam.orm;
}