
package ahiri.controllers;

import ahiri.Song;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


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
    
    @FXML
    private Button btnMore;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnLibrary;
    @FXML
    private Button btnCreatePlaylist;
    @FXML
    private Button btnFavourite;
    @FXML
    private ImageView selectedImg;
    @FXML
    private Label selectedSongName;
    @FXML
    private Label selectedArtist;
    @FXML
    private Label curTimeLabel;
    @FXML
    private Button previousButton;
    @FXML
    private ImageView imgPlayPause;
    @FXML
    private Button stopButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button repeatButton;
    @FXML
    private Slider musicSlider;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Button volumeButton;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ComboBox speedBox;
    
    private String speeds[]={"0.25","0.50","0.75","1","1.25","1.5","1.75","2"};
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colTitle.getStyleClass().add("bold");
        
        colSerial.setCellValueFactory(new PropertyValueFactory<Song,Integer>("serialNo"));
        colTitle.setCellValueFactory(new PropertyValueFactory<Song,String>("title"));
        colAlbum.setCellValueFactory(new PropertyValueFactory<Song,String>("album"));
        colDate.setCellValueFactory(new PropertyValueFactory<Song,String>("date"));
        colDuration.setCellValueFactory(new PropertyValueFactory<Song,String>("duration"));
        
        tableViewFavourite.setItems(songs);
        
        for(int i=0;i<speeds.length;i++){
                speedBox.getItems().add(speeds[i]);
        }
    }    

    @FXML
    private void navigateHome(MouseEvent event)throws IOException {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Home.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    private void previousMusic(ActionEvent event) {
    }

    @FXML
    private void playMusic(ActionEvent event) {
    }

    @FXML
    private void stopMusic(ActionEvent event) {
    }

    @FXML
    private void nextMusic(ActionEvent event) {
    }

    @FXML
    private void repeatMusic(ActionEvent event) {
    }

    @FXML
    private void changeSpeed(ActionEvent event) {
    }
    
}
