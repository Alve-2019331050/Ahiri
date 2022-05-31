
package ahiri.controllers;

import ahiri.Song;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class FavouriteController implements Initializable {

    @FXML
    private TableView<Song> tableViewFavourite;
    @FXML
    private TableColumn<Song, Integer> colSerial;
    @FXML
    private TableColumn<Song, String> colTitle;
    @FXML
    private TableColumn<Song, String> colAlbum;
    @FXML
    private TableColumn<Song, String> colDate;
    @FXML
    private TableColumn<Song, String> colDuration;

    
    public ObservableList<Song> songs = FXCollections.observableArrayList(
            new Song(1,"Jao Pakhi Bolo Tare","Monpura","Apr 6,2022","3:28","a","a"),
            new Song(2,"Onno Aloy","Onno Aloy","Apr 4,2022","4:45","b","b"),
            new Song(3,"Amar Protichchobi","Biborton","Feb 15,2022","3:53","c","c")
    );
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colTitle.getStyleClass().add("bold");
        
        colSerial.setCellValueFactory(new PropertyValueFactory<Song,Integer>("serialNo"));
        colTitle.setCellValueFactory(new PropertyValueFactory<Song,String>("title"));
        colAlbum.setCellValueFactory(new PropertyValueFactory<Song,String>("album"));
        colDate.setCellValueFactory(new PropertyValueFactory<Song,String>("date"));
        colDuration.setCellValueFactory(new PropertyValueFactory<Song,String>("duration"));
        
        tableViewFavourite.setItems(songs);
    }    
    
}
