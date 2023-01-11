package GUI.model;

import BE.Genre;
import BE.Movie;
import BLL.LogicManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private static Model instance=null;
    LogicManager logicManager = new LogicManager();
    Movie movieToEdit;

    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        } return instance;
    }

    public boolean editMovie(Movie movie) {
        return logicManager.editMovie(new Movie(movieToEdit.getId(), movie.getName(), movie.getFileLink(), movie.getLastView(), movie.getImdbRating(), movie.getUserRating(), movie.getGenres()));
    }

    public ObservableList<Movie> searchMovies(String query, ObservableList<Genre> genres)
    {
        ObservableList<Movie> movies = getMovieList();
        List<Movie> filtered = new ArrayList<>();

        if (query.equals("")){
            filtered.addAll(movies);
        }

        else {
            for (Movie m : movies) {
                if (m.getName().toLowerCase().contains(query.toLowerCase()))
                    filtered.add(m);
            }
        }

        /*
        for (Genre genre:genres){
            //TODO actually do something useful
        }*/

        return FXCollections.observableArrayList(filtered);
    }


    public boolean createMovie(Movie movie) {
        return logicManager.addMovie(movie);
    }

    public void deleteMovie(Movie movie){
        logicManager.deleteMovie(movie);
    }

    public void setMovieToEdit(Movie movie){
        this.movieToEdit = movie;
    }

    public Movie getMovieToEdit() {
        return movieToEdit;
    }

    public ObservableList<Movie> getMovieList(){
        return logicManager.getMovieList();
    }

    public List<Movie> getMoviesInGenre(Genre genre){
        return logicManager.getMoviesInGenre(genre);
    }

    public List<Genre> getAllGenresFromMovie(Movie movie){
        return logicManager.getAllGenresFromMovie(movie);
    }

    public void addGenre(String genre){
        logicManager.addGenre(genre);
    }

    public void deleteGenre(Genre genre){
        logicManager.deleteGenre(genre);
    }
}
