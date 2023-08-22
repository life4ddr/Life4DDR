module com.perrigogames.editor {
    requires javafx.controls;
    requires javafx.fxml;
        requires javafx.web;
                requires kotlin.stdlib;
    
        requires org.controlsfx.controls;
                    requires org.kordamp.ikonli.javafx;
        
    opens com.perrigogames.editor to javafx.fxml;
    exports com.perrigogames.editor;
}