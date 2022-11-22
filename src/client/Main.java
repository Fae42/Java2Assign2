package client;

import client.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();

            fxmlLoader.setLocation(getClass().getClassLoader().getResource("mainUI.fxml"));
            Pane root = fxmlLoader.load();
            primaryStage.setTitle("Tic Tac Toe");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            primaryStage.show();
            Controller controller = fxmlLoader.getController();
            new Thread(controller.InitClient()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
