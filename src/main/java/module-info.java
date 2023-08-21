module com.szd.app.recursivecurve {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.szd.app.recursivecurve to javafx.fxml;
    exports com.szd.app.recursivecurve;
}