package com.example.graf;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class GrafController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Spinner<Integer> vertexSpinner;

    @FXML
    private Button findPath;

    private int[][] matrix;

    @FXML
    private TextField from;

    @FXML
    private TextField to;

    @FXML
    private Text stantionList;

    private TextField[][] textFields;

    private HashMap<Integer,String> stantionHashSet = new HashMap<>();

    //Получение значений из текстовых полей в матрицу
    private void fillMatrix() {
        for (int row = 1; row < textFields.length; row++) {
            for (int col = 1; col < textFields[row].length; col++) {
                String text = textFields[row][col].getText();
                matrix[row][col] = Integer.parseInt(text);
            }
        }
    }

    //Отрисовка полей ввода для получения значений матрицы
    public void updateMatrix(Integer MATRIX_SIZE) {
        matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
        textFields = new TextField[MATRIX_SIZE][MATRIX_SIZE];
        mainBorderPane.setLeft(null);

        //Формирование текстового юлока со списком станций
        StringBuilder stations = new StringBuilder();
        for (int i = 1; i < MATRIX_SIZE; i++) {
            stations.append(i).append(") ").append(stantionHashSet.get(i)).append("\n");
        }
        stantionList.setText(stations.toString());

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(16));
        for (int row = 0; row < MATRIX_SIZE; row++) {
            for (int col = 0; col < MATRIX_SIZE; col++) {
                TextField textField = new TextField();
                textField.setPrefWidth(32);
                textField.setText("0");
                textFields[row][col] = textField;
                gridPane.add(textField, col, row);
                textFields[0][col].setText(String.valueOf(col));
                textFields[0][col].setDisable(true);
            }
            textFields[row][0].setText(String.valueOf(row));
            textFields[row][0].setDisable(true);
        }
        mainBorderPane.setLeft(gridPane);
    }

    //Проверка достижимости вершины end из вершины start согласно матрице adjacencyMatrix
    private boolean isReachable(int[][] adjacencyMatrix, int start, int end) {
        int n = adjacencyMatrix.length;
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == end) {
                return true; // end достижим из start
            }
            for (int v = 0; v < n; v++) {
                if (adjacencyMatrix[u][v] != 0 && !visited[v]) {
                    visited[v] = true;
                    queue.add(v);
                }
            }
        }
        return false; // end не достижим из start
    }

    //Вспомогательная функция находит вершину с минимальным значением расстояния среди непосещенных вершин
    private static int minDistance(int[] distance, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < distance.length; i++) {
            if (!visited[i] && distance[i] <= min) {
                min = distance[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    //Функция нахождения поиска кратчайшего пути в графе (использует алгоритм Дейкстры "https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm").
    public List<Integer> findShortestPath(int[][] adjacencyMatrix, int start, int end) {
        int n = adjacencyMatrix.length;
        boolean[] visited = new boolean[n];
        int[] distance = new int[n];
        int[] previous = new int[n];

        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[start] = 0;

        // Проверка достижимости из start в end
        if (!isReachable(adjacencyMatrix, start, end)) {
            return new ArrayList<>(); // Возвращаем пустой список, если end недостижим из start
        }

        for (int i = 0; i < n - 1; i++) {
            int u = minDistance(distance, visited);
            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visited[v] && adjacencyMatrix[u][v] != 0 && distance[u] != Integer.MAX_VALUE
                        && distance[u] + adjacencyMatrix[u][v] < distance[v]) {
                    distance[v] = distance[u] + adjacencyMatrix[u][v];
                    previous[v] = u;
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        int current = end;
        while (current != start) {
            path.add(current);
            current = previous[current];
        }
        path.add(start);
        Collections.reverse(path);
        return path;
    }

    @FXML
    void initialize() {
        //Список станций
        stantionHashSet.put(1,"Академическая");
        stantionHashSet.put(2,"Политехническая");
        stantionHashSet.put(3,"Лесная");
        stantionHashSet.put(4,"Выборгская");
        stantionHashSet.put(5,"Площадь Ленина");
        stantionHashSet.put(6,"Владимирская");
        stantionHashSet.put(7,"Пушкинская");
        stantionHashSet.put(8,"Балтийская");
        stantionHashSet.put(9,"Автово");
        stantionHashSet.put(10,"Кировский завод");

        //Установка допустимого значения количества вершин от 2 до 16 по умолчанию 3 (1 + 1 для добавления подписей строки/столбца)
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1 + 1,10, 3);
        vertexSpinner.setValueFactory(spinnerValueFactory);

        //Отрисовка полей ввода значений матрицы
        updateMatrix(vertexSpinner.getValue() + 1);

        //Обновление таблицы при изменении указанного количества графов
        vertexSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateMatrix(newValue + 1));

        //Действие на кнопку "Найти кратчайший путь"
        findPath.setOnAction(actionEvent -> {
            try {
                //Заполнение матрицы перед использованием
                fillMatrix();

                //Результат List с пройденными вершинами или пустой если пути не существует
                List<Integer> result = findShortestPath(matrix, Integer.parseInt(from.getText()), Integer.parseInt(to.getText()));

                //Уведомление об итогах поиска
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (!result.isEmpty()) {
                    alert.setTitle("Успех");
                    alert.setHeaderText("Путь найден.");
                    StringBuilder finalPath = new StringBuilder();
                    result.forEach(station -> {
                        finalPath.append(stantionHashSet.get(station)).append(" ");
                    });
                    alert.setContentText("Кратчайший путь лежит через станции: " + finalPath);
                } else {
                    alert.setTitle("Увы");
                    alert.setHeaderText("Путь не найден.");
                    alert.setContentText("Путь из вершины " + Integer.parseInt(from.getText()) + " в вершину " + Integer.parseInt(to.getText()) + " не существует.");
                }
                alert.showAndWait().ifPresent(rs -> {});
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Что то не так, проверьте правильность введенных данных!");
                alert.setContentText("Текст ошибки: " + e.getMessage());
                alert.showAndWait().ifPresent(rs -> {});
            }
        });
    }
}
