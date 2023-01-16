package Budgetflix.GUI.controller;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;
import Budgetflix.BLL.AlertManager;
import Budgetflix.BudgetFlix;
import Budgetflix.GUI.controller.cellFactory.MovieListCell;
import Budgetflix.GUI.model.Model;
import com.google.common.collect.Comparators;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.CheckComboBox;

import javax.naming.Binding;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();
    @FXML
    public Button nameSortButton, categorySortButton, userRatingSortButton, imdbRatingSortButton;
    @FXML
    private TextField searchBar;
    @FXML
    private ListView<Movie> moviesList;
    @FXML
    private Slider sliderUserRating, sliderIMDBRating;
    @FXML
    private CheckComboBox<Genre> genresDropDown = new CheckComboBox<Genre>(){};
    @FXML
    private Label lblUserValue, lblIMDBValue;

    private static final String SLIDER_STYLE_FORMAT =
            "-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
                    + "-slider-filled-track-color %1$f%%, -fx-base %1$f%%, -fx-base 100%%);";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        moviesList.setCellFactory(param -> new MovieListCell());

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems(), sliderIMDBRating.getValue(),sliderUserRating.getValue()));
        });

        genresDropDown.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Genre>) observable -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems(),sliderIMDBRating.getValue(),sliderUserRating.getValue()));
        });

        sliderUserRating.valueProperty().addListener((observable, oldValue, newValue) -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(), genresDropDown.getCheckModel().getCheckedItems(),
                    sliderIMDBRating.getValue(), sliderUserRating.getValue()));
            lblUserValue.setText(String.format(Locale.US,"%.2f",sliderUserRating.getValue()));
        });

        sliderIMDBRating.valueProperty().addListener((observable, oldValue, newValue) -> {
                moviesList.setItems(model.filterMovies(searchBar.getText(), genresDropDown.getCheckModel().getCheckedItems(),
                        sliderIMDBRating.getValue(), sliderUserRating.getValue()));
                lblIMDBValue.setText(String.format(Locale.US,"%.2f",sliderIMDBRating.getValue()));
        });

        //Slider changes colour when moved
        sliderUserRating.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = (sliderUserRating.getValue() - sliderUserRating.getMin()) / (sliderUserRating.getMax() - sliderUserRating.getMin()) * 100.0 ;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        }, sliderUserRating.valueProperty(), sliderUserRating.minProperty(), sliderUserRating.maxProperty()));

        sliderIMDBRating.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = ( sliderIMDBRating.getValue() -  sliderIMDBRating.getMin()) / ( sliderIMDBRating.getMax() -  sliderIMDBRating.getMin()) * 100.0 ;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        },  sliderIMDBRating.valueProperty(),  sliderIMDBRating.minProperty(),  sliderIMDBRating.maxProperty()));

        refreshMovieItems();
        refreshGenresItems();
        isOldMovieCheckTrue();
    }

    private void refreshMovieItems(){
        model.getMovieList();
        moviesList.setItems(model.getAllMovies());
    }
    private void refreshGenresItems(){
        model.getGenreList();
        genresDropDown.getItems().setAll(FXCollections.observableList(model.getAllGenres()));
    }

    private void isOldMovieCheckTrue(){
        if(!model.getOldMovies().isEmpty()){
            try {
                openNewWindow("/Budgetflix/GUI/view/OldMovieView.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void btnAddMovieAction(ActionEvent actionEvent) throws IOException {
        openNewWindow("/Budgetflix/GUI/view/NewMovieView.fxml");
    }

    @FXML
    private void btnEditMovieAction(ActionEvent actionEvent) throws IOException {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null)
            new Alert(Alert.AlertType.ERROR, "Please, select a movie to edit").showAndWait();
        else{
            model.setMovieToEdit(movie);
            FXMLLoader fxmlLoader = openNewWindow("/Budgetflix/GUI/view/NewMovieView.fxml");
            NewMovieController newMovieController = fxmlLoader.getController();
            newMovieController.setIsEditing();
        }
    }

    @FXML
    private void btnDeleteMovieAction(ActionEvent actionEvent) {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null){
            alertManager.getAlert("ERROR", "Please, select a movie to delete!", actionEvent).showAndWait();
        }
        else{
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this movie?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                model.deleteMovie(movie);
            }
        }
        refreshMovieItems();
    }

    private FXMLLoader openNewWindow(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BudgetFlix.class.getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Budgetflix");
        stage.getIcons().add(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/budgetflixIcon.png"))));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setAlwaysOnTop(true);
        stage.centerOnScreen();
        stage.show();
        Window window = scene.getWindow();
        window.setOnHiding(event -> {
            refreshGenresItems();
            refreshMovieItems();
        });
        return fxmlLoader;
    }

    @FXML
    private void playMovie(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {
            Movie movie = moviesList.getSelectionModel().getSelectedItem();
            File mediaFile = new File(movie.getFileLink());
            try {
                //Desktop.getDesktop().open(mediaFile);
            } catch (Exception ex){
                alertManager.getAlert("ERROR", "File not found!\nCannot play the selected movie.", mouseEvent).showAndWait();
            }
        }
    }

    @FXML
    private void btnAddGenreAction(ActionEvent actionEvent) throws IOException {
        openNewWindow("/Budgetflix/GUI/view/NewGenreView.fxml");
    }

    @FXML
    private void btnDeleteGenreAction(ActionEvent actionEvent) {
        ArrayList<Genre> genres = new ArrayList<>(genresDropDown.getCheckModel().getCheckedItems());
        if (genres.size() == 0)
            alertManager.getAlert("ERROR", "Please, select a genre to delete!", actionEvent).showAndWait();
        else{
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this genre?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (Genre genre : genres){
                    model.deleteGenre(genre);
                    genresDropDown.getItems().clear();
                }
                refreshGenresItems();
            }
        }
    }

    public void btnNameSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getName));
    }

    public void btnCategorySortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getGenresToString));
    }

    public void btnUserRatingSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getUserRating));
    }

    public void btnImdbRatingSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getImdbRating));
    }


    public void sortData(Comparator<Movie> movieComparator)
    {
        var listOfMovies = moviesList.getItems();
        boolean sorted = Comparators.isInOrder(listOfMovies,movieComparator);
        if(!sorted)
            Collections.sort(listOfMovies,movieComparator);
        else
            Collections.sort(listOfMovies,movieComparator.reversed());
        moviesList.setItems(listOfMovies);
    }
}