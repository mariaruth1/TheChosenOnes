package Budgetflix.GUI.controller;

import Budgetflix.GUI.model.Model;
import Budgetflix.BLL.AlertManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class NewGenreController {
    private Model model = Model.getInstance();
    @FXML private TextField txtGenreName;

    /**
     * Adds a new Genre to the list and the database.
     */
    @FXML
    private void btnSaveAction(ActionEvent actionEvent) {
        String genreName = txtGenreName.getText().trim();
        if (genreName.isEmpty()){
            txtGenreName.setPromptText("Field must not be empty!");
        }
        else{
            if (model.addGenre(genreName).isEmpty()){
                Node node = (Node) actionEvent.getSource();
                node.getScene().getWindow().hide();
            }
            else
                AlertManager.getInstance().getAlert("ERROR", "Genre already exists!", actionEvent).showAndWait();
        }
    }

    /**
     * Closes the window
     */
    @FXML
    private void btnCancelAction(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }
}
