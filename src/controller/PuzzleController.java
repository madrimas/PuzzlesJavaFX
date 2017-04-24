package controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.CutImage;
import model.Tile;
import model.Time;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class PuzzleController {
    private List<Tile> tilesList;
    private Tile first = null;
    private Tile second = null;
    private Timeline timeline;
    private long time = 0;
    private int movesCount = 0;
    private boolean isGameStarted = false;
    private model.Time timeOfGame;
    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }
    private Rectangle firstChosen;
    private Rectangle secondChosen;


    @FXML
    private AnchorPane panel;
    @FXML
    private Label label;
    @FXML
    private Label bestTime;

    @FXML
    private void handleStartBtn(){
        time = 0;
        isGameStarted = true;
        movesCount = 0;
        first = null;
        second = null;
        firstChosen.setFill(Color.TRANSPARENT);
        secondChosen.setFill(Color.TRANSPARENT);
        timeOfGame = new model.Time();
        Time tempTime = new Time();
        String tempString = loadTimeFromFile();
        long tempLong = Long.valueOf(tempString).longValue();
        tempTime.setMillis(tempLong);
        bestTime.setText(tempTime.millisToString(tempLong));


        Collections.shuffle(tilesList);
        for(Tile tile : tilesList){
            int num = tile.getNumberOfImage();
            tile.setFill(new ImagePattern(SwingFXUtils.toFXImage(tilesList.get(num).getPartOfImage(), null)));
        }

        timeline = new Timeline(new KeyFrame(
                Duration.millis(100),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        updateTime();
                    }
                }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();




    }

    @FXML
    private void initialize(){

        this.tilesList = CutImage.getTileList(new File("out/production/PuzzlesJavaFX/assets/herb.png"));


        for(Tile tile : tilesList){
            tile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(first == null){
                        first = (Tile) event.getSource();
                        firstChosen.setLayoutX(first.getLayoutX() - 3);
                        firstChosen.setLayoutY(first.getLayoutY() - 3);
                        firstChosen.setFill(Color.AQUA);
                    }
                    else if(second == null) {
                        second = (Tile) event.getSource();
                        if ((first.getLayoutX() == second.getLayoutX() && Math.abs(first.getLayoutY() - second.getLayoutY()) < 150) || (Math.abs(first.getLayoutX() - second.getLayoutX()) < 150 && first.getLayoutY() == second.getLayoutY())){
                            movesCount++;
                            secondChosen.setLayoutX(first.getLayoutX() - 3);
                            secondChosen.setLayoutY(first.getLayoutY() - 3);
                            secondChosen.setFill(Color.AQUA);
                            PathTransition ptr = getPathTransition(first, second);
                            PathTransition ptr2 = getPathTransition(second, first);
                            ParallelTransition pt = new ParallelTransition(ptr, ptr2);
                            firstChosen.setFill(Color.TRANSPARENT);
                            secondChosen.setFill(Color.TRANSPARENT);
                            pt.play();
                            pt.setOnFinished(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    swap();
                                    if (isGameWon()) {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (isGameStarted) {
                                                    timeline.stop();

                                                    //timeList.add(timeOfGame);
                                                    saveTimeToFile();

                                                    //saveTimes(file);

                                                    setWonAlert();
                                                    isGameStarted = false;
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            second = null;
                        }
                    }
                }
            });
        }


        firstChosen = new Rectangle(106, 106, Color.TRANSPARENT);
        secondChosen = new Rectangle(106, 106, Color.TRANSPARENT);
        panel.getChildren().add(firstChosen);
        panel.getChildren().add(secondChosen);
        panel.getChildren().addAll(tilesList);
    }
    private void swap(){
        int indexOfFirst = tilesList.indexOf(first);
        int indexOfSecond = tilesList.indexOf(second);

        final double xf = first.getLayoutX();
        final double yf = first.getLayoutY();
        final double xs = second.getLayoutX();
        final double ys = second.getLayoutY();

        first.setTranslateX(0);
        first.setTranslateY(0);
        second.setTranslateX(0);
        second.setTranslateY(0);

        first.setLayoutX(xs);
        first.setLayoutY(ys);
        second.setLayoutX(xf);
        second.setLayoutY(yf);

        Collections.swap(tilesList, indexOfFirst, indexOfSecond);
        first = null;
        second = null;
    }
    private boolean isGameWon(){
        for(int i = 0; i < tilesList.size(); i++){
            if(tilesList.get(i).getNumberOfImage() != i)
                return false;
        }
        return true;
    }
    public void saveTimeToFile(){
        try{
            String tempString = loadTimeFromFile();
            long tempLong = Long.valueOf(tempString).longValue();
            long timeMillis = timeOfGame.calculateTimeSum();

            if(timeMillis < tempLong){
                PrintWriter writer = new PrintWriter("time.txt");
                writer.println(timeMillis);

                writer.close();
            }
        }catch(Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd!");
            alert.setHeaderText("Nie udało się zapisać danych");
            alert.setContentText("Nie udało się zapisać danych do pliku:\n");

            alert.showAndWait();
        }
    }
    public static String loadTimeFromFile() {
        File file = new File("time.txt");
        String reader = "";
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                reader += scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Brak Pliku do odczytania!");
        }
        return reader;
    }
    private void setWonAlert(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Wygrana");
        alert.setHeaderText("Gratulacje!");
        alert.setContentText("Udało Ci się ułożyć puzzle!\nIlość ruchów to: " + movesCount);
        alert.showAndWait();
    }
    private void updateTime(){
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        long millis = time - TimeUnit.SECONDS.toMillis(seconds);
        String timeString = String.format("%02d:%02d:%02d:%d", hours, minutes, seconds, millis);
        label.setText(timeString);
        time += 100;
        timeOfGame.setMillis(millis);
        timeOfGame.setSeconds(seconds);
        timeOfGame.setMinutes(minutes);
        timeOfGame.setHours(hours);
    }
    private PathTransition getPathTransition(Tile first, Tile second){
        PathTransition pathTransition = new PathTransition();

        Path path = new Path();
        path.getElements().clear();
        path.getElements().add(new MoveToAbs(first));
        path.getElements().add(new LineToAbs(first, second.getLayoutX(), second.getLayoutY()));

        pathTransition.setPath(path);
        pathTransition.setNode(first);
        return pathTransition;
    }

    public static class MoveToAbs extends MoveTo {
        public MoveToAbs(Node node){
            super(node.getLayoutBounds().getWidth()/2,node.getLayoutBounds().getHeight()/2);
        }
    }
    public static class LineToAbs extends LineTo {
        public LineToAbs(Node node, double x, double y){
            super(x - node.getLayoutX() + node.getLayoutBounds().getWidth() / 2, y - node.getLayoutY() + node.getLayoutBounds().getHeight() / 2);
        }
    }
}
