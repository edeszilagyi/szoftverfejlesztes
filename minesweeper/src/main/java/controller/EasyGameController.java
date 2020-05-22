package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/*import java.time.Instant;
import java.util.Arrays;
import java.util.List;*/

public class EasyGameController {

    //private tileState gameState;
    private String userName;
    private Image cubeImages;
    //private Instant beginGame;

    @FXML
    private Label usernameLabel;

    @FXML
    private GridPane gameGrid;

    private void drawGameState() {
        //stepLabel.setText(String.valueOf(stepCount));

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                ImageView view = (ImageView) gameGrid.getChildren().get(i * 10 + j);
                view.setImage(cubeImages);
            }
        }
    }

    public void initdata(String userName) {
        this.userName = userName;
        usernameLabel.setText("Current user: " + this.userName);
    }

    @FXML
    public void initialize() {

        //gameResultDao = GameResultDao.getInstance();

        //gameState = new tileState();

        //stepCount = 0;

        //beginGame = Instant.now();

        cubeImages = new Image("/pictures/tile.png");

        drawGameState();
    }


}
