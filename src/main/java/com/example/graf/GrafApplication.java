package com.example.graf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GrafApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Разработать программу по расчету минимального пути графа метрополитена СПб,
        // используя матрицу смежности характеризующая граф. Весом ребра графа является
        // время затрачиваемое для перемещения от одной станции к другой.
        FXMLLoader fxmlLoader = new FXMLLoader(GrafApplication.class.getResource("graf-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Расчет минимального пути графа метрополитена СПб");
        stage.getIcons().add(new Image(Objects.requireNonNull(GrafApplication.class.getResourceAsStream("/logo.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}