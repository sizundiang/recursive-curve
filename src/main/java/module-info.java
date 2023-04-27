module com.app.szd.recursivecurve {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.app.szd.recursivecurve to javafx.fxml;
    exports com.app.szd.recursivecurve;
}