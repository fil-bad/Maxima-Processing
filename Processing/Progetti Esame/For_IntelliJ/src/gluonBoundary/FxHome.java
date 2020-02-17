package gluonBoundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;


public class FxHome implements Initializable {
    //=================================================================
    //Top object
    @FXML
    private Button button;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Comandi da chiamare all'avvio della finestra
    }


    public void handle(ActionEvent actionEvent) {
        System.out.println("Pulsante cliccato");
        button.setText("Ora mi hai premuto");
    }


}
