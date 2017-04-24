package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;


    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Puzzle");
        this.primaryStage.getIcons().add(new Image("file:src/assets/icon.png"));

        initRootLayout();
        showPuzzleLayout();
    }

    public void initRootLayout(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("../view/RootLayout.fxml"));

            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void showPuzzleLayout(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("../view/PuzzleLayout.fxml"));

            AnchorPane anchorPane = loader.load();

            rootLayout.setCenter(anchorPane);

            PuzzleController controller = loader.getController();
            controller.setMain(this);
        } catch(Exception e){
            e.printStackTrace();
        }
    }



    public Main(){}


    public static void main(String[] args) {
        launch(args);
    }
}
