package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import results.GameResult;
import results.GameResultDao;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GameController {

    //private tileState gameState;
    private Instant beginGame;
    private String userName;
    private List<Image> TileImages;
    private int stepCount;
    private static final int X_TILES=10;
    private static final int Y_TILES=10;
    public boolean finished;
    public boolean flag;

    private GameResultDao gameResultDao;

    public Tile[][] grid= new Tile[X_TILES][Y_TILES];

    @FXML
    private Label usernameLabel;

    @FXML
    private GridPane gameGrid;

    @FXML
    private Label stepLabel;

    @FXML
    private Label gameResult;

    @FXML
    private Button doneButton;

    public void checkFlag(ActionEvent actionEvent) {
        flag=!flag;
    }

    public static class Tile{
        private int x,y;
        private boolean hasBomb;
        private boolean isOpen = false;
        private boolean hasFlag = false;
        private int bombs=0;

        public Tile(int x,int y, boolean hasBomb,boolean hasFlag,boolean isOpen){
            this.x=x;
            this.y=y;
            this.hasBomb=hasBomb;
            this.hasFlag=hasFlag;
            this.isOpen=isOpen;
        }
    }

    /**
     * Fills the board with random mines, then if a tileis not
     * mine then sets the number of mines near
     */
    public void createContent(){
        for (int x=1; x<X_TILES; x++)
            for (int y=1; y<Y_TILES; y++) {
                Tile tile = new Tile(x, y, Math.random() < 0.1,false,false);
                grid[x][y] = tile;
            }
        for (int x=1; x<X_TILES; x++)
            for (int y=1; y<Y_TILES; y++) {
                Tile tile=grid[x][y];

                if(tile.hasBomb)
                    continue;
                grid[x][y].bombs=checkNeighbors(x,y);
            }
    }

    /**
     * Counts how many mines are near the given tile
     *
     * @param row the row of the tile
     * @param col the column of the tile
     * @return the number of mines near the tile
     */
    private int checkNeighbors(int row,int col){

        int num=0;

        if(row>1 && col>1)
            if(grid[row-1][col-1].hasBomb)
                num++;
        if(row>1)
            if(grid[row-1][col].hasBomb)
                num++;
        if(row>1 && col<9)
            if(grid[row-1][col+1].hasBomb)
                num++;
        if(col>1)
            if(grid[row][col-1].hasBomb)
                num++;
        if(col<9)
            if(grid[row][col+1].hasBomb)
                num++;
        if(row<9 && col>1)
            if(grid[row+1][col-1].hasBomb)
                num++;
        if(row<9)
            if(grid[row+1][col].hasBomb)
                num++;
        if(row<9 && col<9)
            if(grid[row+1][col+1].hasBomb)
                num++;


        return num;




    }

    private void drawGameState() {
        stepLabel.setText(String.valueOf(stepCount));


        for (int x = 1; x < X_TILES; x++) {
            for (int y = 1; y < Y_TILES; y++) {
                int number;
                if(grid[x][y].hasFlag)
                    number=8;
                else if(!grid[x][y].isOpen)
                    number=1;
                else if(grid[x][y].hasBomb)
                    number=0;
                else number=grid[x][y].bombs+2;
                ImageView view = (ImageView) gameGrid.getChildren().get(x * 10 + y);
                view.setImage(TileImages.get(number));
            }
        }
    }

    public void initdata(String userName) {
        this.userName = userName;
        usernameLabel.setText("Current user: " + this.userName);
    }

    @FXML
    public void initialize() {

        gameResultDao = GameResultDao.getInstance();

        createContent();

        beginGame = Instant.now();

        finished =false;

        stepCount = 0;




        TileImages = Arrays.asList(
                new Image(getClass().getResource("/pictures/mine.png").toExternalForm()),
                new Image(getClass().getResource("/pictures/tile.png").toExternalForm()),
                new Image(getClass().getResource("/pictures/tile0.png").toExternalForm()),
                new Image(getClass().getResource("/pictures/tile1.png").toExternalForm()),
                new Image(getClass().getResource("/pictures/tile2.png").toExternalForm()),
                new Image(getClass().getResource("/pictures/tile3.png").toExternalForm()),
                new Image(getClass().getResource("/pictures/tile4.png").toExternalForm()),
                new Image(getClass().getResource("/pictures/tile5.png").toExternalForm()),
                new Image(getClass().getResource("/pictures/flag.png").toExternalForm())
        );

        drawGameState();
    }

    /**
     * Checks whether the player has won
     * @return {@code true} if the player has won, {@code false} otherwise
     */
    public boolean hasWon(){
        boolean result=true;
        for(int x=1;x<X_TILES;x++)
            for(int y=1;y<Y_TILES;y++){
                Tile tile=grid[x][y];
                if(tile.hasBomb && !tile.hasFlag)
                    result=false;
                if(!tile.hasBomb && tile.hasFlag)
                    result=false;
                if(!tile.hasBomb && !tile.isOpen)
                    result=false;
            }
        return result;
    }

    /**
     * Reveal the whole board
     */
    public void revealAll(){

        for (int x = 1; x < X_TILES; x++)
            for (int y = 1; y < Y_TILES; y++) {
                grid[x][y].isOpen=true;
            }

    }

    /**
     * Reveals the given tile
     *
     * @param row the row of the tile to be revealed
     * @param col the column of the tile to be revealed
     */
    public void click(int row, int col){

        if(flag && !grid[row][col].isOpen) {
            if(!grid[row][col].hasFlag)
                log.info("Flagged tile at ({},{})",row,col);
            else log.info("Removed flag from tile at ({},{})",row,col);
            grid[row][col].hasFlag = !grid[row][col].hasFlag;
        }
        else {
            if (grid[row][col].isOpen || grid[row][col].hasFlag)
                return;
            grid[row][col].isOpen = true;
            log.info("Reveal tile at ({},{})",row,col);
            if (grid[row][col].hasBomb) {
                gameResult.setText("GAME OVER :(");
                log.info("Player {} found a mine in {} steps.", userName, stepCount);
                revealAll();
                finished = true;
            }


            if (grid[row][col].bombs == 0) {
                if (row > 1 && col > 1) {
                    click(row - 1, col - 1);
                }
                if (row > 1) {
                    click(row - 1, col);
                }
                if (row > 1 && col < 9) {
                    click(row - 1, col + 1);
                }
                if (col > 1) {
                    click(row, col - 1);
                }
                if (col < 9) {
                    click(row, col + 1);
                }
                if (row < 9 && col > 1) {
                    click(row + 1, col - 1);
                }
                if (row < 9) {
                    click(row + 1, col);
                }
                if (row < 9 && col < 9) {
                    click(row + 1, col + 1);
                }
            }
        }

        if (hasWon()){
            gameResult.setText("YOU WON!");
            log.info("Player {} solved the game in {} steps.", userName, stepCount);
            doneButton.setText("Finish");
            gameResultDao.persist(getResult());
            drawGameState();
            finished=true;
        }

    }


    public void tileClick(MouseEvent mouseEvent) {

        int clickedColumn = GridPane.getColumnIndex((Node)mouseEvent.getSource());
        int clickedRow = GridPane.getRowIndex((Node)mouseEvent.getSource());

        if(!finished) {

            stepCount++;

            click(clickedRow, clickedColumn);

            drawGameState();
        }
    }

    /**
     * Resets the game
     * @param actionevent is the rest button
     */
    public void resetGame(ActionEvent actionevent){
        createContent();
        stepCount=0;
        gameResult.setText("");
        finished=false;
        drawGameState();
        beginGame = Instant.now();
        log.info("Game reset.");
    }

    private GameResult getResult() {

        GameResult result = GameResult.builder()
                .player(userName)
                .solved(hasWon())
                .duration(Duration.between(beginGame, Instant.now()))
                .steps(stepCount)
                .build();
        return result;
    }

    public void finishGame(ActionEvent actionEvent) throws IOException {
        if (!hasWon()) {
            gameResultDao.persist(getResult());
        }

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/highscores.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        log.info("Finished game, loading highscores scene.");
    }

}
