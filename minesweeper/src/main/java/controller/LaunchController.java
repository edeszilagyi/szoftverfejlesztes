package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LaunchController {

    @FXML
    private TextField usernameTextfield;

    @FXML
    private Label errorLabel;


    public void startEasy(ActionEvent actionEvent) throws IOException {
        if (usernameTextfield.getText().isEmpty()) {
            errorLabel.setText("* Username is empty!");
        }else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/easygame.fxml"));
                Parent root = fxmlLoader.load();
                fxmlLoader.<EasyGameController>getController().initdata(usernameTextfield.getText());
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            }

    }


}
