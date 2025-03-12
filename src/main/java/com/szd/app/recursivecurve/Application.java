package com.szd.app.recursivecurve;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/snowflake.png")));
        stage.getIcons().add(icon);
        Scene scene = new Scene(fxmlLoader.load(), 500, 440);
        stage.setTitle("Recursive Curve");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}